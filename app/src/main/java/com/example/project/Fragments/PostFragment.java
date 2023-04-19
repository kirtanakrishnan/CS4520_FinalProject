package com.example.project.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.project.Interfaces.IAddPostToMain;
import com.example.project.Interfaces.IPostToMain;
import com.example.project.Model.Post;
import com.example.project.Model.User;
import com.example.project.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ARTIST = "song_artist";
    private static final String ARG_TITLE = "song_title";
    private static final String ARG_TIME = "time_posted";
    private static final String ARG_USERNAME = "username";


    // TODO: Rename and change types of parameters
    private String artist;
    private String songTitle;
    private String timePosted;
    private String username;
    private TextView textViewArtist;
    private TextView textViewTitle;
    private TextView textViewTime;
    private TextView textViewUsername;
    private Button buttonPost;
    private IPostToMain postToMain;


    public PostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostFragment newInstance(Post post) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTIST, post.getSongArtist());
        args.putString(ARG_TITLE, post.getSongTitle());
        args.putString(ARG_TIME, post.getTimePosted());
        args.putString(ARG_USERNAME, post.getUsername());

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artist = getArguments().getString(ARG_ARTIST);
            songTitle = getArguments().getString(ARG_TITLE);
            timePosted = getArguments().getString(ARG_TIME);
            username = getArguments().getString(ARG_USERNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        textViewArtist = view.findViewById(R.id.textViewSongArtist);
        textViewTitle = view.findViewById(R.id.textViewSongTitle);
        textViewTime = view.findViewById(R.id.textViewTime);
        textViewUsername = view.findViewById(R.id.textViewUsernamePost);
        buttonPost = view.findViewById(R.id.buttonPost);

        textViewArtist.setText(artist);
        textViewTitle.setText(songTitle);
        textViewTime.setText(timePosted);
        textViewUsername.setText(username);

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Post post = new Post();
                post.setUsername(username);
                post.setTimePosted(timePosted);
                post.setSongArtist(artist);
                post.setSongTitle(songTitle);
                postToMain.postButtonClicked(post);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IPostToMain){
            this.postToMain = (IPostToMain) context;
        }else{
            throw new RuntimeException(context.toString()+ "must implement IPostToMain");
        }
    }
}