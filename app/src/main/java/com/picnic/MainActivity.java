package com.picnic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.picnic.data.Member;
import com.picnic.data.Picnic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity  {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String name, email, uid;

    private boolean isFABOpen;

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    ListView drawerList;
    FloatingActionButton add;
    FloatingActionButton host;
    FloatingActionButton join;

    NavDrawerItem[] navRows;

    RecyclerView picnicGallery;
    String TAG = "Mainactivity Debugging Tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Picnics");

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
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
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
                        startActivity(new Intent(MainActivity.this, Register.class));
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
                    if(!isFABOpen){
                        showFABMenu();
                    }else{
                        closeFABMenu();
                    }
                }
        });

        host = (FloatingActionButton) findViewById(R.id.host);
        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Host Picnic", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, CreatePicnic.class));
            }
        });
        join = (FloatingActionButton) findViewById(R.id.join);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Join Picnic", Toast.LENGTH_SHORT).show();

                final EditText taskEditText = new EditText(MainActivity.this);
                final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Enter Picnic Code")
                        .setView(taskEditText)
                        .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String code = String.valueOf(taskEditText.getText());
                                joinPicnic(code);

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });


        picnicGallery = findViewById(R.id.recyclerView);

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

            // Check if user's email is verified
            //boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            uid = user.getUid();

            Toast.makeText(getApplicationContext(), "Hello " + name, Toast.LENGTH_SHORT).show();
        }

        picnicGallery.setLayoutManager(new LinearLayoutManager(this));

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

                for(int i = 0; i < picnicIds.size(); i++){
                    Log.d(TAG, picnicIds.get(i));
                    mDatabase.child("picnics").child(picnicIds.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String name = dataSnapshot.child("name").getValue().toString();
                            String description = dataSnapshot.child("description").getValue().toString();
                            String uid = dataSnapshot.child("hostUID").getValue().toString();
                            String id = dataSnapshot.child("id").getValue().toString();

                            Picnic p = new Picnic(name, description, uid, id);
                            data.add(p);

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


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    private void showFABMenu(){
        isFABOpen=true;
        host.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        join.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        host.animate().translationY(0);
        join.animate().translationY(0);
    }

    private void joinPicnic(String c){

        final String code = c;

        mDatabase.child("picnics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(code)) {

                    boolean joined = false;
                    final ArrayList<Member> members = new ArrayList<>();
                    for (int i = 0; i < dataSnapshot.child(code).child("members").getChildrenCount(); i++) {
                        String mUID = dataSnapshot.child(code).child("members").child(""+i).child("UID").getValue().toString();
                        Log.d(TAG, mUID);

                        members.add(new Member(mUID));

                        // [{UID=7bfHVaXLgHO16UfkZmzUWemrNax1, contributions=0, critiques=0}]

                        if(mUID.equals(uid)){
                            joined = true;
                        }
                    }

                    if(!joined){

                        members.add(new Member(uid));
                        mDatabase.child("picnics").child(code).child("members").setValue(members);

                        mDatabase.child("users").child(uid).child("Joined").addListenerForSingleValueEvent(new ValueEventListener() {

                            // Get a list of picnics already joined
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList joined = new ArrayList<String>();
                                for (int i = 0; i < dataSnapshot.getChildrenCount(); i++) {
                                    joined.add(((ArrayList) dataSnapshot.getValue()).get(i).toString());
                                }

                                // Add the newly joined picnic and push to database
                                joined.add(code);
                                mDatabase.child("users").child(uid).child("Joined").setValue(joined);

                                // Refresh the page
                                startActivity(new Intent(MainActivity.this, MainActivity.class));
                                finish();



                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                    }
                    else{
                        Toast.makeText(getApplicationContext(), "You've already joined this Picnic!", Toast.LENGTH_SHORT).show();
                    }



                } else{
                    Toast.makeText(getApplicationContext(), "This Picnic does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
