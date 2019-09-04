package com.example.inclass01_advancemad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth mauth;
    FirebaseAuth.AuthStateListener mAuthListner;
    Button btnLogin;
    Button btnSignUp;

    TextView forgotPasswordText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        forgotPasswordText = findViewById(R.id.forgot_password);
        btnLogin=(Button) findViewById(R.id.btnLogin);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        mauth = FirebaseAuth.getInstance();
        setTitle("Login");

        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotPasswordIntent = new Intent(getApplicationContext(),forgot_password.class);
                startActivity(forgotPasswordIntent);
            }
        });


        mAuthListner=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser()!=null) // Now user is logged in
                {

                    startActivity(new Intent(MainActivity.this,ChatroomActivity.class));
                }
            }
        };

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);

            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mauth.getCurrentUser();
        if(currentUser!=null){
            Intent chatroom_intent=new Intent(getApplicationContext(),ChatroomActivity.class);
            startActivity(chatroom_intent);
        }
    }


    public void Login()
    {
        EditText editEmail=(EditText)findViewById(R.id.editEmail);
        EditText editPassword=(EditText)findViewById(R.id.editPassword);
        TextInputLayout editEmailLayout = findViewById(R.id.email_text_input_layout);

        String email=editEmail.getText().toString();
        String password=editPassword.getText().toString();
        if (editEmail.getText().toString().trim().length() == 0) {
            Toast.makeText(MainActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
        }

        else if (editPassword.getText().toString().trim().length() == 0) {
            Toast.makeText(MainActivity.this, "Enter a password !!", Toast.LENGTH_SHORT).show();
        }

        else {
            mauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Log.d("xyz","userAdded");
                        startActivity(new Intent(MainActivity.this, ChatroomActivity.class));
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "User is invalid..Please Sign Up", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }
}
