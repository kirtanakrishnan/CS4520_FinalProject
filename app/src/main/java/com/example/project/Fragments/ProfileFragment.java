package com.example.project.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.project.Interfaces.IProfileToMain;
import com.example.project.Model.User;
import com.example.project.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TOPSONGS = "top_songs";
    private static final String ARG_USERNAME = "username";

    private IProfileToMain profileToMain;
    private ArrayList<String> topSongs;
    private String username;

    public ProfileFragment() {
        // Required empty public constructor
    }



    public static ProfileFragment newInstance(User user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, user.getName());
        args.putSerializable(ARG_TOPSONGS, user.getTopTracks());

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topSongs = (ArrayList<String>) getArguments().getSerializable(ARG_TOPSONGS);
            username = getArguments().getString(ARG_USERNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button buttonConnectSpotify = view.findViewById(R.id.buttonConnectToSpotify);
        ListView listViewTracks = view.findViewById(R.id.songs_list);
        TextView textViewUsername = view.findViewById(R.id.profile_username);
        textViewUsername.setText(username);

        buttonConnectSpotify.setOnClickListener(view1 -> profileToMain.connectToSpotifyButtonClicked());

        // display top 10 songs on profile
        if(topSongs != null) {
            buttonConnectSpotify.setClickable(false);
            buttonConnectSpotify.setVisibility(View.GONE);

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_list_item_1,
                    topSongs );

            listViewTracks.setAdapter(arrayAdapter);
        }

        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IProfileToMain){
            this.profileToMain = (IProfileToMain) context;
        }else{
            throw new RuntimeException(context + "must implement IProfileToMain");
        }
    }
}