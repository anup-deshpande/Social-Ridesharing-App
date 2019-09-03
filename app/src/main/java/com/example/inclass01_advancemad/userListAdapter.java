package com.example.inclass01_advancemad;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class userListAdapter extends RecyclerView.Adapter<userListAdapter.ViewHolder> {

    static ArrayList<User> usersArrayList;
    static int position;
    private static Context mcon;

    static User user;
    public userListAdapter(ArrayList<User> userArrayList)
    {
        this.usersArrayList=userArrayList;
    }

    @NonNull
    @Override
    public userListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        mcon = parent.getContext();
        View view= LayoutInflater.from(mcon).inflate(R.layout.userlist_items,parent,false);
        userListAdapter.ViewHolder viewHolder=new userListAdapter.ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final userListAdapter.ViewHolder holder, final int position) {


        user= usersArrayList.get(position);
        if(user!=null) {
            holder.user_name.setText(user.firstName + " "+user.lastName);
            Picasso.get().load(user.imageUrl).into(holder.user_image);
        }
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView user_name;
        ImageView user_image;


        public ViewHolder(View itemView){
            super(itemView);
            user_name= itemView.findViewById(R.id.user_name);
            user_image = itemView.findViewById(R.id.user_profileimage);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            System.out.println("Get adapter position=============== " + getAdapterPosition());
            user = usersArrayList.get(getAdapterPosition());

            Intent showProfileIntent = new Intent(mcon, ShowProfileUser.class);
            showProfileIntent.putExtra("UserId", user.userId);
            mcon.startActivity(showProfileIntent);
        }
    }

    public interface onUserListener{
        void onUserClick(int position);
    }

}
