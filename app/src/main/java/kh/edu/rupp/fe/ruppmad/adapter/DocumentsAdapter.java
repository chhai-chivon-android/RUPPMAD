package kh.edu.rupp.fe.ruppmad.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import kh.edu.rupp.fe.ruppmad.AppSingleton;
import kh.edu.rupp.fe.ruppmad.R;

/**
 * RUPPMAD
 * Created by leapkh on 4/26/17.
 */

public class DocumentsAdapter extends RecyclerView.Adapter<DocumentsAdapter.DocumentViewHolder> {

    private Document[] documents;
    private RecyclerViewItemClickListener recyclerViewItemClickListener;
    private Context context;

    public DocumentsAdapter(Context context, Document[] documents){
        this.context = context;
        this.documents = documents;
    }

    public void setDocuments(Document[] documents) {
        this.documents = documents;
        notifyDataSetChanged();
    }

    public Document getDocument(int position){
        return documents[position];
    }

    public void setRecyclerViewItemClickListener(RecyclerViewItemClickListener recyclerViewItemClickListener) {
        this.recyclerViewItemClickListener = recyclerViewItemClickListener;
    }

    @Override
    public DocumentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View holderView = LayoutInflater.from(context).inflate(R.layout.viewholder_document, parent, false);
        DocumentViewHolder viewHolder = new DocumentViewHolder(holderView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DocumentViewHolder holder, int position) {

        Document document = documents[position];
        holder.txtTitle.setText(document.getTitle());
        holder.txtSize.setText(document.getFormatSize());
        holder.txtHits.setText("Hits: " + document.getHits());
        holder.imgThumbnail.setImageUrl(document.getThumbnailUrl(), AppSingleton.getInstance(context).getImageLoader());
        Log.d("rupp", "Image url: " + document.getThumbnailUrl());

    }

    @Override
    public int getItemCount() {
        return documents.length;
    }

    class DocumentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private NetworkImageView imgThumbnail;
        private TextView txtTitle;
        private TextView txtSize;
        private TextView txtHits;

        public DocumentViewHolder(View itemView) {
            super(itemView);

            imgThumbnail = (NetworkImageView)itemView.findViewById(R.id.img_thumbnail);
            txtTitle = (TextView)itemView.findViewById(R.id.txt_title);
            txtSize = (TextView)itemView.findViewById(R.id.txt_size);
            txtHits = (TextView)itemView.findViewById(R.id.txt_hits);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if(recyclerViewItemClickListener != null){
                recyclerViewItemClickListener.onRecyclerViewItemClick(getAdapterPosition());
            }
        }
    }

}
