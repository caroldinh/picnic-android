package com.picnic;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ContextMenu.ContextMenuInfo;
import android.content.ClipboardManager;


import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.picnic.data.Picnic;

import java.util.ArrayList;
import java.util.List;

public class PicnicAdapter extends RecyclerView.Adapter<PicnicAdapter.ViewHolder> {

    private List<Picnic> data;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private OnItemLongClickListener mLongClickListener;
    private Context appContext;
    private Context mContext;
    private String source;
    private DatabaseReference mDatabase;

    String TAG = "Adapter Debugging Tag";

    // data is passed into the constructor
    PicnicAdapter(Context context, List<Picnic> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.data = data;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {
        View view = mInflater.inflate(R.layout.picnic_cards, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {

        if(position < data.size()) {
            final ViewHolder holder = vh;
            String name = data.get(position).name;
            String description = data.get(position).description;
            holder.name.setText(name);
            holder.description.setText(description);

            if(position % 3 == 0){
                holder.card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.cardcolororange));
            } else if(position % 3 == 1){
                holder.card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.cardcolorgreen));
            } else{
                holder.card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.cardcolorblue));
            }

            final int p = position;
            holder.card.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent =  new Intent(mContext, PicnicActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("PicnicID", data.get(p).id);
                    mContext.startActivity(intent);
                }
            });

            Log.d(TAG, data.get(position).hostUID);

            mDatabase.child("users").child(data.get(position).hostUID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String hostname = dataSnapshot.child("displayName").getValue().toString();
                    holder.hostname.setText(hostname);
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
        TextView hostname;
        TextView description;
        ImageView hostIcon;
        ImageView icon;
        CardView card;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            hostname = itemView.findViewById(R.id.hostname);
            hostIcon = itemView.findViewById(R.id.hosticon);
            icon = itemView.findViewById(R.id.icon);
            hostIcon.setImageResource(R.drawable.profile);
            icon.setImageResource(R.drawable.picnics);
            itemView.setOnClickListener(this);
            card = itemView.findViewById(R.id.card);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

        /***
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            if(source != "PoetPage") {
                MenuItem Copy = menu.add(Menu.NONE, 1, 1, "Copy");
                Copy.setOnMenuItemClickListener(onEditMenu);
                MenuItem Delete = menu.add(Menu.NONE, 2, 2, "Remove");
                Delete.setOnMenuItemClickListener(onEditMenu);
            }
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int position = getAdapterPosition();

                switch (item.getItemId()) {
                    case 1:
                        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Content", headData.get(position));
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(appContext, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                        break;

                    case 2:

                        headData.remove(position);
                        notifyItemRemoved(position);
                        Log.d("RV Debugging Tag", "Removed " + getAdapterPosition());

                        switch(source){
                            case "FavPoems":
                                book.removePoem(position);
                                break;
                            case "SavedQuotes":
                                book.removeLine(position);
                                break;
                            case "FollowedPoets":
                                book.removePoet(position);
                                break;
                            default:
                                break;
                        }

                        break;
                }
                return true;
            }
        };
         ***/

    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return data.get(id).name;
    }

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