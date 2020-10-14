package com.MSMedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserListActivity extends AppCompatActivity {
    ListView userList;
    List<String> userNames;
    UserNameListAdapter adapter;
    LoadingDialog loadingDialog = new LoadingDialog(this);
    // CREATING MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.user_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    // MENU ITEM SELECTED
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case R.id.logOut:
                ParseUser.logOut();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                Toast.makeText(this, "Logged Out from: " + ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.share:
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }else {
                    getPhoto();
                }
            default:
                return false;
        }
    }

    // GETTING MEDIA FROM PHONE STORAGE
    public void getPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    // METHOD TO HANDLE IMAGE UPLOAD TO THE SERVER
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            loadingDialog.startDialog();
            Uri selectedImage = data.getData();
            if (requestCode == 1 && resultCode == RESULT_OK) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] bytes = stream.toByteArray();
                    ParseFile file = new ParseFile("image.png", bytes);
                    ParseObject object = new ParseObject("Image");
                    object.put("image", file);
                    object.put("username", ParseUser.getCurrentUser().getUsername());

                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                loadingDialog.dismissDialog();
                                Toast.makeText(UserListActivity.this, "Image uploaded success.", Toast.LENGTH_SHORT).show();
                            } else {
                                loadingDialog.dismissDialog();
                                Toast.makeText(UserListActivity.this, "Error uploading Image!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }else {
            finish();
            startActivity(getIntent());
//            Intent intent = new Intent(getApplicationContext(),UserListActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            startActivity(intent);
        }

    }

    // METHOD TO HANDEL USER PERMISSIONS
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getPhoto();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        userList = findViewById(R.id.userList);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Users List");
        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startDialog();
        userNames = new ArrayList<>();

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),UserFeedsActivity.class);
                intent.putExtra("username", userNames.get(i));
                startActivity(intent);
            }
        });

        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        parseQuery.addAscendingOrder("username");

        parseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() >0){
                    for (ParseUser user : objects){
                        userNames.add(user.getUsername());
                    }
                    adapter = new UserNameListAdapter(getApplicationContext(), userNames);
                    userList.setAdapter(adapter);
                }else {
                    Toast.makeText(UserListActivity.this, "Cannot Find Users!", Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismissDialog();
            }
        });
    }
}