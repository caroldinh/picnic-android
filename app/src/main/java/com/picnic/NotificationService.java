package com.picnic;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Service;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationService extends Service {

    public FirebaseAuth mAuth;
    public DatabaseReference mDatabase;
    String name, email, uid;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

            name = user.getDisplayName();
            email = user.getEmail();
            uid = user.getUid();

        }

        ChildEventListener postListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName){
                // Notif.notify(getApplicationContext(), snapshot.getValue().toString(), Integer.parseInt(snapshot.getKey()));
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName){ }

            @Override
            public void onChildRemoved(DataSnapshot snapshot){ }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName){ }

            @Override
            public void onCancelled(DatabaseError databaseError) { }

        };

        mDatabase.child("users").child(uid).child("notifications").child("unread").addChildEventListener(postListener);


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // STOP YOUR TASKS
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}