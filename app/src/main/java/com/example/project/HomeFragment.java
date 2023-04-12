package com.example.project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {


    private User currentLocalUser;
    private BottomNavigationView bottomNavigationView;
    private Button logoutButton;
    private IFragmentCommunication mListener;
    public HomeFragment() {
        // Required empty public constructor
    }
    public HomeFragment(User currentLocalUser) {
        this.currentLocalUser = currentLocalUser;
    }


    // TODO: Rename and change types and number of parameters
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



        return view;
    }


    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuFriends:
                mListener.navigationView(item);
                Log.d("demo", "went to add friends fragment" );
                return true;

            case R.id.menuHome:
                mListener.navigationView(item);
                return true;

            case R.id.menuProfile:
                mListener.navigationView(item);
                return true;
        }
        return false;
    }
}