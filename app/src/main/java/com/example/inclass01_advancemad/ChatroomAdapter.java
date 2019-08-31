package com.example.inclass01_advancemad;

import android.accessibilityservice.AccessibilityService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

    public class ChatroomAdapter extends RecyclerView.Adapter<ChatroomAdapter.ViewHolder>{


        static ArrayList<Chatroom> chatroomArrayList;
        static IDoTask task_interface;
        static int pos;

        static Chatroom chatroom;
        public ChatroomAdapter(ArrayList<Chatroom> chatroomArrayList, IDoTask i_task)
        {
            this.chatroomArrayList=chatroomArrayList;
            this.task_interface = i_task;
        }
        @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.chatroom_items,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        System.out.println("position" + position);
        //pos = position;
        chatroom= chatroomArrayList.get(position);
        if(chatroom!=null) {
            holder.txtChatroomName.setText(chatroom.chatroomName.toString().trim());
            holder.imgJoinChatroom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //System.out.println("position" + position);
                    task_interface.joinChatroom(chatroomArrayList.get(position));


                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return chatroomArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtChatroomName;
        ImageView imgJoinChatroom;

        public ViewHolder(View itemView){
            super(itemView);
            txtChatroomName= (TextView)itemView.findViewById(R.id.chatroom_item_name);
            imgJoinChatroom= (ImageView)itemView.findViewById(R.id.chatroom_join_button);


        }
    }



}
