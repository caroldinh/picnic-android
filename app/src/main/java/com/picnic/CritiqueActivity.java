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
import com.picnic.data.Critique;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class CritiqueActivity extends AppCompatActivity  {

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

    String b1, sw, b2, ts, cr;

    TextView critiquer, bread1, sandwich, bread2, timestamp;
    ProgressBar prog;

    LinearLayout layout;

    boolean done = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b1 = getIntent().getStringExtra("bread1");
        b2 = getIntent().getStringExtra("bread2");
        sw = getIntent().getStringExtra("sandwich");
        ts = getIntent().getStringExtra("timestamp");
        cr = getIntent().getStringExtra("critiquer");

        setContentView(R.layout.activity_critique);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        header = findViewById(R.id.header);

        critiquer = findViewById(R.id.critiquer);
        critiquer.setText("Critique by " + cr);
        bread1 = findViewById(R.id.bread1);
        bread1.setText(b1);
        sandwich = findViewById(R.id.sandwich);
        sandwich.setText(sw);
        timestamp = findViewById(R.id.timestamp);
        timestamp.setText(ts);
        bread2 = findViewById(R.id.bread2);
        bread2.setText(b2);

        drawerLayout = findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Critique");

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
                        startActivity(new Intent(CritiqueActivity.this, MainActivity.class));
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
                        startActivity(new Intent(CritiqueActivity.this, Register.class));
                        finish();
                        break;
                    default:
                }

            }
        });

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        mAuth = FirebaseAuth.getInstance();

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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

}
