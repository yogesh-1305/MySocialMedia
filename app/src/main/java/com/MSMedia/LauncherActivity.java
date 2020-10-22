package com.MSMedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.ParseUser;

import java.util.Objects;

public class LauncherActivity extends AppCompatActivity {
    protected FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // CHECK IF USER IS ALREADY LOGGED IN
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            showAllFeeds();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        Button signUp = findViewById(R.id.signUpLauncherActivity);
        Button logIn = findViewById(R.id.logInLauncherActivity);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showAllFeeds(){
        startActivity(new Intent(getApplicationContext(), AllUserActivity.class));
        finish();
    }
}