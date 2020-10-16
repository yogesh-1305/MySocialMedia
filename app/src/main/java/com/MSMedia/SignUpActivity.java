package com.MSMedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {

    FirebaseAuth mAuth;
    EditText email;
    EditText username;
    EditText password;
    EditText retypePassword;
    Button signUpButton;
    Button goToLogin;
    ToggleButton passwordToggleButton;
    boolean passwordVisible = true;
    Auth auth = new Auth(SignUpActivity.this);


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
        goToLogin = findViewById(R.id.goToLoginButton);

        passwordToggleButton.setOnClickListener(this);
        goToLogin.setOnClickListener(this);
        retypePassword.setOnKeyListener(this);
    }


    public void signUp(View view){
        String userName = username.getText().toString();
        String mail = email.getText().toString();
        String pass = password.getText().toString();
        String retypedPassword = retypePassword.getText().toString();
        auth.signUp(mail,userName,pass,retypedPassword);

    }


    public void showAllFeeds(){
        //username.setText("");
        password.setText("");
        retypePassword.setText("");
        startActivity(new Intent(this, AllUserActivity.class));
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signUpPasswordToggleButton:
                if (passwordVisible){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordVisible = false;
                }else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordVisible = true;
                }
            case R.id.goToLoginButton:
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            default:
                Toast.makeText(this, "default case", Toast.LENGTH_SHORT).show();
        }
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
}