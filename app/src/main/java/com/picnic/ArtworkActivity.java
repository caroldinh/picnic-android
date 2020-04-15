package com.picnic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.picnic.data.Artwork;
import com.picnic.data.Critique;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ArtworkActivity extends AppCompatActivity  {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String name, email, uid;

    private boolean isFABOpen;

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    ListView drawerList;
    FloatingActionButton add;

    NavDrawerItem[] navRows;

    LinearLayout header;

    RecyclerView critiques;
    String TAG = "PicnicActivity Debugging Tag";

    String picnicID;
    String artID;

    TextView title, artist, description, timestamp, feedback;
    ImageView artwork;
    ProgressBar prog;

    LinearLayout layout;

    boolean done = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        picnicID = getIntent().getStringExtra("PicnicID");
        artID = getIntent().getStringExtra("ArtID");

        setContentView(R.layout.activity_artworkpage);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prog = findViewById(R.id.progressBar);
        prog.setVisibility(View.VISIBLE);

        header = findViewById(R.id.header);

        title = findViewById(R.id.title);
        artist = findViewById(R.id.artist);
        description = findViewById(R.id.description);
        timestamp = findViewById(R.id.timestamp);
        feedback = findViewById(R.id.feedback);

        artwork = findViewById(R.id.imageView);
        layout = findViewById(R.id.container_body);
        layout.setVisibility(View.GONE);

        drawerLayout = findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Loading...");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        drawerList = findViewById(R.id.leftDrawer);

        navRows = new NavDrawerItem[4];
        navRows[0] = new NavDrawerItem(R.drawable.picnics, "My Picnics");
        navRows[1] = new NavDrawerItem(R.drawable.settings, "Settings");
        navRows[2] = new NavDrawerItem(R.drawable.about, "About");
        navRows[3] = new NavDrawerItem(R.drawable.signout, "Logout");
        drawerList.setAdapter(new NavDrawerAdapter(this, R.layout.nav_row, navRows));

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: do your stuff

                switch(position)
                {
                    case 0:
                        //Toast.makeText(MainActivity.this, "My Picnics",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ArtworkActivity.this, MainActivity.class));
                        finish();
                        break;
                    case 1:
                        //Toast.makeText(MainActivity.this, "Settings",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        //Toast.makeText(MainActivity.this, "About",Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        //Toast.makeText(MainActivity.this, "Logout",Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        startActivity(new Intent(ArtworkActivity.this, Register.class));
                        finish();
                        break;
                    default:
                }

            }
        });

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        mAuth = FirebaseAuth.getInstance();

        add = findViewById(R.id.fab);
        add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(ArtworkActivity.this, CreateCritique.class);
                    intent.putExtra("PicnicID", picnicID);
                    intent.putExtra("ArtID", artID);
                    intent.putExtra("Feedback", feedback.getText().toString());
                    startActivity(intent);
                }
        });

        critiques = findViewById(R.id.critiques);

    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user = mAuth.getCurrentUser();
        // updateUI(currentUser);
        if (user != null) {
            // Name, email address, and profile photo Url
            name = user.getDisplayName();
            email = user.getEmail();
            //Uri photoUrl = user.getPhotoUrl();

            uid = user.getUid();
        }

        mDatabase.child("picnics").child(picnicID).child("artworks").child(artID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String t = dataSnapshot.child("title").getValue().toString();
                String d = dataSnapshot.child("description").getValue().toString();
                String a = dataSnapshot.child("artist").getValue().toString();
                String ts = dataSnapshot.child("timestamp").getValue().toString();
                String fb = dataSnapshot.child("feedback").getValue().toString();
                String url = dataSnapshot.child("imageURL").getValue().toString();

                getSupportActionBar().setTitle(t);

                title.setText(t);
                description.setText(d);
                timestamp.setText(ts);
                feedback.setText(fb);

                try {
                    Bitmap image = new ArtworkActivity.getImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url).get();
                    artwork.setImageBitmap(image);
                } catch(Exception e){ }

                mDatabase.child("users").child(a).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String displayName = dataSnapshot.child("displayName").getValue().toString();
                        artist.setText(displayName);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                layout.setVisibility(View.VISIBLE);
                prog.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        critiques.setLayoutManager(new LinearLayoutManager(this));

        // TODO: Add critiques adapter


        mDatabase.child("picnics").child(picnicID).child("artworks").child(artID).child("critiques").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Critique> critiqueList = new ArrayList<>();
                for(int i = 0; i < dataSnapshot.getChildrenCount(); i++){
                    String bread1 = dataSnapshot.child("" + i).child("bread1").getValue().toString();
                    String bread2 = dataSnapshot.child("" + i).child("bread2").getValue().toString();
                    String critiquer = dataSnapshot.child("" + i).child("critiquer").getValue().toString();
                    String sandwich = dataSnapshot.child("" + i).child("sandwich").getValue().toString();
                    String timestamp = dataSnapshot.child("" + i).child("timestamp").getValue().toString();

                    Log.d(TAG, bread1);

                    Critique c = new Critique(critiquer, bread1, sandwich, bread2);
                    critiqueList.add(c);
                }

                Log.d(TAG, ""+ critiqueList.size());
                CritiqueAdapter adapter = new CritiqueAdapter(getApplicationContext(), critiqueList);
                critiques.setAdapter(adapter);
                critiques.setVisibility(View.VISIBLE);
                done = true;
                prog.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
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
