package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.project.Fragments.AddFriendsFragment;
import com.example.project.Fragments.AddPostFragment;
import com.example.project.Fragments.HomeFragment;
import com.example.project.Fragments.LandingFragment;
import com.example.project.Fragments.PostFragment;
import com.example.project.Fragments.ProfileFragment;
import com.example.project.Fragments.SignUpFragment;
import com.example.project.Interfaces.IFragmentCommunication;
import com.example.project.Interfaces.IHomeToMain;
import com.example.project.Interfaces.IAddPostToMain;
import com.example.project.Interfaces.IPostToMain;
import com.example.project.Interfaces.IProfileToMain;
import com.example.project.Model.Post;
import com.example.project.Model.User;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PagingCursorbased;
import se.michaelthelin.spotify.model_objects.specification.PlayHistory;
import se.michaelthelin.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;

public class MainActivity extends AppCompatActivity implements IFragmentCommunication, IProfileToMain,
        IAddPostToMain, IHomeToMain, IPostToMain, BottomNavigationView
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
    private static final String REDIRECT_URI = "http://localhost:8080/";
    private static final String CLIENT_ID = "cdf1584220164b2ab8d3ba003d6b350b";
    private SpotifyApi spotifyApi;
    private ArrayList<Post> posts = new ArrayList<>();

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

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void connectToSpotifyButtonClicked() {
        authorizationRequest();
    }

    private void authorizationRequest() {
        String[] scopes = {"user-top-read", "user-read-recently-played"};
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(scopes);
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
                    Toast.makeText(this, "Connected to Spotify!", Toast.LENGTH_SHORT).show();
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    Log.d("demo", "FAILED TO CONNECT TO SPOTIFY");
                    Toast.makeText(this, "Failed to connect to Spotify", Toast.LENGTH_SHORT).show();

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
                        .replace(R.id.mainLayout, HomeFragment.newInstance(posts),
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
        Post post = getMostRecentSong();
        post.setUsername(currentLocalUser.getName());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainLayout, PostFragment.newInstance(post),
                        "postFragment")
                .commit();
    }

    private Post getMostRecentSong() {
        spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(token)
                .build();

        GetCurrentUsersRecentlyPlayedTracksRequest getCurrentUsersRecentlyPlayedTracksRequest =
                spotifyApi.getCurrentUsersRecentlyPlayedTracks()
//                  .after(new Date(1517087230000L))
//                  .before(new Date(1453932420000L))
                        .limit(1)
                        .build();
        Post post = new Post();


        try {
            final CompletableFuture<PagingCursorbased<PlayHistory>> pagingCursorbasedFuture = getCurrentUsersRecentlyPlayedTracksRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            final PagingCursorbased<PlayHistory> playHistoryPagingCursorbased = pagingCursorbasedFuture.join();
            PlayHistory[] tracks = playHistoryPagingCursorbased.getItems();
            post.setSongTitle(tracks[0].getTrack().getName());
            Log.d("demo", tracks[0].getTrack().getName());
            ArtistSimplified[] artists = tracks[0].getTrack().getArtists();
            post.setSongArtist(artists[0].getName());
            Log.d("demo", artists[0].getName());
            String date = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
            post.setTimePosted(date);
            Log.d("demo", date);
            return post;

        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        } catch(AssertionError e) {
            Toast.makeText(this, "You must connect to Spotify before posting", Toast.LENGTH_SHORT).show();
        }
        return post;
    }

    @Override
    public void postButtonClicked(Post post) {
        posts.add(post);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainLayout, HomeFragment.newInstance(posts),
                        "homeFragment")
                .commit();

    }

    @Override
    public void addPostButtonClicked() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainLayout, AddPostFragment.newInstance(),
                        "addPostFragment")
                .commit();
    }
}