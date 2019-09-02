package com.example.inclass01_advancemad;
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

    public MessageAdapter(ArrayList<Message> messageList)
    {
        this.message_list = messageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_items,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
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
            if(!holder.msg_image.equals("NoImage")) {
                Picasso.get().load(message.msgImageUrl).into(holder.msg_image);
            }
            Picasso.get().load(message.userProfile).into(holder.user_image);
        }
    }

    @Override
    public int getItemCount() {
        return message_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView user_image;
        ImageView msg_image;
        TextView msg_text;
        TextView msg_time;
        TextView user_name;
        public ViewHolder(View itemView){
            super(itemView);
            user_image = itemView.findViewById(R.id.msg_userProfile);
            msg_image = itemView.findViewById(R.id.msg_image);
            msg_text = itemView.findViewById(R.id.msg_text);
            msg_time = itemView.findViewById(R.id.msg_time);
            user_name = itemView.findViewById(R.id.msg_UserName);
        }
    }

}
