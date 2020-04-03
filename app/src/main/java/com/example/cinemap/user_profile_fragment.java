package com.example.cinemap;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class user_profile_fragment extends Fragment implements View.OnClickListener
{

    ImageView ProfilePicture;
    TextView UserName;
    TextView Email;
    TextView Profile_Description;
    Button UserList;
    Button EditProfile;
    Intent intent;
    DocumentReference UserDocRef;
    ListenerRegistration UserDoc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View LayoutView=inflater.inflate(R.layout.fragment_user,container,false);
        //variable declaration
        ProfilePicture=LayoutView.findViewById(R.id.profile_image);
        UserName=LayoutView.findViewById(R.id.user_name);
        Email=LayoutView.findViewById(R.id.user_mail);
        Profile_Description=LayoutView.findViewById(R.id.user_desc);
        UserList=LayoutView.findViewById(R.id.user_list_button);
        EditProfile=LayoutView.findViewById(R.id.edit_profile_button);
        //gets the users data form the firebase database
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore Database=FirebaseFirestore.getInstance();
        UserDocRef=Database.collection("Users").document(user.getUid());//gets the users data from the FireStore Database and displays them
        EditProfile.setOnClickListener(this);
        UserList.setOnClickListener(this);
        //intent to move to the users list activity
        intent=new Intent(getContext(),user_list_activity.class);
        intent.putExtra("uid",user.getUid());

        return LayoutView;
    }

    @Override
    public void onStart() //snapshot listener for automatic updating
    {
       UserDoc= UserDocRef.addSnapshotListener((documentSnapshot, e) ->
        {
            Email.setText("Email: "+documentSnapshot.getString("Email"));
            UserName.setText("Username: "+documentSnapshot.getString("UserName"));
            Profile_Description.setText("Description: "+documentSnapshot.getString("User_Description"));
            Glide.with(this).load(documentSnapshot.getString("Profile_Image")).fitCenter().into(ProfilePicture);
        });
        super.onStart();
    }

    @Override
    public void onStop()
    {
        UserDoc.remove();
        super.onStop();
    }

    //universal click handler
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.user_list_button: //if List Button is pressed sends the users id id and opens the users list activity
                startActivity(intent);
                break;
            case R.id.edit_profile_button: //if Edit Profile Button is clicked opens the users edit profile activity
                startActivity(new Intent(getContext(),Edit_Profile_Activity.class));
                break;
        }
    }
}
