package com.MSMedia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;


import static com.parse.Parse.getApplicationContext;
import static com.MSMedia.R.drawable.like;
import static com.MSMedia.R.drawable.liked;

public class AllFeedsAdapter extends BaseAdapter {
    int size;
    boolean likeClicked = false;
    Context context;
    List<String> username;
    List<Bitmap> images;

    public AllFeedsAdapter(Context context, List<String> username, List<Bitmap> images) {
        this.context = context;
        this.size = username.size();
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

    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.card_view_layout, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.usernameText.setText(username.get(i));
        viewHolder.imageView.setImageBitmap(images.get(i));

        viewHolder.feedProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),UserFeedsActivity.class);
                intent.putExtra("username", username.get(i));
                context.startActivity(intent);
            }
        });

        viewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (likeClicked) {
                    likeClicked = false;
                    viewHolder.likeButton.setImageResource(like);
                }else {
                    likeClicked = true;
                    viewHolder.likeButton.setImageResource(liked);
                }
            }
        });

        viewHolder.imageView.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(getApplicationContext(),new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Log.i("double", "tapped");
                    if (likeClicked) {
                        likeClicked = false;
                        viewHolder.likeButton.setImageResource(like);
                    }else {
                        likeClicked = true;
                        viewHolder.likeButton.setImageResource(liked);
                    }
                    return super.onDoubleTap(e);
                }
            });
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });

        viewHolder.usernameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("image","tapped" + username.get(i));
                Intent intent = new Intent(getApplicationContext(),UserFeedsActivity.class);
                intent.putExtra("username", username.get(i));
                context.startActivity(intent);
            }
        });
        return view;
    }

    private static class ViewHolder{
        TextView usernameText;
        ImageView imageView;
        ImageButton likeButton;
        ImageView feedProfilePic;

        ViewHolder(View view){
            usernameText = view.findViewById(R.id.username);
            imageView = view.findViewById(R.id.myImage);
            likeButton = view.findViewById(R.id.likeButton);
            feedProfilePic = view.findViewById(R.id.feedProfilePic);
        }
    }
}
