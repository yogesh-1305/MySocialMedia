package com.MSMedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

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
    LoadingDialog loadingDialog;
    AllFeedsAdapter adapter;
    ListView listView;
    List<String> username;
    List<Bitmap> photos;
    Bitmap bitmap;
    int size = 0;


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

    public void getQuery(){
        username.clear();
        photos.clear();
        final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Image");
        //query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0){
                    for (final ParseObject images : objects){
                        //username.add(Objects.requireNonNull(images.get("username")).toString());
                        Log.i("username", (String) Objects.requireNonNull(images.get("username")));
                        ParseFile file = (ParseFile) images.get("image");
                        query.orderByDescending("createdAt");
                        if (file != null) {
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null && data != null) {
                                        username.add(Objects.requireNonNull(images.get("username")).toString());
                                        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        photos.add(bitmap);
//                                    size++;
                                        Log.i("photo", "added");
                                        adapter = new AllFeedsAdapter(getApplicationContext(), username, photos);
                                        listView.setAdapter(adapter);
                                        Log.i("adapter", "added");
                                        loadingDialog.dismissDialog();
                                    }
                                }
                            });
                        }else {
                            loadingDialog.dismissDialog();
                        }
                    }
                }else {
                    loadingDialog.dismissDialog();
                    Toast.makeText(AllUserActivity.this, "NO MEDIA FOUND!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);
        Objects.requireNonNull(getSupportActionBar()).setTitle("User Feeds");
        listView = findViewById(R.id.allFeedsListView);
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        username = new ArrayList<>();
        photos = new ArrayList<>();
        loadingDialog = new LoadingDialog(AllUserActivity.this);
        loadingDialog.startDialog();
        getQuery();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                finish();
//                startActivity(getIntent());
                getQuery();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }
}