package com.MSMedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {
    Button logInButton;
    Button goToSignupButton;
    boolean passwordVisible = true;
    ToggleButton passwordToggleButton;
    ConstraintLayout constraintLayout;
    EditText username;
    EditText password;
    TextView forgetPassword;
    View orLeft;
    View orRight;
    TextView OR;
    ImageView fbImage;
    TextView loginFB;

    private FirebaseAuth mAuth;

    public void showUserList(){
        password.setText("");
        Intent intent = new Intent(getApplicationContext(), AllUserActivity.class);
        startActivity(intent);
        finish();
    }

    //  BUTTON FOR SIGNUP AND LOGIN TO TRIGGER
    public void logIn(View view){
        String email = username.getText().toString();
        String pass = password.getText().toString();
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        // CHECK IF USERNAME AND PASSWORD ARE EMPTY
        if (username.getText().toString().equals("") || password.getText().toString().equals("")){
            Toast.makeText(this, "Log in data cannot be empty.", Toast.LENGTH_SHORT).show();
        }else if (!email.matches(emailPattern)){
            Toast.makeText(this, "Please enter a valid Email", Toast.LENGTH_SHORT).show();
        }else {
            Log.i("log in", "clicked");
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        assert firebaseUser != null;
                        Toast.makeText(MainActivity.this, "Logged in as: " + firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this, "Opps! Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mAuth = FirebaseAuth.getInstance();

        constraintLayout = findViewById(R.id.signUpLayout);
        forgetPassword = findViewById(R.id.forgotPasswordTextView);
        passwordToggleButton = findViewById(R.id.passwordToggleButton);
        ImageView logoImage = findViewById(R.id.logoImageView);
        username = findViewById(R.id.usernameText);
        password = findViewById(R.id.passwordText);
        logInButton = findViewById(R.id.sugnUpButton);
        orLeft = findViewById(R.id.ORLeft);
        OR = findViewById(R.id.ORText);
        orRight = findViewById(R.id.ORRight);
        fbImage = findViewById(R.id.fbLogoImage);
        loginFB = findViewById(R.id.loginwithfb);
        goToSignupButton = findViewById(R.id.goToSignupButton);

        // auto fill for below views
        username.setAutofillHints(View.AUTOFILL_HINT_EMAIL_ADDRESS);
        password.setAutofillHints(View.AUTOFILL_HINT_PASSWORD);

        // animations
        logoImage.setY(-600);
        logoImage.animate().translationYBy(600).setDuration(800);
        username.setX(-1000);
        username.animate().setStartDelay(300).translationXBy(1000).setDuration(800);
        password.setX(1000);
        password.animate().setStartDelay(400).translationXBy(-1000).setDuration(800);
        passwordToggleButton.setX(1000);
        passwordToggleButton.animate().setStartDelay(400).translationXBy(-1000).setDuration(800);
        logInButton.setY(1000);
        logInButton.animate().setStartDelay(600).translationYBy(-1000).setDuration(800);
        forgetPassword.setY(1000);
        forgetPassword.animate().setStartDelay(800).translationYBy(-1000).setDuration(800);
        orLeft.setX(-1000);
        orLeft.animate().setStartDelay(800).translationXBy(1000).setDuration(800);
        OR.setY(1000);
        OR.animate().setStartDelay(800).translationYBy(-1000).setDuration(800);
        orRight.setX(1000);
        orRight.animate().setStartDelay(800).translationXBy(-1000).setDuration(800);
        fbImage.setX(-1000);
        fbImage.animate().setStartDelay(800).translationXBy(1000).setDuration(800);
        loginFB.setX(1000);
        loginFB.animate().setStartDelay(800).translationXBy(-1000).setDuration(800);
        goToSignupButton.setY(1000);
        goToSignupButton.animate().setStartDelay(600).translationYBy(-1000).setDuration(900);


        passwordToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordVisible){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordVisible = false;
                }else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordVisible = true;
                }
            }
        });

        goToSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
        password.setOnKeyListener(this);
        forgetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.forgotPasswordTextView){
            Log.i("forget","password");
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
            logIn(view);
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(),0);
        }
        return false;
    }
}