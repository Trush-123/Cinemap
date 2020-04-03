package com.example.cinemap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class user_list_activity extends AppCompatActivity
{
    Button Back;
    RecyclerView UserListDisplay;
    List<User_List_Movie> UserList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_activity);

        //variable declaration
        Back=findViewById(R.id.user_list_back);
        UserListDisplay=findViewById(R.id.user_list);
        Back.setOnClickListener(v ->
        {
            finish();
        });
        UserList=new ArrayList<>();
        Intent intent=getIntent();
        String uid=intent.getStringExtra("uid");
        FirebaseFirestore Database=FirebaseFirestore.getInstance();
        //gets the users list form the firebase database add every movie to a list and sends it to the recycler view
        Query query = Database.collection("Lists").document(uid+"List").collection("Movies");
        query.get().addOnSuccessListener(queryDocumentSnapshots ->
        {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
            {
                User_List_Movie Movie=documentSnapshot.toObject(User_List_Movie.class);
                UserList.add(Movie);
            }
                UserListActivityAdapter ListAdapter=new UserListActivityAdapter(UserList,this);
                UserListDisplay.setAdapter(ListAdapter);
                UserListDisplay.setLayoutManager(new GridLayoutManager(this,2));
        });
    }
}
