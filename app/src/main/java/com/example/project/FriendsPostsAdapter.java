package com.example.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.Model.Post;

import java.util.ArrayList;
import java.util.List;

public class FriendsPostsAdapter extends RecyclerView.Adapter<FriendsPostsAdapter.PostViewHolder> {
    List<Post> postsList = new ArrayList<Post>();

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_row, parent, false);

        return new PostViewHolder(itemView);    }

    @Override
    public void onBindViewHolder(@NonNull FriendsPostsAdapter.PostViewHolder holder, int position) {
        Post post = postsList.get(position);
        holder.postUsername.setText(post.getUsername());
        holder.postSongName.setText(post.getSongTitle());
        holder.postSongArtist.setText(post.getSongArtist());
        holder.timePosted.setText(post.getTimePosted());
        holder.likeButton.findViewById(R.id.imageViewLikeButton);
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public TextView postUsername, postSongName, postSongArtist, timePosted, postLocation;
        public ImageView likeButton;



        public PostViewHolder(View view) {
            super(view);
            postUsername = view.findViewById(R.id.textViewUsername);
            postSongName = view.findViewById(R.id.textViewSongName);
            postSongArtist = view.findViewById(R.id.textViewArtist);
            timePosted = view.findViewById(R.id.textViewTimePosted);
            postLocation = view.findViewById(R.id.textViewLocation);
            likeButton = view.findViewById(R.id.imageViewLikeButton);

            // initialize other views in the row as necessary
        }
    }

}
