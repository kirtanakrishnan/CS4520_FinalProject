package com.example.project.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.project.Adapters.FriendsAdapter;
import com.example.project.Model.User;
import com.example.project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

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

    private TextView friends;
    private RecyclerView recyclerViewFriends;
    private ArrayList<User> friendsList;
    private FriendsAdapter adapter;
    private User currentLocalUser;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;

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
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        friendsList = new ArrayList<>();
//      track the users...
        getUsersRealTime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_friends, container, false);


        friends = view.findViewById(R.id.friends);
        recyclerViewFriends = view.findViewById(R.id.recyclerViewFriends);
        recyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewFriends.setLayoutManager(recyclerViewLayoutManager);

        adapter = new FriendsAdapter(getContext(), friendsList);
        recyclerViewFriends.setAdapter(adapter);

        return view;
    }

    private void getUsersRealTime() {
        db.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        // friendsList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            friendsList.add(doc.toObject(User.class));
                            adapter.setFriendsList(friendsList);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}