package com.example.inclass01_advancemad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {

    ImageView userProfile;
    Spinner spinnerGender;
    Button btnSignUp;
    Button btnBack;
    EditText editFirstName;
    EditText editLastName;
    EditText editCity;
    EditText editEmail;
    EditText editPassword;
    EditText editRetypePassword;
    FirebaseAuth mauth;
    DatabaseReference mroot;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String imagePath;
    String userId;

    final int PICK_IMAGE_REQUEST = 71;
    Uri filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");

        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        mroot= FirebaseDatabase.getInstance().getReference();
        mauth=FirebaseAuth.getInstance();
        spinnerGender = (Spinner)findViewById(R.id.user_genderSpinner);
        userProfile = (ImageView)findViewById(R.id.user_PROFILE);
        btnSignUp = (Button)findViewById(R.id.buttonSignUp);
        btnBack = (Button)findViewById(R.id.buttonBacktoMain);
        editFirstName = (EditText)findViewById(R.id.user_FirstName);
        editLastName = (EditText) findViewById(R.id.user_LastName);
        editCity = (EditText) findViewById(R.id.userCIty);
        editEmail = (EditText)findViewById(R.id.user_editEmail);
        editPassword = (EditText)findViewById(R.id.user_editPassword);
        editRetypePassword = (EditText)findViewById(R.id.user_editRetypePass);
        //userId = UUID.randomUUID().toString();


        fillDropdown();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void signUp()
    {
        final String Name=editFirstName.getText().toString();
        final String lastName=editLastName.getText().toString();
        final String city = editCity.getText().toString();
        final String gender = spinnerGender.getSelectedItem().toString();
        final String Email=editEmail.getText().toString();
        final String Password=editPassword.getText().toString();
        final String ConfirmPass=editRetypePassword.getText().toString();

        if(TextUtils.isEmpty(Name)) {
            editFirstName.setError("Enter First Name");

        }
        else if(TextUtils.isEmpty(lastName)){
            editLastName.setError("Enter Last Name");
        }

        else if(TextUtils.isEmpty(Email)){
            editEmail.setError("Enter Email");
        }
        else if(TextUtils.isEmpty(Password)){
            editPassword.setError("Enter Password");
        }
        else if(TextUtils.isEmpty(ConfirmPass)){
            editRetypePassword.setError("Enter Confirm Password");
        }
        else if(!Password.equals(ConfirmPass))
        {
            Toast.makeText(SignUpActivity.this,"Password do not match confirm Password" , Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(city)){
            editCity.setError("Enter City");
        }
        else if(filePath ==null)
        {
            Toast.makeText(SignUpActivity.this,"Please select user profile" , Toast.LENGTH_SHORT).show();
        }
        else {


            mauth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        userId = mauth.getCurrentUser().getUid();

                        User user = new User();
                        user.userId = userId;
                        user.firstName = Name;
                        user.lastName = lastName;
                        user.email = Email;
                        user.password = Password;
                        user.city = city;
                        user.gender = gender;
                        user.imageUrl = imagePath;

                        addUserImage(user);



                    }

                }
            });

        }





    }
    public void addUserImage(final User user)
    {

        imagePath="MessageImages/" + userId +".jpg";
        final StorageReference userImageReference = storageReference.child(imagePath);
        UploadTask uploadTask = null;
        uploadTask= userImageReference.putFile(filePath);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                userImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference users = mroot.child("Users");
                        DatabaseReference current_user = users.child(userId);
                        user.imageUrl= uri.toString();
                        current_user.setValue(user);
                        Toast.makeText(SignUpActivity.this, "The user has been created", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, ChatroomActivity.class));
                        //System.out.println("DOWNLOAD URL " + uri.toString());
                    }
                });
               // System.out.println("DOWNLOAD URL " + userImageReference.getDownloadUrl().toString());
                Log.d("xyz","success");



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("xyz","Failed");
            }
        });




    }
    public void fillDropdown()
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinnerValues, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
    }

    private void chooseImage() {
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
                Log.d("str",bitmap.toString());

                //ImageView imgAddImage=(ImageView) findViewById(R.id.imgAddImage);
                userProfile.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
