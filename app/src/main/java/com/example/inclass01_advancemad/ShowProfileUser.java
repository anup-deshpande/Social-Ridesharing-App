package com.example.inclass01_advancemad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ShowProfileUser extends AppCompatActivity {

    TextView userName, lastName, city, gender, email;
    Button btnBack;
    ImageView ivProfilePic;
    DatabaseReference ref;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;
    User userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile_user);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        setTitle("User Profile");

        userName = findViewById(R.id.tvUNameSP);
        lastName = findViewById(R.id.tvLNameSP);
        city = findViewById(R.id.tvCitySP);
        gender = findViewById(R.id.tvGenderSP);
        email = findViewById(R.id.tvEmailSP);
        ivProfilePic = findViewById(R.id.ivProfileSP);
        btnBack = findViewById(R.id.btnBack);

        final String userId = getIntent().getStringExtra("UserId");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference("Users/" + userId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userData = dataSnapshot.getValue(User.class);
                userName.setText(userData.getFirstName());
                lastName.setText(userData.getLastName());
                city.setText(userData.getCity());
                gender.setText(userData.getGender());
                email.setText(userData.getEmail());

                storageReference = FirebaseStorage.getInstance().getReference().child("MessageImages/" + userId + ".jpg");

                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            String downloadUrl = task.getResult().toString();
                            Picasso.get().load(downloadUrl).into(ivProfilePic);

                        } else {
                            Log.w("thank you", "Getting download url was not successful.",
                                    task.getException());
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("error",databaseError.getDetails());
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }
}
