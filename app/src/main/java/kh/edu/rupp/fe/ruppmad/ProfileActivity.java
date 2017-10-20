package kh.edu.rupp.fe.ruppmad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import kh.edu.rupp.fe.ruppmad.db.DbManager;

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
                try {
                    Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    imgProfile.setImageBitmap(selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                /*
                // Loading thumbnail from captured image
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                imgProfile.setImageBitmap(thumbnail);
                */

                // Load full image
                try {
                    Bitmap capturedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), capturedImageUri);
                    imgProfile.setImageBitmap(capturedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

}
















