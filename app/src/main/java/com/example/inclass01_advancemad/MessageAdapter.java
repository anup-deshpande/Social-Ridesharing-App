package com.example.inclass01_advancemad;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    ArrayList<Message> message_list;
    Message message;
    IMessageTasks msg_interface;
    String userId;

    public MessageAdapter(ArrayList<Message> messageList, IMessageTasks messageInterface, String userId)
    {
        this.message_list = messageList;
        this.msg_interface = messageInterface;
        this.userId= userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_items,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        message = message_list.get(position);
        if(message!=null)
        {
            try {
                Date date=dateFormat.parse(message.msgTime.toString());
                PrettyTime pt=new PrettyTime();
                holder.msg_time.setText(pt.format(date));

            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.user_name.setText(message.userName.trim());
            holder.msg_text.setText(message.msgText.trim());
            if(message.userId.equals(userId))
            {
                holder.msg_delete.setVisibility(View.VISIBLE);
            }else{
                holder.msg_delete.setVisibility(View.INVISIBLE);
            }


            if(message.likeUsers.contains(userId))
            {
                holder.msg_like.setImageResource(R.drawable.imglike);
            }else
            {
                holder.msg_like.setImageResource(R.drawable.imgnotlike);
            }

            if(message.likeUsers!=null)
            {
                holder.msg_likeCount.setText(String.valueOf(message.likeUsers.size()));
            }


            if(!message.msgImageUrl.equals("NoImage")) {
                holder.msg_image.setVisibility(View.VISIBLE);
                Picasso.get().load(message.msgImageUrl).into(holder.msg_image);
            }else
            {
               // holder.msg_image.setImageResource(null);
                holder.msg_image.setVisibility(View.GONE);
            }
            Picasso.get().load(message.userProfile).into(holder.user_image);
        }
        holder.msg_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(message_list.get(position).likeUsers.contains(userId))
                {

                    //Log.d("click", "CLICKED NOT LIKE");
                    holder.msg_like.setImageResource(R.drawable.imgnotlike);
                    msg_interface.dislikeMessage(message_list.get(position));
                }
                else {

                    //Log.d("click", "CLICKED LIKE");
                    holder.msg_like.setImageResource(R.drawable.imglike);
                    msg_interface.likeMessage(message_list.get(position));
                }

            }
        });

        holder.msg_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(message_list.size()!=0)
                {
                    msg_interface.deleteMessage(message_list.get(position));
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return message_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView user_image;
        ImageView msg_image;
        ImageView msg_like;
        ImageView msg_delete;
        TextView msg_text;
        TextView msg_time;
        TextView user_name;
        TextView msg_likeCount;

        public ViewHolder(View itemView){
            super(itemView);
            user_image = itemView.findViewById(R.id.msg_userProfile);
            msg_like = itemView.findViewById(R.id.msg_LikeButton);
            msg_likeCount= itemView.findViewById(R.id.msgLikeCount);
            msg_delete = itemView.findViewById(R.id.msg_delete);
            msg_image = itemView.findViewById(R.id.msg_image);
            msg_text = itemView.findViewById(R.id.msg_text);
            msg_time = itemView.findViewById(R.id.msg_time);
            user_name = itemView.findViewById(R.id.msg_UserName);
        }
    }

}
