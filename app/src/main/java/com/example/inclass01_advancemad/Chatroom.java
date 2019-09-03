package com.example.inclass01_advancemad;

import java.io.Serializable;
import java.util.ArrayList;

public class Chatroom implements Serializable {
    public String chatroomId;
    public String chatroomName;
    public ArrayList<User> userList;
    //public ArrayList<Message> messageList;
    public String time;

    Chatroom()
    {
        this.userList=new ArrayList<>();
        //this.messageList= new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Chatroom{" +
                "chatroomId='" + chatroomId + '\'' +
                ", chatroomName='" + chatroomName + '\'' +
                ", userList=" + userList +
                ", time='" + time + '\'' +
                '}';
    }
}
