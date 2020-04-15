package com.picnic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.picnic.data.Critique;
import java.util.Date;
import com.picnic.data.Picnic;

import java.util.ArrayList;
import java.util.Random;

public class CreateCritique extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    String username;
    String uid;

    EditText bread1, sandwich, bread2;
    TextView feedback;

    String artID, picnicID, fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_critique);

        picnicID = getIntent().getStringExtra("PicnicID");
        artID = getIntent().getStringExtra("ArtID");
        fb = getIntent().getStringExtra("Feedback");

        bread1 = findViewById(R.id.bread1);
        sandwich = findViewById(R.id.sandwich);
        bread2 = findViewById(R.id.bread2);

        feedback = findViewById(R.id.feedback);
        feedback.setText(fb);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user = mAuth.getCurrentUser();
        // updateUI(currentUser);
        if (user != null) {
            // Name, email address, and profile photo Url
            username = user.getDisplayName();
            uid = user.getUid();

        }

    }

    public void cancel(View v){

        finish();

    }

    public void create(View v){

        if(!bread1.getText().toString().equals("") && !bread2.getText().toString().equals("") && !sandwich.getText().toString().equals("")) {

            mDatabase.child("picnics").child(picnicID).child("artworks").child(artID).child("critiques").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Date date = new Date();
                    long timestamp = date.getTime();
                    Critique critique = new Critique(uid, bread1.getText().toString(),
                            sandwich.getText().toString(), bread2.getText().toString());

                    mDatabase.child("picnics").child(picnicID).child("artworks").child(artID).child("critiques").child(dataSnapshot.getChildrenCount()+"").setValue(critique);

                    mDatabase.child("picnics").child(picnicID).child("members").child(uid).child("critiques").addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            long critiques = (long)dataSnapshot.getValue();
                            critiques++;
                            mDatabase.child("picnics").child(picnicID).child("members").child(uid).child("critiques").setValue(critiques);
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }

                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }

    }
}
