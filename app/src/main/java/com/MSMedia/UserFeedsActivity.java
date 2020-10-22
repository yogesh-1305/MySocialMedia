package com.MSMedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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

public class UserFeedsActivity extends AppCompatActivity {
    ListView listView;
    MyAdapter adapter;
    Bitmap bitmap;
    List<Bitmap> photos;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feeds);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        listView = findViewById(R.id.feedsListView);
        photos = new ArrayList<>();
        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startDialog();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Image");
        query.whereEqualTo("username", username);
//        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0){
                    for (ParseObject images : objects){
                        ParseFile file = (ParseFile) images.get("image");
                        assert file != null;
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null && data != null){
                                    bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                                    photos.add(bitmap);
                                    adapter = new MyAdapter(getApplicationContext(),username,photos);
                                    listView.setAdapter(adapter);
                                    loadingDialog.dismissDialog();
                                    Log.i("photo", "added");
                                }
                            }
                        });
                    }
                }else {
                    loadingDialog.dismissDialog();
                    Toast.makeText(UserFeedsActivity.this, "No feeds to show!", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}