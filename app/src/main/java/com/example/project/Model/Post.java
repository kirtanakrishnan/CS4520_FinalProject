package com.example.project.Model;

import android.widget.ImageView;

public class Post {
    private String username;
    private String songTitle;
    private String songArtist;
    private String timePosted;
    private String location;

    private ImageView likeButton;


    public Post() {}

    public Post(String username, String songTitle, String songArtist, String timePosted, String location) {
        this.username = username;
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.timePosted = timePosted;
        this.location = location;
        this.likeButton = likeButton;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getTimePosted() {
        return timePosted;
    }

    public void setTimePosted(String timePosted) {
        this.timePosted = timePosted;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ImageView getLikeButton() {
        return likeButton;
    }

    public void setLikeButton() {
        this.likeButton = likeButton;
    }
}
