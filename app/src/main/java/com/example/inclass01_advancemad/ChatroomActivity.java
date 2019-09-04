package com.example.inclass01_advancemad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class ChatroomActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    DatabaseReference mroot;
    FirebaseStorage firebaseStorage;
    FirebaseDatabase database;
    Toolbar toolbar;
    NavigationView navigationView;
    View headerView;
    FirebaseAuth mauth;
    FirebaseUser firebaseUser;
    String userId;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mroot= FirebaseDatabase.getInstance().getReference();
        firebaseStorage=FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        mauth= FirebaseAuth.getInstance();
        firebaseUser=mauth.getCurrentUser();

        setUpNavigationBar();
        if(savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ChatroomFragment()).commit();
            navigationView.setCheckedItem(R.id.chatroom);
        }

        userId =firebaseUser.getUid();
        getUserDetails();
    }

    public void setUpNavigationBar()
    {
        drawer= findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    public void getUserDetails()
    {
        DatabaseReference myRef = database.getReference("Users/"+ userId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                user = dataSnapshot.getValue(User.class);

                TextView headerUserName = (TextView)headerView.findViewById(R.id.txtUserName_NAVHEADER);
                headerUserName.setText(user.firstName + " " + user.lastName);
                TextView headerEmail = (TextView)headerView.findViewById(R.id.txtUSEREMAIL_NAVHEADER);
                headerEmail.setText(user.email);
                final ImageView userProfile = (ImageView)headerView.findViewById(R.id.imgProfile_NAVHEADER);

                StorageReference storageReference=firebaseStorage.getReference();
                StorageReference setMsgRef=storageReference.child("MessageImages/" + userId + ".jpg");
                final long ONE_MEGABYTE = (1024 * 1024)*5;
                setMsgRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        userProfile.setImageBitmap(bmp);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(250, 300);
                        userProfile.setLayoutParams(layoutParams);

                        Log.d("xyz","Success");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        Log.d("xyz","FAILED");
                        // Handle any errors
                    }
                });




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    public void logout()
    {
        mauth.signOut();
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch(menuItem.getItemId())
        {

            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;

            case R.id.user:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new UserFragment()).commit();
                break;

            case R.id.chatroom:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ChatroomFragment()).commit();
                break;

            case R.id.logout:
                logout();
                break;

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
