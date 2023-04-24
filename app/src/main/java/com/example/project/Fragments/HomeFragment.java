package com.example.project.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.project.Adapters.FriendsPostsAdapter;
import com.example.project.Interfaces.IFragmentCommunication;
import com.example.project.Interfaces.IHomeToMain;
import com.example.project.Model.Post;
import com.example.project.Model.User;
import com.example.project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private RecyclerView friendsPostsRecyclerView;
    private FriendsPostsAdapter adapter;
    private static final String ARG_POSTS = "posts";

    private User currentLocalUser;
    private BottomNavigationView bottomNavigationView;
    private IFragmentCommunication mListener;
    private ImageView addButton;
    private IHomeToMain homeToMain;
    private ArrayList<Post> posts;

    public HomeFragment() {
        // Required empty public constructor
    }

    public HomeFragment(User currentLocalUser) {
        this.currentLocalUser = currentLocalUser;
    }


    public static HomeFragment newInstance(ArrayList<Post> posts) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_POSTS, posts);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.posts = (ArrayList<Post>) getArguments().getSerializable(ARG_POSTS);
            if (posts != null) {
            } else {
            }
        }
        getActivity().setTitle("Home");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        // Inflate the layout for this fragment

        bottomNavigationView.findViewById(R.id.bottomNavigationView);
        friendsPostsRecyclerView = view.findViewById(R.id.friends_posts_recyclerView);
        friendsPostsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        addButton = view.findViewById(R.id.imageViewAddButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeToMain.addPostButtonClicked();
            }
        });

        adapter = new FriendsPostsAdapter(getContext(), posts);
        friendsPostsRecyclerView.setAdapter(adapter);

        return view;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IHomeToMain) {
            this.homeToMain = (IHomeToMain) context;
        } else if (context instanceof IFragmentCommunication) {
            this.mListener = (IFragmentCommunication) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement IHomeToMain");
        }

    }
}