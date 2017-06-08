package kh.edu.rupp.fe.ruppmad;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kh.edu.rupp.fe.ruppmad.adapter.Document;
import kh.edu.rupp.fe.ruppmad.adapter.DocumentsAdapter;
import kh.edu.rupp.fe.ruppmad.adapter.RecyclerViewItemClickListener;

/**
 * RUPPMAD
 * Created by leapkh on 7/3/17.
 */

public class DocumentsFragment extends Fragment implements RecyclerViewItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView rclDocuments;
    private DocumentsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_documents, container, false);

        // Swife to refresh layout
        swipeRefreshLayout = (SwipeRefreshLayout)fragmentView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        // Reference to RecyclerView
        rclDocuments = (RecyclerView)fragmentView.findViewById(R.id.rcl_documents);

        // Layout Manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rclDocuments.setLayoutManager(layoutManager);

        // Adapter
        Document[] emptyDocs = new Document[0];
        adapter = new DocumentsAdapter(getActivity(), emptyDocs);
        adapter.setRecyclerViewItemClickListener(this);
        rclDocuments.setAdapter(adapter);

        loadDocuments();

        return fragmentView;
    }

    // Temporary List of Documents
    private void loadDocuments(){
        Log.d("rupp", "Start loading documents");
        swipeRefreshLayout.setRefreshing(true);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = "http://10.0.2.2/test/ruppmad-api/";
        final JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // Convert response to Document objects
                // Apply document objects to adapter
                Log.d("rupp", "Loaded documents success");
                Document[] documents = new Document[response.length()];
                boolean isError = false;
                Gson gson = new Gson();
                for(int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Document document = gson.fromJson(jsonObject.toString(), Document.class);
                        documents[i] = document;
                    } catch (JSONException e) {
                        Log.d("rupp", "Error converting documents");
                        isError = true;
                    }
                }
                if(isError){
                    Toast.makeText(getActivity(), "Error while loading docuements", Toast.LENGTH_LONG).show();
                }
                swipeRefreshLayout.setRefreshing(false);
                adapter.setDocuments(documents);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error while loading docuements", Toast.LENGTH_LONG).show();
                Log.d("rupp", "Error load docs: " + error.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        requestQueue.add(request);
    }

    @Override
    public void onRecyclerViewItemClick(int position) {
        Log.d("rupp", "Recycler item click: " + position);
        Document document = adapter.getDocument(position);
        document.setHits(document.getHits()+1);
        adapter.notifyDataSetChanged();
        Gson gson = new Gson();
        String serializedDocument = gson.toJson(document);
        Intent intent = new Intent(getActivity(), DocumentActivity.class);
        intent.putExtra("serializedDocument", serializedDocument);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        Log.d("rupp", "On refresh");
        // Check update from server
        loadDocuments();
    }
}
