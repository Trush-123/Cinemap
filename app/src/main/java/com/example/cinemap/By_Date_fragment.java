package com.example.cinemap;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

public class By_Date_fragment extends Fragment
{
    Spinner YearFilter;
    Spinner MonthFilter;
    RecyclerView ByDateListDisplay;
    List<Movie> ByDateList;
    MovieLayoutAdapter ListAdapter;
    Button Search;
    TextView PageTitle;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        View LayoutView=inflater.inflate(R.layout.fragment_by_date,container,false);
        //variable declaration
        PageTitle=LayoutView.findViewById(R.id.Page_Title);
        YearFilter=LayoutView.findViewById(R.id.by_date_year);
        MonthFilter=LayoutView.findViewById(R.id.by_date_month);
        ByDateListDisplay=LayoutView.findViewById(R.id.by_date_list);
        Search=LayoutView.findViewById(R.id.By_Date_Search);
        ByDateList=new ArrayList<>();
        //spinner adapter
        ArrayAdapter<CharSequence> YearAdapter=ArrayAdapter.createFromResource(getContext(),R.array.Year_Filter,android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> MonthAdapter=ArrayAdapter.createFromResource(getContext(),R.array.Month_Chosen_Filter,android.R.layout.simple_spinner_item);
        YearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        YearFilter.setAdapter(YearAdapter);
        MonthFilter.setAdapter(MonthAdapter);
        YearFilter.setSelection(0);
        MonthFilter.setSelection(0);

        //gets the entered year and month and starts the list inserting method
        Search.setOnClickListener(v ->
        {
            String year=YearFilter.getSelectedItem().toString();
            String month=MonthFilter.getSelectedItem().toString();
            insert(year,month);
        });

        //sends the sorted list the recycler view adapter and passes it into the recycler view
        ListAdapter=new MovieLayoutAdapter(ByDateList,getContext());
        ByDateListDisplay.setAdapter(ListAdapter);
        //adds a layout manager to recycler view and makes its orientation horizontal
        LinearLayoutManager ListManger=new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
        ByDateListDisplay.setLayoutManager(ListManger);

        return LayoutView;
    }

    //goes thorough the database and inserts the movies within the selected date range into the list
        private void insert(String year,String month)
        {
            ByDateList.clear();//clears the list of previous sort
            //database variables declaration
            Movie_DB_Helper hlp=new Movie_DB_Helper(getContext());
            SQLiteDatabase Database=hlp.getWritableDatabase();
            Cursor cursor=Database.query(Movie_DB_Helper.Movie_Table,null,null,null,null,null,Movie_DB_Helper.Movie_Release_Date+" DESC",null);
            cursor.moveToFirst();
            try
            {
                //gets the amount of days in the selected month
                String Days=GetDays(year,MonthNum(month));
                //sets up the date range selected by the user
                Date EnteredDateStart=new SimpleDateFormat("yyyy-MM-dd").parse(year+"-"+MonthNum(month)+"-"+Days);
                Date EnteredDateEnd=new SimpleDateFormat("yyyy-MM-dd").parse(year+"-"+MonthNum(month)+"-01");
                Date comp;
                while (!cursor.isAfterLast())//starts going through the database
                {
                    //gets movie date
                    comp = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(cursor.getColumnIndex(Movie_DB_Helper.Movie_Release_Date)));
                    if(comp.before(EnteredDateStart)&&comp.after(EnteredDateEnd))//if the movie is whiten the selected range inserts it into the list
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
                        ByDateList.add(movie);
                    }
                    if (comp.before(EnteredDateEnd))//if it paces hte final date it sends the cursor to the last position nad ends the loop(to reduce unnecessary reads)
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
            ListAdapter.notifyDataSetChanged();
        }



    //gets the month and returns the amount of days in them (also checks for leap years)
    private String GetDays(String year, String month)
    {
        String Days="";
        switch (month)
        {
            case "01":
            case "03":
            case "05":
            case "07":
            case "08":
            case "10":
            case "12":
                Days="31";
                break;
            case "02":
                if(Integer.parseInt(year)%4==0&&Integer.parseInt(month)%100!=0)//year leap check
                {
                    Days="29";
                }
                else
                {
                    Days="28";
                }
                break;
            case "04":
            case "06":
            case "09":
            case "11":
                Days="30";
                break;
        }
        return Days;
    }

    //gets the selected month name and converts it into its matching number
    public String MonthNum(String Month)
    {
        String Rmonth="";
        switch (Month)
        {
            case "January":
                Rmonth="01";
                break;
            case "February":
                Rmonth="02";
                break;
            case "March":
                Rmonth="03";
                break;
            case "April":
                Rmonth="04";
                break;
            case "May":
                Rmonth="05";
                break;
            case "June":
                Rmonth="06";
                break;
            case "July":
                Rmonth="07";
                break;
            case "August":
                Rmonth="08";
                break;
            case "September":
                Rmonth="09";
                break;
            case "October":
                Rmonth="10";
                break;
            case "November":
                Rmonth="11";
                break;
            case "December":
            {
                Rmonth="12";
                break;
            }
        }
        return Rmonth;
    }
}
