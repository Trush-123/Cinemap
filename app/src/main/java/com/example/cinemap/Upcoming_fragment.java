package com.example.cinemap;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Upcoming_fragment extends Fragment
{

    List<Movie> UpcomingList;
    RecyclerView UpcomingListDisplay;
    TextView Page_Title;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View LayoutView=inflater.inflate(R.layout.upcoming_fragment,container,false);
        //variable declaration
        UpcomingList=new ArrayList<>();
        UpcomingListDisplay=LayoutView.findViewById(R.id.upcoming_list);
        Page_Title=LayoutView.findViewById(R.id.Page_Title);

        //starts the  list insertion method
        GetUpcoming();

        //sends the sorted list the recycler view adapter and passes it into the recycler view
        MovieLayoutAdapter ListAdapter=new MovieLayoutAdapter(UpcomingList,getContext());
        UpcomingListDisplay.setAdapter(ListAdapter);
        //adds a layout manager to recycler view and makes its orientation horizontal
        LinearLayoutManager ListManger=new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
        UpcomingListDisplay.setLayoutManager(ListManger);

        return LayoutView;
    }


    //goes through the database and gets very movie that is within one month of the current month
    private void GetUpcoming()
    {
        //gets the date one month before and  after current date
        Calendar CurrentDateBefore = Calendar.getInstance();
        CurrentDateBefore.add(Calendar.MONTH,-1);
        Calendar CurrentDateAfter= Calendar.getInstance();
        CurrentDateAfter.add(Calendar.MONTH,1);
        //database variables declaration
        Movie_DB_Helper hlp=new Movie_DB_Helper(getContext());
        SQLiteDatabase Database=hlp.getWritableDatabase();
        Cursor cursor=Database.query(Movie_DB_Helper.Movie_Table,null,null,null,null,null,Movie_DB_Helper.Movie_Release_Date+" DESC",null);
        cursor.moveToFirst();
        try
        {
            Date comp;
            //starts going through the database
            while (!cursor.isAfterLast())
            {
                //gets movie date
                comp=new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(cursor.getColumnIndex(Movie_DB_Helper.Movie_Release_Date)));
                //if the movies date is within the 1 month range gets its title and image and it to the list
                if (comp.before(CurrentDateAfter.getTime())&&comp.after(CurrentDateBefore.getTime()))
                {
                    String Title = cursor.getString(cursor.getColumnIndex(Movie_DB_Helper.Movie_Title));
                    String Poster_Path;
                    //some posters aren't available so if the poster path received from the api call is null the list will receive a place holder image
                    Poster_Path = "https://image.tmdb.org/t/p/w500" + cursor.getString(cursor.getColumnIndex(Movie_DB_Helper.Movie_Poster_Path));
                    if (Poster_Path.equals("https://image.tmdb.org/t/p/w500null"))
                    {
                        Poster_Path="https://image.shutterstock.com/image-vector/no-image-available-icon-vector-260nw-1323742826.jpg";
                    }
                    Movie movie = new Movie(Title, Poster_Path);
                    UpcomingList.add(movie);
                }
                if (comp.before(CurrentDateBefore.getTime())) //if it passes the final date it sends the cursor to the last position and ends the loop(to reduce unnecessary reads)
                {
                    cursor.moveToLast();
                }
                cursor.moveToNext();
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        //database closing
        cursor.close();
        Database.close();
    }
}
