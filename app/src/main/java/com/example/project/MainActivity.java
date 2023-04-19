package com.example.project;

import static com.spotify.sdk.android.auth.AccountsQueryParameters.CLIENT_ID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.project.Interfaces.IPostToMain;
import com.example.project.Interfaces.IProfileToMain;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements IFragmentCommunication, IProfileToMain,
        IPostToMain, BottomNavigationView
                .OnNavigationItemSelectedListener{

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private User currentLocalUser;
    private FirebaseFirestore db;
    private BottomNavigationView bottomNavigationView;
    private String token;
    private static final String RECENTLY_PLAYED = "https://api.spotify.com/v1/me/player/recently-played";



    // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "http://localhost:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        bottomNavigationView
                = findViewById(R.id.bottomNavigationView);

        bottomNavigationView
                .setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.menuHome);
        // Get the FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Create a new instance of the fragment you want to display
        LandingFragment landingFragment = new LandingFragment();

        // Replace the current fragment with the new one
        fragmentManager.beginTransaction()
                .replace(R.id.mainLayout, landingFragment)
                .commit();


    }


    private void connected() {
        // Then we will write some more code here.
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Aaand we will finish off here.
    }

    public void connectToSpotifyButtonClicked() {
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    token = response.getAccessToken();
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    Log.d("demo", "FAILED TO CONNECT TO SPOTIFY");
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
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
                                        .replace(R.id.mainLayout, new HomeFragment(currentLocalUser),"homeFragment")
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
                .replace(R.id.mainLayout, SignUpFragment.newInstance(),"signUpFragment")
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
        Log.d("demo", "NAV BAR ITEM SELECTED");
        switch (item.getItemId()) {
            case R.id.menuFriends:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainLayout, AddFriendsFragment.newInstance(),
                                "addFriendsFragment")
                        .commit();
                Log.d("demo", "went to add friends fragment" );
                return true;

           case R.id.menuHome:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainLayout, HomeFragment.newInstance(),
                                "homeFragment")
                        .commit();
                return true;

            case R.id.menuProfile:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainLayout, ProfileFragment.newInstance(),
                                "profileFragment")
                        .commit();
                return true;
        }
        return false;
    }


    @Override
    public void addSongButtonClicked() {
        OkHttpClient client = new OkHttpClient();
        
    }

    @Override
    public void postButtonClicked() {

    }
}