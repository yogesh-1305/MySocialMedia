package com.MSMedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    FloatingActionButton logInButton;
    Button goToSignupButton;
    FloatingActionButton fbLoginButton;
    FloatingActionButton googleSignInButton;
    boolean passwordVisible = true;
    ToggleButton passwordToggleButton;
    ConstraintLayout constraintLayout;
    EditText username;
    EditText password;
    TextView forgetPassword;
    ImageView logoImage;
    GoogleSignInOptions gso;
    GoogleSignInClient googleSignInClient;
    CallbackManager mCallbackManager;
    Auth auth;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        auth = new Auth(MainActivity.this);

        constraintLayout = findViewById(R.id.signUpLayout);
        passwordToggleButton = findViewById(R.id.passwordToggleButton);
        logoImage = findViewById(R.id.logoImageView);
        username = findViewById(R.id.usernameText);
        password = findViewById(R.id.passwordText);
        goToSignupButton = findViewById(R.id.goToSignupButton);
        logInButton = findViewById(R.id.loginButton);
        fbLoginButton = findViewById(R.id.FacebookLogin);
        googleSignInButton = findViewById(R.id.googleSignInButton);

        // auto fill for below views
        username.setAutofillHints(View.AUTOFILL_HINT_EMAIL_ADDRESS);
        password.setAutofillHints(View.AUTOFILL_HINT_PASSWORD);
//        // animations
//       animations();

        passwordToggleButton.setOnClickListener(this);
        fbLoginButton.setOnClickListener(this);
        goToSignupButton.setOnClickListener(this);
        password.setOnKeyListener(this);
//        forgetPassword.setOnClickListener(this);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

    }

    // GOOGLE SIGN IN INTENT
    public void signIn(View v) {
//        auth.signInWithGoogle();
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
        auth.onActivityResult(1,RESULT_OK,signInIntent);
    }

    //  BUTTON FOR LOGIN THROUGH EMAIL
    public void logIn(View view){
        String email = username.getText().toString();
        String pass = password.getText().toString();
        auth.logIn(email, pass);
    }

//    private void animations() {
//        logoImage.setY(-600);
//        logoImage.animate().translationYBy(600).setDuration(800);
//        username.setX(-1000);
//        username.animate().setStartDelay(300).translationXBy(1000).setDuration(800);
//        password.setX(1000);
//        password.animate().setStartDelay(400).translationXBy(-1000).setDuration(800);
//        passwordToggleButton.setX(1000);
//        passwordToggleButton.animate().setStartDelay(400).translationXBy(-1000).setDuration(800);
//        logInButton.setY(1000);
//        logInButton.animate().setStartDelay(600).translationYBy(-1000).setDuration(800);
//        forgetPassword.setY(1000);
//        forgetPassword.animate().setStartDelay(800).translationYBy(-1000).setDuration(800);
//        orLeft.setX(-1000);
//        orLeft.animate().setStartDelay(800).translationXBy(1000).setDuration(800);
//        OR.setY(1000);
//        OR.animate().setStartDelay(800).translationYBy(-1000).setDuration(800);
//        orRight.setX(1000);
//        orRight.animate().setStartDelay(800).translationXBy(-1000).setDuration(800);
//        goToSignupButton.setY(1000);
//        goToSignupButton.animate().setStartDelay(600).translationYBy(-1000).setDuration(900);
//    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.passwordToggleButton){
            if (passwordVisible){
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                passwordVisible = false;
            }else {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                passwordVisible = true;
            }
        }else if (view.getId() == R.id.goToSignupButton){
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            finish();
        }else if (view.getId() == R.id.FacebookLogin){
            auth.fbLoginButton();
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(),0);
            logIn(view);
        }
        return false;
    }

    public void showUserList(){
        password.setText("");
        startActivity(new Intent(getApplicationContext(), AllUserActivity.class));
        finish();
    }

}
