package kh.edu.rupp.fe.ruppmad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kh.edu.rupp.fe.ruppmad.db.DbManager;

import static android.graphics.BitmapFactory.decodeStream;

public class ProfileActivity extends AppCompatActivity {

    private final int GALLERY_REQUEST_CODE = 1;
    private final int CAMERA_REQUEST_CODE = 2;

    private ImageView imgProfile;

    private Uri capturedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        imgProfile = (ImageView) findViewById(R.id.img_profile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();
                Bitmap bitmap = loadFitImage(selectedImageUri, imgProfile.getWidth(), imgProfile.getHeight());
                imgProfile.setImageBitmap(bitmap);
                uploadProfileImageToServer(selectedImageUri);
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                /*
                // Loading thumbnail from captured image
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                imgProfile.setImageBitmap(thumbnail);
                */

                // Load full image
                Bitmap bitmap = loadFitImage(capturedImageUri, imgProfile.getWidth(), imgProfile.getHeight());
                imgProfile.setImageBitmap(bitmap);
                uploadProfileImageToServer(capturedImageUri);
            }
        }
    }

    public void onLoginHistoryClick(View view) {
        DbManager dbManager = new DbManager(this);
        long[] histories = dbManager.getAllLoginHistory();
        for (long history : histories) {
            Log.d("rupp", "History: " + formatDate(history));
        }
    }

    private String formatDate(long timeStamp) {
        Date date = new Date(timeStamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        return simpleDateFormat.format(date);
    }


    public void onProfileImageClick(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.change_profile_image);
        dialog.setMessage(R.string.msg_change_profile_image);
        dialog.setPositiveButton(R.string.gallery, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });
        dialog.setNegativeButton(R.string.camera, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // Define Uri for storing full image
                File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                String fileName = System.currentTimeMillis() + ".jpg";
                File imageFile = new File(directory, fileName);
                capturedImageUri = FileProvider.getUriForFile(ProfileActivity.this, "kh.edu.rupp.fe.ruppmad.FILE_PROVIDER", imageFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);

                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        });
        dialog.show();
    }

    private Bitmap loadFitImage(Uri imageUri, int targetWidth, int targetHeight) {

        // Get image's size
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            decodeStream(getContentResolver().openInputStream(imageUri), null, options);
            int imageWidth = options.outWidth;
            int imageHeight = options.outHeight;

            // Caculate scale factor
            int scaleFactor = Math.min(imageWidth / targetWidth, imageHeight / targetHeight);

            // Modify decode option
            options.inJustDecodeBounds = false;
            options.inSampleSize = scaleFactor;

            return BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

    private void uploadProfileImageToServer(Uri imageUri) {
        Bitmap bitmap = loadFitImage(imageUri, 512, 512);

        // Convert image to base64 string
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        final String base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);

        // Upload base64 string to Server
        String uploadProfileUrl = "http://10.0.2.2/test/ruppmad-api/upload-profile.php";
        StringRequest request = new StringRequest(Request.Method.POST, uploadProfileUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(ProfileActivity.this, "Upload profile image success", Toast.LENGTH_LONG).show();
                Log.d("rupp", "Success: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProfileActivity.this, "Upload profile image error.", Toast.LENGTH_LONG).show();
                Log.d("rupp", "Upload image error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("_image_profile", base64String);
                return params;
            }
        };
        AppSingleton.getInstance(this).getRequestQueue().add(request);
    }

}
















