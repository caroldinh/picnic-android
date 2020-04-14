package com.picnic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.picnic.data.Picnic;

import java.util.ArrayList;
import java.util.Random;

public class CreatePicnic extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    String username;
    String uid;

    EditText name;
    EditText description;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_picnic);

        name = findViewById(R.id.name);
        description = findViewById(R.id.description);

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

        if(!name.getText().toString().equals("") && !description.getText().toString().equals("")) {

            key = generateID();

            mDatabase.child("picnics").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    while (dataSnapshot.hasChild(key)) {
                        key = generateID();
                    }
                    Picnic newPicnic = new Picnic(name.getText().toString(), description.getText().toString(), uid, key);
                    mDatabase.child("picnics").child(key).setValue(newPicnic);

                    mDatabase.child("users").child(uid).child("Hosted").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList hosted = new ArrayList<String>();
                            for (int i = 0; i < dataSnapshot.getChildrenCount(); i++) {
                                hosted.add(((ArrayList) dataSnapshot.getValue()).get(i).toString());
                            }
                            hosted.add(key);
                            mDatabase.child("users").child(uid).child("Hosted").setValue(hosted);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    startActivity(new Intent(CreatePicnic.this, MainActivity.class));
                    finish();
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


    public String generateID(){

        Random dice = new Random();
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        String key = "";

        for(int i = 0; i < 6; i++){
            key += chars.charAt(dice.nextInt(chars.length()));
        }

        return key;

    }
}
