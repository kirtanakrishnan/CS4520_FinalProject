package com.example.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.Model.Post;
import com.example.project.Model.User;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>{
    private Context mContext;
    private ArrayList<User> friendsList;

    public FriendsAdapter(Context mContext, ArrayList<User> friendsList) {
        this.mContext = mContext;
        this.friendsList = friendsList;
    }

    @NonNull
    @Override
    public FriendsAdapter.FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_row, parent, false);

        return new FriendsAdapter.FriendsViewHolder(itemView);
    }

    public ArrayList<User> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(ArrayList<User> friendsList) {
        this.friendsList = friendsList;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.FriendsViewHolder holder, int position) {
        User friend = friendsList.get(position);
        holder.nameText.setText(friend.getName());

    }
    public void setUsers(ArrayList<User> friendsList) {
        this.friendsList = friendsList;
    }

    @Override
    public int getItemCount() {
        if (friendsList == null) {
            return 0;
        } else {
            return friendsList.size();
        }
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        public TextView name, nameText;

        public FriendsViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            nameText = view.findViewById(R.id.nameText);

        }
    }
}
