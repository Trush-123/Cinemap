package com.example.cinemap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class User_List_fragment extends Fragment
{

    RecyclerView UserListDisplay;
    List<User_List_Movie> UserList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        //variable declaration
        View LayoutView=inflater.inflate(R.layout.user_list_fragment,container,false);
        UserListDisplay=LayoutView.findViewById(R.id.user_list);
        UserList=new ArrayList<>();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore Database=FirebaseFirestore.getInstance();
        String uid=user.getUid();
        //gets the users list form the firebase database add every movie to a list and sends it to the recycler view
        Query query = Database.collection("Lists").document(uid+"List").collection("Movies");
        query.get().addOnSuccessListener(queryDocumentSnapshots ->
        {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
            {
                User_List_Movie Movie=documentSnapshot.toObject(User_List_Movie.class);
                UserList.add(Movie);
            }
            UserListAdapter ListAdapter=new UserListAdapter(UserList,getContext());
            UserListDisplay.setAdapter(ListAdapter);
            UserListDisplay.setLayoutManager(new GridLayoutManager(getContext(),2));
        });
        return LayoutView;
    }
}
