package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements IFragmentCommunication,
        BottomNavigationView
                .OnNavigationItemSelectedListener{

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private User currentLocalUser;
    private FirebaseFirestore db;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        bottomNavigationView
                = findViewById(R.id.bottomNavigationView);


    }

    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        populateScreen();
    }

    private void populateScreen() {
        //      Check for Authenticated users ....
        if(currentUser != null){
//            The user is authenticated, fetching the details of the current user from Firebase...
            db.collection("users")
                    .document(currentUser.getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                currentLocalUser = task.getResult()
                                        .toObject(User.class);
//                                    Log.d(Tags.TAG, "Current user: "+currentLocalUser.toString());
                                //Populating The Main Fragment....
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragmentContainerView, new HomeFragment(currentLocalUser),"homeFragment")
                                        .commit();

                            }else{
                                mAuth.signOut();
                                currentUser = null;
                                populateScreen();
                            }
                        }
                    });
        }else{
//            The user is not logged in, load the login Fragment....
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, LandingFragment.newInstance(),"landingFragment")
                    .commit();
            bottomNavigationView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void populateHomeFragment(FirebaseUser mUser) {
        this.currentUser = mUser;
        populateScreen();
    }

    @Override
    public void populateSignUpFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, SignUpFragment.newInstance(),"signUpFragment")
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void registerDone(FirebaseUser firebaseUser, User user) {
        this.currentUser = firebaseUser;
//        Updating the Firestore structure....
            updateFirestoreWithUserDetails(user);
    }

    private void updateFirestoreWithUserDetails(User user) {
        db.collection("users")
                .document(user.getEmail())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
//                      On success populate home screen...
                        // Log.d(Tags.TAG, "onSuccess: updated data");
                        populateScreen();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log.e(Tags.TAG, "onFailure: "+e.getMessage());
                    }
                });
    }

    public void navigationView(@NonNull MenuItem item) {
        onNavigationItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuFriends:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, AddFriendsFragment.newInstance(),
                                "addFriendsFragment")
                        .commit();
                Log.d("demo", "went to add friends fragment" );
                return true;

           case R.id.menuHome:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, HomeFragment.newInstance(),
                                "homeFragment")
                        .commit();
                return true;

            case R.id.menuProfile:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, ProfileFragment.newInstance(),
                                "profileFragment")
                        .commit();
                return true;
        }
        return false;
    }


}