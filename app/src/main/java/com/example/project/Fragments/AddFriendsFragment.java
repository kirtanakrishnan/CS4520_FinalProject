package com.example.project.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.FriendsAdapter;
import com.example.project.FriendsPostsAdapter;
import com.example.project.Model.Post;
import com.example.project.Model.User;
import com.example.project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFriendsFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;

    private TextView textViewAddFriends;
    private EditText editTextEmailSearch;
    private TextView friends;
    private Button add;
    private RecyclerView recyclerViewFriends;
    private ArrayList<User> friendsList;
    private static final String ARG_FRIENDS = "friends";
    private FriendsAdapter adapter;
    private User currentLocalUser;

    private String friendEmail;

    public AddFriendsFragment() {
        // Required empty public constructor
    }

    public AddFriendsFragment(User currentLocalUser) {
        this.currentLocalUser = currentLocalUser;
    }

    // TODO: Rename and change types and number of parameters
    public static AddFriendsFragment newInstance() {
        AddFriendsFragment fragment = new AddFriendsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            this.friendsList = (ArrayList<User>) getArguments().getSerializable(ARG_FRIENDS);
            if(friendsList != null) {
                Log.d("demo", "posts: " + friendsList.toString());

            }
            else{
                Log.d("demo", "posts is null");

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_friends, container, false);

        textViewAddFriends = view.findViewById(R.id.textViewAddFriends);
        editTextEmailSearch = view.findViewById(R.id.editTextEmailSearch);
        friends = view.findViewById(R.id.friends);
        add = view.findViewById(R.id.add);
        recyclerViewFriends = view.findViewById(R.id.recyclerViewFriends);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendEmail = editTextEmailSearch.getText().toString().trim();

               String name = String.valueOf(db.collection("users")
                        .document(friendEmail).get(Source.valueOf("name")));

               User friend = new User(name);

               friendsList.add(friend);


            }
        });

        adapter = new FriendsAdapter(getContext(), friendsList);
        Log.d("demo", "setting friends");
        recyclerViewFriends.setAdapter(adapter);

        return view;
    }
}