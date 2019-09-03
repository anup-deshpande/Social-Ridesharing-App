package com.example.inclass01_advancemad;

import android.accessibilityservice.AccessibilityService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

    public class ChatroomAdapter extends RecyclerView.Adapter<ChatroomAdapter.ViewHolder>{

        static ArrayList<Chatroom> chatroomArrayList;
        static IDoTask task_interface;
        String userId;
        static Chatroom chatroom;
        public ChatroomAdapter(ArrayList<Chatroom> chatroomArrayList, IDoTask i_task, String userId)
        {
            this.chatroomArrayList=chatroomArrayList;
            this.task_interface = i_task;
            this.userId = userId;
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
            boolean flag= false;
            for(int i=0;i<chatroom.userList.size();i++)
            {
                if(userId.equals(chatroom.userList.get(i).userId))
                {
                    flag = true;
                    break;
                }

            }
            if(flag)
            {
                holder.imgJoinChatroom.setVisibility(View.INVISIBLE);
            }

            holder.imgJoinChatroom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //System.out.println("position" + position);
                    task_interface.joinChatroom(chatroomArrayList.get(holder.getAdapterPosition()));
                }
            });

            holder.txtChatroomName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chatroom = chatroomArrayList.get(holder.getAdapterPosition());
                    boolean flag = false;
                    for(int i=0;i<chatroom.userList.size();i++)
                    {
                        if(userId.equals(chatroom.userList.get(i).userId))
                        {
                            flag = true;
                            break;
                        }

                    }
                    if(flag == true)
                    {

                        task_interface.goInChatroom(chatroomArrayList.get(holder.getAdapterPosition()));
                    }else{
                        Toast.makeText(view.getContext(),"Please join the chatroom to read messages",Toast.LENGTH_SHORT).show();
                    }


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
