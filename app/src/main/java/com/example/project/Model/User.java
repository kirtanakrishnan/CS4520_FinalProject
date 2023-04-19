package com.example.project.Model;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class User {

    private String name;
    private String email;
    private String imageuri;
    private ArrayList<String> topTracks;

    public User(){

    }

    public ArrayList<String> getTopTracks() {
        return topTracks;
    }

    public void setTopTracks(ArrayList<String> topTracks) {
        this.topTracks = topTracks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageuri() {
        return imageuri;
    }

    public void setImageuri(String imageuri) {
        this.imageuri = imageuri;
    }

    public User(String name, String email, String imageuri) {
        this.name = name;
        this.email = email;
        this.imageuri = imageuri;
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        User other = (User) obj;
        return getEmail().equals(other.getEmail());
    }

    @Override
    public int hashCode() {
        return getEmail().hashCode();
    }
}
