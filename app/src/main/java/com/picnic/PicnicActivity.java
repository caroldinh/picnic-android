package com.picnic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.picnic.data.Artwork;
import com.picnic.data.Picnic;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PicnicActivity extends AppCompatActivity  {

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

    RecyclerView artGallery;
    String TAG = "PicnicActivity Debugging Tag";

    String picnicID;

    TextView picnicName;
    TextView picnicDescription;
    TextView picnicCode;

    ProgressBar prog;

    boolean done = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        picnicID = getIntent().getStringExtra("PicnicID");

        setContentView(R.layout.activity_picnicpage);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Loading...");

        prog = findViewById(R.id.progressBar);
        prog.setVisibility(View.VISIBLE);

        header = findViewById(R.id.header);

        picnicName = findViewById(R.id.name);
        picnicDescription = findViewById(R.id.description);
        picnicCode = findViewById(R.id.code);

        drawerLayout = findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        /***
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
         ***/

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
                        startActivity(new Intent(PicnicActivity.this, MainActivity.class));
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
                        Intent i = new Intent(PicnicActivity.this,Register.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);
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

                    mDatabase.child("picnics").child(picnicID).child("members").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            long contributions = (long)dataSnapshot.child("contributions").getValue();
                            long critiques = (long)dataSnapshot.child("critiques").getValue();
                            if(contributions > critiques) {
                                Toast.makeText(getApplicationContext(), "You must add another critique before you can upload artwork!", Toast.LENGTH_SHORT).show();
                            } else{
                                Intent intent = new Intent(PicnicActivity.this, UploadArtwork.class);
                                intent.putExtra("PicnicID", picnicID);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
        });

        artGallery = findViewById(R.id.recyclerView);

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
        } else{
            mAuth.signOut();
            Intent i = new Intent(PicnicActivity.this,Register.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        }

        mDatabase.child("picnics").child(picnicID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String description = dataSnapshot.child("description").getValue().toString();
                String hostUID = dataSnapshot.child("hostUID").getValue().toString();
                String code = dataSnapshot.child("id").getValue().toString();

                getSupportActionBar().setTitle(name);

                if(hostUID.equals(uid)){
                    picnicCode.setVisibility(View.VISIBLE);
                    picnicCode.setText(code);
                } else{
                    picnicCode.setVisibility(View.GONE);
                }

                picnicName.setText(name);
                picnicDescription.setText(description);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        artGallery.setLayoutManager(new LinearLayoutManager(this));

        // TODO: Add art gallery adapter

        mDatabase.child("picnics").child(picnicID).child("artworks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Artwork> artworks = new ArrayList<>();
                for(int i = 0; i < dataSnapshot.getChildrenCount(); i++){
                    String imageURL = dataSnapshot.child("" + i).child("imageURL").getValue().toString();
                    String title = dataSnapshot.child("" + i).child("title").getValue().toString();
                    String description = dataSnapshot.child("" + i).child("description").getValue().toString();
                    String artist = dataSnapshot.child("" + i).child("artist").getValue().toString();
                    String feedback = dataSnapshot.child("" + i).child("feedback").getValue().toString();

                    Artwork a = new Artwork(title, artist, imageURL, description, feedback);
                    artworks.add(a);
                }

                if(dataSnapshot.getChildrenCount() - artworks.size() == 0) {
                    ArtworkAdapter adapter = new ArtworkAdapter(getApplicationContext(), artworks, picnicID);
                    artGallery.setAdapter(adapter);
                    done = true;
                    prog.setVisibility(View.GONE);
                    header.setVisibility(View.VISIBLE);
                }

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

}
