package com.picnic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.picnic.data.Artwork;
import com.picnic.data.Picnic;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ArtworkAdapter extends RecyclerView.Adapter<ArtworkAdapter.ViewHolder> {

    private List<Artwork> data;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private OnItemLongClickListener mLongClickListener;
    private Context appContext;
    private Context mContext;
    private String source;
    private DatabaseReference mDatabase;

    String TAG = "Adapter Debugging Tag";

    // data is passed into the constructor
    ArtworkAdapter(Context context, List<Artwork> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.data = data;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {
        View view = mInflater.inflate(R.layout.artwork_cards, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {

        if(position < data.size()) {

            final ViewHolder holder = vh;
            String name = data.get(position).title;
            String description = data.get(position).description;
            holder.name.setText(name);
            holder.description.setText(description);

            if (position % 3 == 0) {
                holder.card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.cardcolororange));
            } else if (position % 3 == 1) {
                holder.card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.cardcolorgreen));
            } else {
                holder.card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.cardcolorblue));
            }

            try {
                URL url = new URL(data.get(position).imageURL);
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                holder.artwork.setImageBitmap(image);
            } catch(IOException e) {
                //System.out.println(e);
            }

            final int p = position;

            // TODO: Add Onclick listener

            /***
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PicnicActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //intent.putExtra("PicnicID", data.get(p).id);
                    mContext.startActivity(intent);
                }
            });
             ***/


            mDatabase.child("users").child(data.get(position).artist).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String hostname = dataSnapshot.child("displayName").getValue().toString();
                    holder.artist.setText(hostname);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return data.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView artist;
        TextView description;
        ImageView artwork;
        CardView card;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            artist = itemView.findViewById(R.id.artist);
            artwork = itemView.findViewById(R.id.artwork);
            card = itemView.findViewById(R.id.card);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

    }

    // convenience method for getting data at click position
    //String getItem(int id) {
    //    return data.get(id).name;
    //}

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    void setLongClickListener(OnItemLongClickListener longClickListener){
        this.mLongClickListener = longClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClicked(View view, int position);
    }

}