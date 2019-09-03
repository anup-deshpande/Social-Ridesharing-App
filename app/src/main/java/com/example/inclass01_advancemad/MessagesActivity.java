package com.example.inclass01_advancemad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class MessagesActivity extends AppCompatActivity implements IMessageTasks{

    DatabaseReference mroot;
    FirebaseStorage firebaseStorage;
    FirebaseDatabase database;
    FirebaseAuth mauth;
    Uri filePath;
    String UserId;
    String imagePath;
    EditText newMessage;
    ImageView btnSendMessage;
    ImageView btnAddImage;
    Chatroom current_chatroom;
    String userId;
    User current_user;
    ArrayList<Message> msg_list;
    RecyclerView recyclerView;
    RecyclerView.Adapter rec_adapter;
    RecyclerView.LayoutManager rec_layout;
    final int PICK_IMAGE_REQUEST = 71;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);


        mroot= FirebaseDatabase.getInstance().getReference();
        firebaseStorage=FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        mauth=FirebaseAuth.getInstance();
        mauth.getCurrentUser();
        msg_list=new ArrayList<>();
        userId = mauth.getUid().toString();
        FirebaseUser firebaseUser=mauth.getCurrentUser();
        UserId=firebaseUser.getUid();
        newMessage = findViewById(R.id.editNewMessage);
        btnSendMessage = findViewById(R.id.imgSendMsg);
        btnAddImage= findViewById(R.id.imgAddImage);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerMessageList);
        recyclerView.setHasFixedSize(true);
        rec_layout=new LinearLayoutManager(MessagesActivity.this);
        recyclerView.setLayoutManager(rec_layout);
        rec_adapter=new MessageAdapter(msg_list,this, userId);

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();
            }
        });

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImage();
            }
        });
        getCurrentUser();
        getChatroomdetails();
        getMessages();

    }
    public void getMessages()
    {

        DatabaseReference msgRef = mroot.child("Messages/" + current_chatroom.chatroomId );
        msgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                msg_list.clear();
                if(dataSnapshot!=null) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Message message = child.getValue(Message.class);
                        Log.d("message" , message.toString());
                        msg_list.add(message);
                        recyclerView.setAdapter(rec_adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void addImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                btnAddImage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void getCurrentUser()
    {
        DatabaseReference myRef = mroot.child("Users/"+ userId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current_user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getChatroomdetails()
    {
        if(getIntent().getExtras()!=null)
        {
            current_chatroom = (Chatroom)getIntent().getExtras().getSerializable("chatroom");
        }
    }

    public void sendMessage()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = Calendar.getInstance().getTime();
        final String mydate = dateFormat.format(date);

        String messageId = UUID.randomUUID().toString();
        Message msg=new Message();
        msg.chatroomId = current_chatroom.chatroomId;
        msg.msgId =messageId;
        msg.userId = userId;
        msg.userName = current_user.firstName + " " + current_user.lastName;
        msg.msgText = newMessage.getText().toString();
        msg.msgTime = mydate;
        msg.userProfile = current_user.imageUrl;

        imagePath="ChatroomMessages/"+current_chatroom.chatroomId + "_" + messageId + ".jpg";
        addMessgaeImage(msg);
    }


    public void addMessgaeImage(final Message msg)
    {
        StorageReference storageReference=firebaseStorage.getReference();
        final StorageReference messageReference=storageReference.child(imagePath);
        UploadTask uploadTask=null;
        if(filePath!=null) {
            uploadTask= messageReference.putFile(filePath);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    messageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            msg.msgImageUrl = uri.toString();
                            DatabaseReference messages=mroot.child("Messages/" + current_chatroom.chatroomId + "/" + msg.msgId);
                            messages.setValue(msg);
                            filePath=null;
                        }
                    });

                    ImageView imgAddImage=(ImageView) findViewById(R.id.imgAddImage);
                    imgAddImage.setImageResource(R.drawable.addimage);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
        else
        {
            msg.msgImageUrl = "NoImage";
            DatabaseReference messages=mroot.child("Messages/" + current_chatroom.chatroomId + "/" + msg.msgId);
            messages.setValue(msg);

        }
    }

    @Override
    public void likeMessage(Message message) {
        if(!message.likeUsers.contains(current_user.userId)) {
            message.likeUsers.add(current_user.userId);
            DatabaseReference messages=mroot.child("Messages/" + current_chatroom.chatroomId + "/" + message.msgId);
            messages.setValue(message);
        }

    }

    @Override
    public void dislikeMessage(Message message) {
        if(message.likeUsers.contains(current_user.userId)) {
            message.likeUsers.remove(current_user.userId);
            DatabaseReference messages=mroot.child("Messages/" + current_chatroom.chatroomId + "/" + message.msgId);
            messages.setValue(message);
        }
    }
}
