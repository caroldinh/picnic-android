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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.picnic.data.Picnic;

import java.util.ArrayList;

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

    RecyclerView artGallery;
    String TAG = "PicnicActivity Debugging Tag";

    String picnicID;

    TextView picnicName;
    TextView picnicDescription;
    TextView picnicCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        picnicID = getIntent().getStringExtra("PicnicID");

        setContentView(R.layout.activity_picnicpage);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                        startActivity(new Intent(PicnicActivity.this, Register.class));
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
                    // TODO: Add "Upload Artwork" page
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

        /***

        final ArrayList<String> picnicIds = new ArrayList<>();

        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "" + dataSnapshot.child("Hosted").getChildrenCount());
                for(int i = 0; i < dataSnapshot.child("Hosted").getChildrenCount(); i++) {
                    picnicIds.add(((ArrayList) dataSnapshot.child("Hosted").getValue()).get(i).toString());
                }
                for(int i = 0; i < dataSnapshot.child("Joined").getChildrenCount(); i++) {
                    picnicIds.add(((ArrayList) dataSnapshot.child("Joined").getValue()).get(i).toString());
                }
                final ArrayList<Picnic> data = new ArrayList<>();

                //Picnic p = new Picnic("Test Picnic", "Test Description", uid);
                //data.add(p);

                for(int i = 0; i < picnicIds.size(); i++){
                    Log.d(TAG, picnicIds.get(i));
                    mDatabase.child("picnics").child(picnicIds.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String[] children = new String[3];
                            int i = 0;
                            for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                                Log.d(TAG, messageSnapshot.getValue().toString());
                                children[i] = messageSnapshot.getValue().toString();
                                i++;
                            }
                            String name = children[2];
                            String description = children[0];
                            String uid = children[1];
                            Picnic p = new Picnic(name, description, uid);
                            Log.d(TAG, "UID = " + p.hostUID);
                            data.add(p);

                            Log.d(TAG, "Data: "+data.size());
                            for(Picnic pic:data){
                                Log.d(TAG, pic.hostUID);
                            }

                            PicnicAdapter adapter = new PicnicAdapter(getApplicationContext(), data);
                            picnicGallery.setAdapter(adapter);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        ***/


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

}
