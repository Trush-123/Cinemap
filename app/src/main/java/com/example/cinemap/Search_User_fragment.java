package com.example.cinemap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Search_User_fragment extends Fragment
{

    SearchView Search_Users;
    RecyclerView UsersListDisplay;
    List<User> UsersList;
    UsersListAdapter ListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        //variable declaration
        View LayoutView=inflater.inflate(R.layout.fragment_search_user,container,false);
        UsersList=new ArrayList<>();
        UsersListDisplay=LayoutView.findViewById(R.id.users_list);
        Search_Users=LayoutView.findViewById(R.id.search_users);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore Database=FirebaseFirestore.getInstance();
        String uid=user.getUid();
        //gets every users details from the firebase database and sends it to the recycler view
        Query query = Database.collection("Users");
        query.get().addOnSuccessListener(queryDocumentSnapshots ->
        {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
            {
                if(!documentSnapshot.getId().equals(uid))
                {
                    String UserName=documentSnapshot.getString("UserName");
                    String Uid=documentSnapshot.getId();
                    String ProfileImage=documentSnapshot.getString("Profile_Image");
                    User User=new User(UserName,Uid,ProfileImage);
                    UsersList.add(User);
                }
            }
            ListAdapter=new UsersListAdapter(UsersList,getContext());
            UsersListDisplay.setAdapter(ListAdapter);
            LinearLayoutManager ListManger=new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
            UsersListDisplay.setLayoutManager(ListManger);
        });
        //sends search text to the list adapter
        Search_Users.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                ListAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return LayoutView;
    }

}
