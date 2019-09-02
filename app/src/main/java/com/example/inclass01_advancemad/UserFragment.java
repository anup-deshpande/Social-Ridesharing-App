package com.example.inclass01_advancemad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserFragment extends Fragment{


    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mauth;
    private String userId;
    private ArrayList<User> usersArrayList;
    private RecyclerView userListRecyclerView;
    private RecyclerView.Adapter userListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private User current_user;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user,container,false);
        getActivity().setTitle(R.string.user_list);

        databaseReference= FirebaseDatabase.getInstance().getReference();
        mauth= FirebaseAuth.getInstance();
        firebaseUser=mauth.getCurrentUser();
        userId =firebaseUser.getUid();
        usersArrayList = new ArrayList<>();

        userListRecyclerView = view.findViewById(R.id.recyclerUserList);
        userListRecyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(getContext());
        userListRecyclerView.setLayoutManager(layoutManager);
        userListAdapter=new userListAdapter(usersArrayList);
        userListRecyclerView.setAdapter(userListAdapter);
        getUserList();


        return view;
    }


    public void getUserList()
    {
        DatabaseReference myRef = databaseReference.child("Users/");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot);

                usersArrayList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    User user = postSnapshot.getValue(User.class);
                    usersArrayList.add(user);
                }

                userListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
