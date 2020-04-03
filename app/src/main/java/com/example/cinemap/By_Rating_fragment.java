package com.example.cinemap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class By_Rating_fragment extends Fragment
{
    RecyclerView ByRatedListDisplay;
    List<Movie> RatingsList;
    TextView PageTitle;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        //variable declaration
        View LayoutView = inflater.inflate(R.layout.fragment_rating, container, false);
        ByRatedListDisplay = LayoutView.findViewById(R.id.By_Rated_List);
        PageTitle=LayoutView.findViewById(R.id.Page_Title);
        RatingsList = new ArrayList<>();
        List<User_List_Movie> Rows = new ArrayList<>();
        //goes through the firebase database and gets every document that has rating
        FirebaseFirestore Database = FirebaseFirestore.getInstance();
        Database.collectionGroup("Movies").whereGreaterThan("Rating","-").get().addOnSuccessListener(queryDocumentSnapshots ->
        {
            int views=1; //amount of appearances of a movie
            int sum=0; //the sum of its ratings
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) //goes through each document and adds it to the list
            {
                User_List_Movie movie=documentSnapshot.toObject(User_List_Movie.class);
                Rows.add(movie);
            }
            //goes through the movie list and removes all duplicate references
            for(int i=0;i<Rows.size();i++)
            {
                for(int j=i+1;j<Rows.size();j++)
                {
                    if (Rows.get(i).getTitle().equals(Rows.get(j).getTitle())) //when duplicate is found adds a view to the movies view count adds its rating to the movies rating sum and removes the reference
                    {
                        views++;
                        sum+=Integer.parseInt(Rows.get(j).getRating());
                        Rows.remove(j);
                    }
                }
                Rows.get(i).setRating(String.valueOf(sum/views)); //after summing the movies rating sets its rating average
            }
            Rows.sort(Comparator.comparing(User_List_Movie::getRating).reversed());//sorts the list by rating in descending order
            //goes trough the list and adds the 20 best rated movies to the list that is inserted to the recycler view(if there are less than 20 rated movies it will display only those movies)
            for(int i=0;i<Rows.size();i++)
            {
                if(i==19) //when the count reached 19(20 movies) stops the loop
                {
                    i=Rows.size();
                }
                Movie movie=new Movie(Rows.get(i).getTitle(),Rows.get(i).getPoster_Path());
                RatingsList.add(movie);
            }
            //sends the sorted list the recycler view adapter and passes it into the recycler view
            MovieLayoutAdapter ListAdapter=new MovieLayoutAdapter(RatingsList,getContext());
            ByRatedListDisplay.setAdapter(ListAdapter);
            //adds a layout manager to recycler view and makes its orientation horizontal
            LinearLayoutManager ListManger=new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
            ByRatedListDisplay.setLayoutManager(ListManger);
        });

        return LayoutView;
    }
}

