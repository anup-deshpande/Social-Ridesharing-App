package com.example.inclass01_advancemad;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class ProfileFragment extends Fragment {

    TextView userName, lastName, city;
    Button btnUpdate, btnCancel;
    ImageView ivProfilePic;
    Spinner spinnerGender;
    DatabaseReference ref;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;
    final int PICK_IMAGE_REQUEST = 71;
    Uri filePath;
    User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile,container,false);

        getActivity().setTitle(R.string.str_labelProfile);

        userName = v.findViewById(R.id.etUname);
        lastName = v.findViewById(R.id.etLastName);
        city = v.findViewById(R.id.etCity);
        spinnerGender = v.findViewById(R.id.spinnerGender);
        ivProfilePic = v.findViewById(R.id.ivProfilePicture);
        btnUpdate = v.findViewById(R.id.btnUpdate);
        btnCancel = v.findViewById(R.id.btnCancel);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference("Users/" + firebaseAuth.getUid());

        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent( getActivity(), ChatroomActivity.class));
            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                user = dataSnapshot.getValue(User.class);
                userName.setText(user.getFirstName());
                lastName.setText(user.getLastName());
                city.setText(user.getCity());

                spinnerGender.setSelection(((ArrayAdapter) spinnerGender.getAdapter()).getPosition(user.getGender()));
                storageReference = FirebaseStorage.getInstance().getReference().child("MessageImages/" + firebaseAuth.getUid() + ".jpg");

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


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User userData = user;
                userData.setFirstName(userName.getText().toString());
                userData.setLastName(lastName.getText().toString());
                userData.setGender(spinnerGender.getSelectedItem().toString());
                userData.setCity(city.getText().toString());

                updateUserImage(userData);

            }
        });

        return v;
    }

    public void updateUserImage(final User userData) {

        if(filePath != null) {

            UploadTask uploadTask = null;
            uploadTask = storageReference.putFile(filePath);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            userData.setImageUrl(uri.toString().trim());
                            Log.d("xyz", "success");

                            ref.setValue(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getActivity(), "The user has been updated.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("xyz", "Failed");
                }
            });

        } else {

            ref.setValue(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getActivity(), "The user has been updated.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                Log.d("str",bitmap.toString());

                ivProfilePic.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
