package com.example.inclass01_advancemad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class activeUserListAdapter extends RecyclerView.Adapter<activeUserListAdapter.ViewHolder> {

    static ArrayList<User> activeUsersArrayList;
    static int position;



    static User user;
    public activeUserListAdapter(ArrayList<User> userArrayList)
    {
        this.activeUsersArrayList = userArrayList;
    }
    @NonNull
    @Override
    public activeUserListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.active_userlist_items,parent,false);
        activeUserListAdapter.ViewHolder viewHolder=new activeUserListAdapter.ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final activeUserListAdapter.ViewHolder holder, final int position) {


        user= activeUsersArrayList.get(position);
        if(user!=null) {
            Picasso.get().load(user.imageUrl).into(holder.user_image);
        }



    }

    @Override
    public int getItemCount() {
        return activeUsersArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        ImageView user_image;


        public ViewHolder(View itemView){
            super(itemView);
            user_image = itemView.findViewById(R.id.user_profileimage);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            System.out.println("Get adapter position" + getAdapterPosition());
        }
    }

    public interface onUserListener{
        void onUserClick(int position);
    }

}
