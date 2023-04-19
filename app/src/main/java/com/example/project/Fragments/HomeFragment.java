package com.example.project.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.project.FriendsPostsAdapter;
import com.example.project.Interfaces.IFragmentCommunication;
import com.example.project.Interfaces.IHomeToMain;
import com.example.project.Model.User;
import com.example.project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private RecyclerView friendsPostsRecyclerView;
    private FriendsPostsAdapter adapter;

    private User currentLocalUser;
    private BottomNavigationView bottomNavigationView;
    private Button logoutButton;
    private IFragmentCommunication mListener;
    private ImageView addButton;
    private IHomeToMain homeToMain;
    public HomeFragment() {
        // Required empty public constructor
    }
    public HomeFragment(User currentLocalUser) {
        this.currentLocalUser = currentLocalUser;
    }


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

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


      //  List<Post> postsList = getFriendsPosts(); // get the list of friends' posts
     //   adapter = new FriendsPostsAdapter(postsList);
     //   friendsPostsRecyclerView.setAdapter(adapter);

        return view;

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IHomeToMain){
            this.homeToMain = (IHomeToMain) context;
        }else{
            throw new RuntimeException(context.toString()+ "must implement IHomeToMain");
        }
    }

}