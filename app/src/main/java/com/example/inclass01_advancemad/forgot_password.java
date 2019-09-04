package com.example.inclass01_advancemad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgot_password extends AppCompatActivity {

    Button submitButton;
    Button cancelButton;
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setTitle(R.string.btnForgotPasswordStr);

        cancelButton = findViewById(R.id.btn_cancel);
        submitButton = findViewById(R.id.btn_submit);
        email = findViewById(R.id.email);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!email.getText().toString().equals("")){
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Intent goBacktoLoginIntent = new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(goBacktoLoginIntent);
                                    }
                                }
                            });
                }
                else{
                    email.setError("Please enter email");
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goBacktoLoginIntent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(goBacktoLoginIntent);
            }
        });



    }
}
