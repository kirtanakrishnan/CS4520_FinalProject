package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.project.Fragments.AddFriendsFragment;
import com.example.project.Fragments.AddPostFragment;
import com.example.project.Fragments.EditProfileFragment;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PagingCursorbased;
import se.michaelthelin.spotify.model_objects.specification.PlayHistory;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;
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

    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "http://localhost:8080/";
    private static final String CLIENT_ID = "cdf1584220164b2ab8d3ba003d6b350b";
    private SpotifyApi spotifyApi;
    private final ArrayList<Post> posts = new ArrayList<>();
    private String longitude;
    private String latitude;
    private String location;
    private LocationRequest mLocationRequest;

    // initializing
    // FusedLocationProviderClient
    // object
    private FusedLocationProviderClient mFusedLocationClient;

    // Initializing other items
    // from layout file
    int PERMISSION_ID = 44;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setTitle("Sound Soulmates");

        bottomNavigationView
                = findViewById(R.id.bottomNavigationView);

        bottomNavigationView
                .setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.menuHome);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void connectToSpotifyButtonClicked() {
        authorizationRequest();
    }

    @Override
    public ArrayList<String> getTopTenSongs() {
        return getTopTenSongsHelper();
    }

    @Override
    public void logoutButtonClicked() {
        mAuth.signOut();
        currentUser = null;
        token = null;
        populateScreen();
    }

    @Override
    public void profileAvatarClicked() {

    }

    @Override
    public void editProfileButtonClicked() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainLayout, new EditProfileFragment(),"editProfileFragment")
                .commit();
    }

    private ArrayList<String> getTopTenSongsHelper() {
        spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(token)
                .build();

        GetUsersTopTracksRequest getUsersTopTracksRequest = spotifyApi.getUsersTopTracks()
                .limit(10)
                .offset(0)
                .time_range("medium_term")
                .build();
        ArrayList<String> tracks = new ArrayList<>();


        try {
            final CompletableFuture<Paging<Track>> pagingFuture = getUsersTopTracksRequest.executeAsync();

            final Paging<Track> trackPaging = pagingFuture.join();
            Track[] trackList = trackPaging.getItems();
            for(Track track: trackList) {
                ArtistSimplified[] artists = track.getArtists();
                tracks.add(track.getName() + " by " + artists[0].getName());
                System.out.println(track.getName() + " by " + artists[0].getName());
            }

        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
        return tracks;
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
                    ArrayList<String> topSongs = getTopTenSongs();
                    currentLocalUser.setTopTracks(topSongs);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.mainLayout, ProfileFragment.newInstance(currentLocalUser),"profileFragment")
                            .addToBackStack(null)
                            .commit();
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
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
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            currentLocalUser = task.getResult()
                                    .toObject(User.class);
                            //Populating The Main Fragment....
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.mainLayout, new HomeFragment(currentLocalUser),"homeFragment")
                                    .commit();
                            bottomNavigationView.setVisibility(View.VISIBLE);

                        }else{
                            mAuth.signOut();
                            currentUser = null;
                            populateScreen();
                        }
                    });
        }else{
//            The user is not logged in, load the login Fragment....
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainLayout, LandingFragment.newInstance(),"landingFragment")
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
                .addOnSuccessListener(unused -> {
//                      On success populate home screen...
                    populateScreen();
                })
                .addOnFailureListener(e -> {
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
                        .addToBackStack("addFriendsFragment")
                        .commit();
                return true;

           case R.id.menuHome:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainLayout, HomeFragment.newInstance(posts),
                                "homeFragment")
                        .addToBackStack("homeFragment")
                        .commit();
                return true;

            case R.id.menuProfile:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainLayout, ProfileFragment.newInstance(currentLocalUser),
                                "profileFragment")
                        .commit();
                return true;
        }
        return false;
    }


    @Override
    public void addSongButtonClicked() {
        getLocation();
    }
    private void setPostLocationAndPost() {
        Post post = getMostRecentSong();
        if(location != null) {
            post.setLocation("Boston, MA");
            post.setUsername(currentLocalUser.getName());

        }
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

            final PagingCursorbased<PlayHistory> playHistoryPagingCursorbased = pagingCursorbasedFuture.join();
            PlayHistory[] tracks = playHistoryPagingCursorbased.getItems();
            post.setSongTitle(tracks[0].getTrack().getName());
            ArtistSimplified[] artists = tracks[0].getTrack().getArtists();
            post.setSongArtist(artists[0].getName());
            String datePattern = "yyyy-MM-dd HH:mm:ss aaa";
            String date = new SimpleDateFormat(datePattern).format(new java.util.Date());
            post.setTimePosted(date);
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

    @SuppressLint("MissingPermission")
    private void getLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last location from FusedLocationClient object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location locationObj = task.getResult();
                    if (locationObj == null) {
                        requestNewLocationData();
                    } else {
                        latitude = String.valueOf(locationObj.getLatitude());
                        longitude = String.valueOf(locationObj.getLongitude());
                        location = latitude + " " + longitude;
                        setPostLocationAndPost();

                    }
                });
            }
            else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(5);
            mLocationRequest.setFastestInterval(0);
            mLocationRequest.setNumUpdates(1);
        }

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude = String.valueOf(mLastLocation.getLatitude());
            longitude = String.valueOf(mLastLocation.getLongitude());
            location = latitude + " " + longitude;
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
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