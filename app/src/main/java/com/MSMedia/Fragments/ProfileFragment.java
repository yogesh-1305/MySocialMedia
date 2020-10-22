package com.MSMedia.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.MSMedia.R;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    Context context;
//    CircleImageView imageView;
//    ConstraintLayout bottomSheetLayout;
//    Button editProfileButton, optionsButton;
//    ImageView cancelEdits;
    public ProfileFragment() {
        // Required empty public constructor
    }

    public ProfileFragment(Context context){
        this.context = context;
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        rootView.findViewById(R.id.edit_profile_button).setOnClickListener(this);
        rootView.findViewById(R.id.options_button).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.edit_profile_button){
            Toast.makeText(context, "Edit button clicked!", Toast.LENGTH_SHORT).show();
        }else if (v.getId() == R.id.options_button){
            Toast.makeText(context, "Options Button Clicked", Toast.LENGTH_SHORT).show();
        }
    }
}