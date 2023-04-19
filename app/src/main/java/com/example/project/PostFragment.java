package com.example.project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    // TODO: Rename and change types of parameters
    private String artist;
    private String songTitle;
    private String timePosted;
    private TextView textViewArtist;
    private TextView textViewTitle;
    private TextView textViewTime;




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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        textViewArtist = view.findViewById(R.id.textViewSongArtist);
        textViewTitle = view.findViewById(R.id.textViewSongTitle);
        textViewTime = view.findViewById(R.id.textViewTime);

        textViewArtist.setText(artist);
        textViewTitle.setText(songTitle);
        textViewTime.setText(timePosted);

        // Inflate the layout for this fragment
        return view;
    }
}