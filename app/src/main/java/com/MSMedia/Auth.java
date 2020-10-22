package com.MSMedia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Auth extends AppCompatActivity {
    Context context;
    final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public Auth(Context c) {
        this.context = c;
    }

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    CallbackManager mCallbackManager = CallbackManager.Factory.create();


    public void fbLoginButton(){
        LoginManager.getInstance().logInWithReadPermissions((Activity) context, Arrays.asList("email", "public profile"));
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
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //  FACEBOOK SIGN IN
    public void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            updateUI();
                            Toast.makeText(context, "Signed in as: " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
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
            Log.i(data.toString(), "onActivityResult: ");
            if (requestCode == 1 && resultCode == RESULT_OK){
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    assert account != null;
                    firebaseAuthWithGoogle(account.getIdToken());
                }catch (Exception e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // AUTHENTICATING USER TO FIREBASE
    public void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            updateUI();
                            Toast.makeText(context, "Signed in as: " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    //  BUTTON FOR SIGNUP AND LOGIN TO TRIGGER
    public void logIn(String email, String password){
        // CHECK IF USERNAME AND PASSWORD ARE EMPTY
        if (email.equals("") || password.equals("")){
            Toast.makeText(context, "Log in data cannot be empty.", Toast.LENGTH_SHORT).show();
        }else if (!email.matches(emailPattern)){
            Toast.makeText(context, "Please enter a valid Email", Toast.LENGTH_SHORT).show();
        }else {
            Log.i("log in", "clicked");
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        assert firebaseUser != null;
                        Toast.makeText(context, "Logged in as: " + firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                        updateUI();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void signUp(String mail, final String userName, final String pass, String retypePassword){
        if (mail.equals("") && userName.equals("")) {
            Toast.makeText(context, "All above fields are required.", Toast.LENGTH_SHORT).show();
        }else if (!mail.matches(emailPattern)){
            Toast.makeText(context, "Please enter a valid Email", Toast.LENGTH_SHORT).show();
        }else if (!pass.equals(retypePassword)) {
            Toast.makeText(context, "Password Mis-Match", Toast.LENGTH_SHORT).show();
        }else{
            mAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        assert firebaseUser != null;
                        uploadDetailsToDatabase(userName, pass);
                        Toast.makeText(context, "Signed Up as: " + firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void uploadDetailsToDatabase(String username, String password){
//        Log.i("logs data    " + username, password);
        User user = new User(username,password);
        FirebaseDatabase.getInstance().getReference("Users").push().setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        Toast.makeText(SignUpActivity.this, "complete", Toast.LENGTH_SHORT).show();
                        updateUI();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUI(){
        Auth.this.context.startActivity(new Intent(context,AllUserActivity.class));
    }
}
