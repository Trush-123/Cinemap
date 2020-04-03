package com.example.cinemap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

public class User_Profile_Activity extends AppCompatActivity implements View.OnClickListener
{
    ImageView ProfilePicture;
    TextView UserName;
    TextView Email;
    TextView Profile_Description;
    Button UserList;
    Button Back;
    Intent intent2;
    DocumentReference UserDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__profile);

        //variable declaration
        ProfilePicture=findViewById(R.id.profile_image);
        UserName=findViewById(R.id.user_name);
        Email=findViewById(R.id.user_mail);
        Profile_Description=findViewById(R.id.user_desc);
        UserList=findViewById(R.id.user_list_button);
        Back=findViewById(R.id.back_profile_button);
        //gets the users uid from user search activity
        Intent intent=getIntent();
        String uid=intent.getStringExtra("uid");
        //intent to move to the users list activity
        intent2=new Intent(this,user_list_activity.class);
        intent2.putExtra("uid",uid);
        //gets the users data from the FireStore Database and displays them
        FirebaseFirestore Database=FirebaseFirestore.getInstance();
        UserDocRef=Database.collection("Users").document(uid);
        Back.setOnClickListener(this);
        UserList.setOnClickListener(this);

    }

    @Override
    protected void onStart() //snapshot listener for automatic updating
    {
        UserDocRef.addSnapshotListener(this, (documentSnapshot, e) ->
        {
            Email.setText("Email: "+documentSnapshot.getString("Email"));
            UserName.setText("Username: "+documentSnapshot.getString("UserName"));
            Profile_Description.setText("Description: "+documentSnapshot.getString("User_Description"));
            Glide.with(this).load(documentSnapshot.getString("Profile_Image")).into(ProfilePicture);
        });
        super.onStart();
    }

    //universal click handler
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.user_list_button: //if List Button is pressed sends the users id id and opens the users list activity
                startActivity(intent2);
                break;
            case R.id.back_profile_button: //if Back Button is pressed goes back to search users fragment
                finish();
                break;
        }
    }
}
