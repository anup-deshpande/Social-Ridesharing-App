package com.example.inclass01_advancemad;

public class Message {

    public String msgId;
    public String chatroomId;
    public String userId;
    public String userName;
    public String userProfile;
    public String msgImageUrl;
    public String msgText;
    public String msgTime;

    @Override
    public String toString() {
        return "Message{" +
                "msgId='" + msgId + '\'' +
                ", chatroomId='" + chatroomId + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userProfile='" + userProfile + '\'' +
                ", msgImageUrl='" + msgImageUrl + '\'' +
                ", msgText='" + msgText + '\'' +
                ", msgTime='" + msgTime + '\'' +
                '}';
    }
}
