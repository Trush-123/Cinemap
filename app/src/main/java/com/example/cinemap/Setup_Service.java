package com.example.cinemap;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import androidx.annotation.Nullable;


public class Setup_Service extends IntentService
{
    public static final int UPDATE_PROGRESS =8334 ;
    ResultReceiver receiver;

    public Setup_Service()
    {
        super("Setup_Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        receiver = intent.getParcelableExtra("receiver");
        // makes request to get total number of pages for a request and sends it to the main request which gets from each page until total pages reached
        RequestFuture<JSONObject> future=RequestFuture.newFuture();
        JsonObjectRequest request;
        RequestQueue queue= Volley.newRequestQueue(this);
        String Url = "https:api.themoviedb.org/3/discover/movie?api_key=20802c7f964aa2f2bd48535e9a0d6e8a&language=en-US&sort_by=popularity.desc&certification_country=US&include_adult=false&include_video=false&page=1&primary_release_date.gte=2017-01-01&primary_release_date.lte=2021-12-31&with_release_type=4&with_genres=28%7C12%7C16%7C35%7C80%7C18%7C10751%7C14%7C36%7C27%7C10402%7C9648%7C10749%7C878%7C10770%7C53%7C10752%7C37&without_genres=99%2C1077&with_original_language=en";
        request=new JsonObjectRequest(Request.Method.GET,Url,new JSONObject(),future,future);
        queue.add(request);
        try
        {
            JSONObject response=future.get(15, TimeUnit.SECONDS);
            int TotalPages=response.getInt("total_pages");
            MainRequest(TotalPages);
            Bundle resultData = new Bundle();
            resultData.putInt("progress" ,100);
            receiver.send(UPDATE_PROGRESS, resultData);
        }
        catch (InterruptedException | ExecutionException | TimeoutException | JSONException e)
        {
            Toast.makeText(Setup_Service.this, "Error Restart The App To Re preform First Time Setup ", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void MainRequest(int TotalPages)
    {
        //gets total amount of pages a makes a request for each page gets its data and sends to the database insert method
        for (int i=1;i<=TotalPages;i++)
        {
            RequestQueue Queue = Volley.newRequestQueue(this);
            RequestFuture<JSONObject> future=RequestFuture.newFuture();
            JsonObjectRequest request;
            String page= String.valueOf(i);
            String Url="https:api.themoviedb.org/3/discover/movie?api_key=20802c7f964aa2f2bd48535e9a0d6e8a&language=en-US&sort_by=popularity.desc&certification_country=US&include_adult=false&include_video=false&page="+page+"&primary_release_date.gte=2017-01-01&primary_release_date.lte=2021-12-31&with_release_type=4&with_genres=28%7C12%7C16%7C35%7C80%7C18%7C10751%7C14%7C36%7C27%7C10402%7C9648%7C10749%7C878%7C10770%7C53%7C10752%7C37&without_genres=99%2C1077&with_original_language=en";
            request=new JsonObjectRequest(Request.Method.GET,Url,new JSONObject(),future,future);
            Queue.add(request);
            try
            {
                JSONObject response=future.get(15, TimeUnit.SECONDS);
                JSONArray jsonArray = response.getJSONArray("results");
                for (int j = 0; j < jsonArray.length(); j++)
                {
                    JSONObject results = jsonArray.getJSONObject(j);
                    String Title = results.getString("original_title");
                    String regex="^[a-zA-Z0-9_. :-]*$";
                    if(Title.matches(regex))
                    {
                        String ReleaseDate = results.getString("release_date");
                        String PosterPath = results.getString("poster_path");
                        int ID=results.getInt("id");
                        WriteInDb(ID,Title, ReleaseDate, PosterPath);
                    }
                }
                Bundle resultData = new Bundle();
                resultData.putInt("progress" , i * 100 /TotalPages );
                receiver.send(UPDATE_PROGRESS, resultData);
            }
            catch (InterruptedException | ExecutionException | TimeoutException | JSONException e)
            {
                Toast.makeText(Setup_Service.this, "Error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }


    private void WriteInDb(int ID, String Title, String ReleaseDate, String PosterPath)
    {
        //gets data from main request and inserts into the database
        SQLiteDatabase Database;
        Movie_DB_Helper Database_Helper;
        Database_Helper=new Movie_DB_Helper(this);
        ContentValues ItemValues= new ContentValues();
        ItemValues.put(Movie_DB_Helper.Movie_ID,ID);
        ItemValues.put(Movie_DB_Helper.Movie_Title,Title);
        ItemValues.put(Movie_DB_Helper.Movie_Release_Date,ReleaseDate);
        ItemValues.put(Movie_DB_Helper.Movie_Poster_Path,PosterPath);
        Database=Database_Helper.getWritableDatabase();
        Database.insert(Movie_DB_Helper.Movie_Table,null,ItemValues);
        Database.close();
    }
}
