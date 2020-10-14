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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {
    Button logInButton;
    Button goToSignupButton;
    Button fbLoginButton;
    boolean passwordVisible = true;
    ToggleButton passwordToggleButton;
    ConstraintLayout constraintLayout;
    EditText username;
    EditText password;
    TextView forgetPassword;
    View orLeft;
    View orRight;
    TextView OR;
    ImageView logoImage;
    GoogleSignInOptions gso;
    GoogleSignInClient googleSignInClient;
    CallbackManager mCallbackManager;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mAuth = FirebaseAuth.getInstance();
//        FacebookSdk.sdkInitialize(this);

        constraintLayout = findViewById(R.id.signUpLayout);
        forgetPassword = findViewById(R.id.forgotPasswordTextView);
        passwordToggleButton = findViewById(R.id.passwordToggleButton);
        logoImage = findViewById(R.id.logoImageView);
        username = findViewById(R.id.usernameText);
        password = findViewById(R.id.passwordText);
        logInButton = findViewById(R.id.sugnUpButton);
        orLeft = findViewById(R.id.ORLeft);
        OR = findViewById(R.id.ORText);
        orRight = findViewById(R.id.ORRight);
        goToSignupButton = findViewById(R.id.goToSignupButton);
        fbLoginButton = findViewById(R.id.FacebookLogin);

        // auto fill for below views
        username.setAutofillHints(View.AUTOFILL_HINT_EMAIL_ADDRESS);
        password.setAutofillHints(View.AUTOFILL_HINT_PASSWORD);
        // animations
       animations();
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
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                finish();
            }
        });
        password.setOnKeyListener(this);
        forgetPassword.setOnClickListener(this);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        mCallbackManager = CallbackManager.Factory.create();
        fbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email", "public profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // GOOGLE SIGN IN INTENT
    public void signIn(View v) {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    //  FACEBOOK SIGN IN
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            Toast.makeText(MainActivity.this, "Signed in as: " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data); // DATA CALLBACK FOR FACEBOOK
        if (data != null){
            if (requestCode == 1 && resultCode == RESULT_OK){
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    assert account != null;
                    firebaseAuthWithGoogle(account.getIdToken());
                }catch (Exception e){
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // AUTHENTICATING USER TO FIREBASE
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            Toast.makeText(MainActivity.this, "Signed in as: " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private void animations() {
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
        goToSignupButton.setY(1000);
        goToSignupButton.animate().setStartDelay(600).translationYBy(-1000).setDuration(900);
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

    public void showUserList(){
        password.setText("");
        startActivity(new Intent(getApplicationContext(), AllUserActivity.class));
        finish();
    }

}
