package com.MSMedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.MSMedia.Fragments.ActivityFragment;
import com.MSMedia.Fragments.AddFragment;
import com.MSMedia.Fragments.HomeFragment;
import com.MSMedia.Fragments.ProfileFragment;
import com.MSMedia.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AllUserActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;
    List<String> username;
    List<Bitmap> photos;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);
        mAuth = FirebaseAuth.getInstance();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        username = new ArrayList<>();
        photos = new ArrayList<>();
//        loadingDialog = new LoadingDialog(AllUserActivity.this);
//        loadingDialog.startDialog();


        // TO INITIALLY SHOW HOME FRAGMENT ON ACTIVITY LAUNCH
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new HomeFragment()).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()){
                    case R.id.navHome:
                        selectedFragment = new HomeFragment();
//                        Toast.makeText(AllUserActivity.this, "Home clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navSearch:
                        selectedFragment = new SearchFragment();
//                        Toast.makeText(AllUserActivity.this, "Search", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navAdd:
                        selectedFragment = new AddFragment(AllUserActivity.this);
//                        Toast.makeText(AllUserActivity.this, "add", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navActivity:
                        selectedFragment = new ActivityFragment();
//                        Toast.makeText(AllUserActivity.this, "activity", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navProfile:
                        selectedFragment = new ProfileFragment(AllUserActivity.this);
//                        Toast.makeText(AllUserActivity.this, "profile", Toast.LENGTH_SHORT).show();
                        break;
                    default:
//                        Toast.makeText(AllUserActivity.this, "default", Toast.LENGTH_SHORT).show();
                        break;

                }
                assert selectedFragment != null;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, selectedFragment).commit();
                return true;
            }
        });
    }



    // BOOLEAN AND BELOW METHOD HANDLES EXITING THE APP ON BACK BUTTON PRESS
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }

    public void openCamera(View view){
        Intent intent = new Intent(getApplicationContext(),CameraActivity.class);
        startActivity(intent);
    }

    public void ShowUsersList(View view){
        Intent intent = new Intent(getApplicationContext(),UserListActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}