package com.example.project;

import android.view.MenuItem;

import com.google.firebase.auth.FirebaseUser;

public interface IFragmentCommunication {
    void populateHomeFragment(FirebaseUser mUser);
    void populateSignUpFragment();

    void registerDone(FirebaseUser firebaseUser, User user);

    void navigationView(MenuItem item);
}
