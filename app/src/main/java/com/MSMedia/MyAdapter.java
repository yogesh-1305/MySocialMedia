package com.MSMedia;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;

public class MyAdapter extends BaseAdapter {
    int size;
    Context context;
    String username;
    List<Bitmap> images;

    public MyAdapter(@NonNull Context context, String username, List<Bitmap> images) {
        this.context = context;
        this.size = images.size();
        this.username = username;
        this.images = images;
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.card_view_layout,viewGroup,false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.usernameText.setText(username);
        viewHolder.imageView.setImageBitmap(images.get(i));
        return view;
    }

    private static class ViewHolder{
        TextView usernameText;
        ImageView imageView;

        ViewHolder(View view){
            usernameText = view.findViewById(R.id.username);
            imageView = view.findViewById(R.id.myImage);
        }
    }
}
