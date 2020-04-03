package com.example.cinemap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private DrawerLayout drawerLayout;
    FirebaseAuth Auth;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        Auth=FirebaseAuth.getInstance();

        Toolbar toolbar=findViewById(R.id.ToolBar);
        setSupportActionBar(toolbar);
        drawerLayout=findViewById(R.id.Layout);
        NavigationView Nav=findViewById(R.id.Nav_View);
        Nav.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_Container,new Upcoming_fragment()).commit();

        FirebaseFirestore Database = FirebaseFirestore.getInstance();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference UserRef = Database.collection("Users").document(user.getUid());
        UserRef.get().addOnSuccessListener(documentSnapshot ->
        {
            //if its the users first login(ie his user document dose not exist yet) adds his user info to the firebase database
            if(!documentSnapshot.exists())
            {
                Map<String,Object> User=new HashMap<>();
                User.put("UserName", user.getEmail());
                User.put("Email",user.getEmail());
                User.put("User_Description","");
                User.put("Profile_Image","https://recoverycafe.org/wp-content/uploads/2019/06/generic-user.png");
                Database.collection("Users").document(user.getUid()).set(User);
            }
            //displays user info in the navigation drawer header
            View HeaderView=Nav.getHeaderView(0);
            ImageView HeaderImage=HeaderView.findViewById(R.id.nav_profile_picture);
            TextView HeaderUsername=HeaderView.findViewById(R.id.nav_username);
            TextView HeaderEmail=HeaderView.findViewById(R.id.nav_email);
            Glide.with(this).load(documentSnapshot.getString("Profile_Image")).into(HeaderImage);
            HeaderEmail.setText(documentSnapshot.getString("Email"));
            HeaderUsername.setText(documentSnapshot.getString("UserName"));
        });


    }

    @Override
    public void onBackPressed()
    {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.Nav_Upcoming:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_Container,new Upcoming_fragment()).commit();
                break;
            case R.id.Nav_Search:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_Container,new Search_fragment()).commit();
                break;
            case R.id.Nav_By_Rating:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_Container,new By_Rating_fragment()).commit();
                break;
            case R.id.Nav_By_Date:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_Container,new By_Date_fragment()).commit();
                break;
            case R.id.Nav_User_List:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_Container,new User_List_fragment()).commit();
                break;
            case R.id.Nav_Profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_Container,new user_profile_fragment()).commit();
                break;
            case R.id.Nav_Search_Users:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_Container,new Search_User_fragment()).commit();
                break;
            case R.id.Nav_Log_Out:
                Auth.signOut();
                finish();
                break;
        }
        return true;
    }
}
