package kh.edu.rupp.fe.ruppmad;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import kh.edu.rupp.fe.ruppmad.adapter.Document;

public class DocumentActivity extends AppCompatActivity {

    private ImageView imgDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        TextView txtTitle = (TextView)findViewById(R.id.txt_title);
        TextView txtSize = (TextView)findViewById(R.id.txt_size);
        TextView txtHits = (TextView)findViewById(R.id.txt_hits);
        imgDocument = (ImageView)findViewById(R.id.img_document);

        String serializedDocument = getIntent().getStringExtra("serializedDocument");
        Gson gson = new Gson();
        Document document = gson.fromJson(serializedDocument, Document.class);

        txtTitle.setText(document.getTitle());
        txtSize.setText("Size: " + document.getSize() + " M");
        txtHits.setText("Hits: " + document.getHits());

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(document.getTitle());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        processSaveHitHistory(document.getId());

        loadDocumentImageFromServer(document.getThumbnailUrl());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void processSaveHitHistory(int documentId){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject params = new JSONObject();
        String serverUrl = "http://10.0.2.2/test/ruppmad-api/save-document-hits.php?document_id=" + documentId;
        JsonObjectRequest request = new JsonObjectRequest(serverUrl, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("rupp", "Save hits history success");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("rupp", "Save hits history fail: " + error.getMessage());
            }
        });
        requestQueue.add(request);
    }

    private void loadDocumentImageFromServer(String imageUrl){
        ImageRequest imageRequest = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imgDocument.setImageBitmap(response);
            }
        }, 512, 512, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DocumentActivity.this, "Fail to load image from server", Toast.LENGTH_LONG).show();
            }
        });
        AppSingleton.getInstance(this).getRequestQueue().add(imageRequest);
    }

}
