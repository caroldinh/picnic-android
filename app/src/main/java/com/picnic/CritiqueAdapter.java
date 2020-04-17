package com.picnic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.picnic.data.Artwork;
import com.picnic.data.Critique;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class CritiqueAdapter extends RecyclerView.Adapter<CritiqueAdapter.ViewHolder> {

    private List<Critique> data;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private OnItemLongClickListener mLongClickListener;
    private Context appContext;
    private Context mContext;
    private String source;
    private DatabaseReference mDatabase;
    private String picnicID;
    private String ownWork;

    String TAG = "Adapter Debugging Tag";

    // data is passed into the constructor
    CritiqueAdapter(Context context, List<Critique> data, boolean ownWork) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.data = data;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        if(ownWork) {
            this.ownWork = "true";
        } else {
            this.ownWork = "false";
        }
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {
        View view = mInflater.inflate(R.layout.critiqueadapter, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {

        if(position < data.size()) {

            final ViewHolder holder = vh;
            String bread1 = data.get(position).bread1;
            holder.preview.setText(bread1);


            final int p = position;

            // TODO: Add Onclick listener

            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CritiqueActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("critiquer", holder.critiquer.getText());
                    intent.putExtra("bread1", data.get(p).bread1);
                    intent.putExtra("sandwich", data.get(p).sandwich);
                    intent.putExtra("bread2", data.get(p).bread2);
                    intent.putExtra("timestamp", data.get(p).timestamp);
                    intent.putExtra("crUID", data.get(p).critiquer);
                    intent.putExtra("ownWork", ownWork);
                    mContext.startActivity(intent);
                }
            });

            mDatabase.child("users").child(data.get(position).critiquer).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String hostname = dataSnapshot.child("displayName").getValue().toString();
                    holder.critiquer.setText(hostname);
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
        TextView preview;
        TextView critiquer;
        CardView card;

        ViewHolder(View itemView) {
            super(itemView);
            preview = itemView.findViewById(R.id.preview);
            critiquer = itemView.findViewById(R.id.critiquer);
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


    public class getImage extends AsyncTask<String , Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return image;
            } catch(IOException e) {
                //System.out.println(e);
            }

            return null;

        }
    }
}
