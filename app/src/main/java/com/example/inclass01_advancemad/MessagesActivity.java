package com.example.inclass01_advancemad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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

    ArrayList<User> UserList;
    ArrayList<User> activeUserList;
    RecyclerView activeUserrecyclerView;
    RecyclerView.Adapter activeUser_Rec_Adapter;
    RecyclerView.LayoutManager activeUser_Rec_Layout;


    final int PICK_IMAGE_REQUEST = 71;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
        UserList = new ArrayList<>();
        activeUserList = new ArrayList<>();
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
        recyclerView.setAdapter(rec_adapter);
        activeUserrecyclerView = findViewById(R.id.recyclerActiveUsersList);
        activeUserrecyclerView.setHasFixedSize(true);
        activeUser_Rec_Layout = new LinearLayoutManager(MessagesActivity.this,LinearLayoutManager.HORIZONTAL,false);
        activeUserrecyclerView.setLayoutManager(activeUser_Rec_Layout);

        activeUser_Rec_Adapter = new activeUserListAdapter(activeUserList);
        activeUserrecyclerView.setAdapter(activeUser_Rec_Adapter);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(newMessage.getText().toString().isEmpty()){
                    Toast.makeText(MessagesActivity.this,"Please enter message to send", Toast.LENGTH_SHORT).show();
                }else{
                    sendMessage();
                }

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
        getUserList();
        getActiveusers();



    }

    public void getUserList()
    {
        DatabaseReference myRef = mroot.child("Users/");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot);

                UserList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    User user = postSnapshot.getValue(User.class);
                    UserList.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void getActiveusers(){

        DatabaseReference msgRef = mroot.child("Chatroom/" + current_chatroom.chatroomId + "/activeUserList");
        msgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {

                    activeUserList.clear();

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        activeUserList.add(user);
                    }

                    activeUser_Rec_Adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void sortlist()
    {

        Collections.sort(msg_list, new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                Date m2_date=Calendar.getInstance().getTime();;
                Date m1_date=Calendar.getInstance().getTime();;
                try {
                   m1_date=dateFormat.parse(m1.msgTime);
                   m2_date=dateFormat.parse(m2.msgTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return m1_date.compareTo(m2_date);
            }
        });
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
                    }
                }
                sortlist();
                Parcelable recyclerViewState;
                recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

                //recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount()-1);
                //rec_adapter.notifyDataSetChanged();
                recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                rec_adapter.notifyDataSetChanged();
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
            setTitle(current_chatroom.chatroomName);
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
                    newMessage.setText("");


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
            newMessage.setText("");

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

    @Override
    public void deleteMessage(Message message) {
        StorageReference storageReference=firebaseStorage.getReference();
        final DatabaseReference deleteRef=mroot.child("Messages/" + message.chatroomId + "/" + message.msgId);

        if(!message.msgImageUrl.equals("NoImage"))
        {

            StorageReference setMsgRef=storageReference.child("ChatroomMessages/"+ message.chatroomId + "_" + message.msgId + ".jpg");
            setMsgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    deleteRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });


        }
        else
        {
            deleteRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                      Log.d("delete","NOT DELETED");
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DatabaseReference activeUser_ref = mroot.child("Chatroom/" + current_chatroom.chatroomId + "/activeUserList");
        activeUser_ref.child(""+current_user.userId).removeValue();
    }


}
