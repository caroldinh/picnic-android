package com.picnic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.picnic.data.Artwork;
import com.picnic.data.Member;
import com.picnic.data.Picnic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class UploadArtwork extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    FirebaseStorage storage;

    public static final int GET_FROM_GALLERY = 3;

    String username;
    String uid;
    String picnicID;
    Bitmap image;

    EditText name;
    EditText description;
    EditText feedback;

    ImageView preview;

    Button upload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_artwork);

        picnicID = getIntent().getStringExtra("PicnicID");

        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        feedback = findViewById(R.id.feedback);
        upload = findViewById(R.id.upload);
        preview = findViewById(R.id.preview);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();

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

        if(!name.getText().toString().equals("") && !description.getText().toString().equals("") &&
                !feedback.getText().toString().equals("") && !upload.getText().toString().equals("No file chosen")) {

            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();

            Date date = new Date();
            long timestamp = date.getTime();

            String filename = picnicID + "_" + timestamp + ".png";

            // Create a reference to "mountains.jpg"
            StorageReference ref = storageRef.child(filename);

            // Create a reference to 'images/mountains.jpg'
            StorageReference imagesref = storageRef.child("images/" + filename);

            // Get the data from an ImageView as bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = ref.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                    Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                    while(!downloadUri.isComplete()){}
                    if(downloadUri.isSuccessful()){
                        final String url = downloadUri.getResult().toString();

                        mDatabase.child("picnics").child(picnicID).child("artworks").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Artwork art = new Artwork(name.getText().toString(), uid, url, description.getText().toString(), feedback.getText().toString());
                                mDatabase.child("picnics").child(picnicID).child("artworks").child(""+dataSnapshot.getChildrenCount()).setValue(art);
                                mDatabase.child("picnics").child(picnicID).child("members").child(uid).child("contributions").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        long contributions = (long)dataSnapshot.getValue();
                                        contributions++;
                                        mDatabase.child("picnics").child(picnicID).child("members").child(uid).child("contributions").setValue(contributions);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                                finish();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                    }

                    /***

                     ***/

                }
            });


            /***

            mDatabase.child("picnics").child(picnicID).child("artworks").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

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

                    startActivity(new Intent(UploadArtwork.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
             ***/

        }

    }

    public void upload(View v){

        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                image = bitmap;
                preview.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
