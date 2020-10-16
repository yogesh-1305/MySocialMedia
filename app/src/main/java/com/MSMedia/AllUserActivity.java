package com.MSMedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AllUserActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;
    List<String> username;
    List<Bitmap> photos;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);
        Objects.requireNonNull(getSupportActionBar()).setTitle("User Feeds");
        mAuth = FirebaseAuth.getInstance();
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        username = new ArrayList<>();
        photos = new ArrayList<>();
//        loadingDialog = new LoadingDialog(AllUserActivity.this);
//        loadingDialog.startDialog();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

//        FragmentManager manager = getSupportFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//        HomeFragment homeFragment = new HomeFragment();
//        transaction.add(R.id.fragment, homeFragment);
//        transaction.commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navHome:
                        Toast.makeText(AllUserActivity.this, "Home clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navSearch:
                        Toast.makeText(AllUserActivity.this, "Search", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navAdd:
                        Toast.makeText(AllUserActivity.this, "add", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navActivity:
                        Toast.makeText(AllUserActivity.this, "activity", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navProfile:
                        Toast.makeText(AllUserActivity.this, "profile", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(AllUserActivity.this, "default", Toast.LENGTH_SHORT).show();
                        break;
                }
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
        switch (item.getItemId()){
            case R.id.logOut:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}