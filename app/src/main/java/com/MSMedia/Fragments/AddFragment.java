package com.MSMedia.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.MSMedia.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddFragment extends Fragment {
    Context context;
    List<String> images;
    RecyclerView recyclerView;
    GalleryAdapter galleryAdapter;
    private static final int READ_PERMISSION_CODE = 101;

    public AddFragment() {
        // Required empty public constructor
    }

    public AddFragment(Context context) {
        this.context = context;
    }

    public static AddFragment newInstance() {
        return new AddFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add, container, false);

        recyclerView = rootView.findViewById(R.id.add_frag_recycler_view);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_PERMISSION_CODE);
        }else{
            loadImages();
        }

        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_PERMISSION_CODE){
            if (grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
                loadImages();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void loadImages() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
        images = ImagesGallery.listOfImages(context);
        galleryAdapter = new GalleryAdapter(context, images, new GalleryAdapter.PhotoListener() {
            @Override
            public void onPhotoClick(String path) {
                Toast.makeText(context, "photo tapped", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(galleryAdapter);
    }
}

class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{

    Context context;
    List<String> images;
    protected PhotoListener photoListener;

    public GalleryAdapter(Context context, List<String> images, PhotoListener photoListener) {
        this.context = context;
        this.images = images;
        this.photoListener = photoListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.gallery_item,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final String image = images.get(position);
        Glide.with(context).load(image).into(holder.image);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Image: " + position + " clicked.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.gallery_image);
        }
    }

    public interface PhotoListener{
        void onPhotoClick(String path);
    }
}

class ImagesGallery{
    @SuppressLint("Recycle")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static ArrayList<String> listOfImages(Context context){
        Uri uri;
        Cursor cursor;
        int column_index;
//        int column_index_folder;
        ArrayList<String> listOfAllImages = new ArrayList<>();
        String pathOfImage;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        cursor = context.getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
        assert cursor != null;
        column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

//        get folder name
//        column_index_folder = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        while(cursor.moveToNext()){
            pathOfImage = cursor.getString(column_index);
            listOfAllImages.add(pathOfImage);
        }

        return listOfAllImages;
    }
}