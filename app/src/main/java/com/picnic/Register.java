package com.picnic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.picnic.data.User;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    EditText usernameEditText;
    EditText passwordEditText;
    EditText displayName;
    EditText verifyPassword;
    Button registerButton;
    ProgressBar loadingProgressBar;
    Button loginButton;
    TextView loginInstead;
    TextView registerInstead;
    String TAG = "Register Debugging Tag";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        displayName = findViewById(R.id.displayname);
        registerButton = findViewById(R.id.register);
        loadingProgressBar = findViewById(R.id.loading);
        loginButton = findViewById(R.id.login);
        loginInstead = findViewById(R.id.logininstead);
        registerInstead = findViewById(R.id.registerInstead);
        verifyPassword = findViewById(R.id.verifyPassword);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user = mAuth.getCurrentUser();
        // updateUI(currentUser);

        if (user != null) {
            updateUI(user);
        }
    }

    public void login(View v){

        String email = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        try {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(Register.this, "Your username or password is incorrect.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        catch(Exception e){
            Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
        }

    }


    public void register(View v){

        final String email = usernameEditText.getText().toString();
        final String name = displayName.getText().toString();
        final String password = passwordEditText.getText().toString();
        final String verified = verifyPassword.getText().toString();

        if(password.equals(verified) && email.indexOf("@") != -1 && !name.equals("") && !password.equals("")) {

            try {

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    final FirebaseUser user = mAuth.getCurrentUser();
                                    User userLog = new User(user.getUid(), name);
                                    mDatabase.child("users").child(user.getUid()).setValue(userLog);
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();
                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "User profile updated.");
                                                        updateUI(user);
                                                    }
                                                }
                                            });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(Register.this, "Registration failed.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT).show();
            }
        }

        else{
            if(!password.equals(verified)){
                Toast.makeText(getApplicationContext(), "Passwords must match", Toast.LENGTH_SHORT).show();
            } else if(email.indexOf("@")==-1){
                Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
            } else if(name.equals("")){
                Toast.makeText(getApplicationContext(), "Display name cannot be blank", Toast.LENGTH_SHORT).show();
            } else if(password.equals("")){
                Toast.makeText(getApplicationContext(), "Password cannot be blank", Toast.LENGTH_SHORT).show();
            }



        }

    }


    public void updateUI(FirebaseUser user){

        startActivity(new Intent(Register.this, MainActivity.class));
        finish();

    }

    public void showLogin(View v){

        loginButton.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.GONE);
        displayName.setVisibility(View.GONE);
        registerInstead.setVisibility(View.VISIBLE);
        loginInstead.setVisibility(View.GONE);
        verifyPassword.setVisibility(View.GONE);

        displayName.setText("");
        usernameEditText.setText("");
        passwordEditText.setText("");
        verifyPassword.setText("");


    }

    public void showRegister(View v){

        loginButton.setVisibility(View.GONE);
        registerButton.setVisibility(View.VISIBLE);
        displayName.setVisibility(View.VISIBLE);
        registerInstead.setVisibility(View.GONE);
        loginInstead.setVisibility(View.VISIBLE);
        verifyPassword.setVisibility(View.VISIBLE);

        displayName.setText("");
        usernameEditText.setText("");
        passwordEditText.setText("");
        verifyPassword.setText("");


    }


}
