package com.MSMedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity implements View.OnKeyListener {

    FirebaseAuth mAuth;
    EditText email;
    EditText username;
    EditText password;
    EditText retypePassword;
    Button signUpButton;
    Button goToLogin;
    ToggleButton passwordToggleButton;
    boolean passwordVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sign up");

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.singUpEmail);
        username = findViewById(R.id.signUpUsername);
        password = findViewById(R.id.signUpPassword);
        retypePassword = findViewById(R.id.signUpRepeatPassword);
        signUpButton = findViewById(R.id.signUpButton);
        passwordToggleButton = findViewById(R.id.signUpPasswordToggleButton);
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

        goToLogin = findViewById(R.id.goToLoginButton);
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
        retypePassword.setOnKeyListener(this);
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(),0);
            signUp(view);
        }
        return false;
    }

    public void signUp(View view){
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email.getText().toString().equals("") && username.getText().toString().equals("")) {
            Toast.makeText(this, "All above fields are required.", Toast.LENGTH_SHORT).show();
        }else if (!email.getText().toString().matches(emailPattern)){
            Toast.makeText(this, "Please enter a valid Email", Toast.LENGTH_SHORT).show();
        }else if (!password.getText().toString().equals(retypePassword.getText().toString())) {
            Toast.makeText(this, "Password Mis-Match", Toast.LENGTH_SHORT).show();
        }else{
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        assert firebaseUser != null;
                        Toast.makeText(SignUpActivity.this, "Signed Up as: " + firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void showAllFeeds(){
        //username.setText("");
        password.setText("");
        retypePassword.setText("");
        startActivity(new Intent(getApplicationContext(), AllUserActivity.class));
        finish();
    }

}