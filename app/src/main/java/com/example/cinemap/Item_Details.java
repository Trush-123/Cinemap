package com.example.cinemap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class Item_Details extends AppCompatActivity
{
    TextView Title;
    TextView Release_Date;
    TextView Rating;
    TextView Views;
    TextView Plot;
    TextView Actors;
    TextView Production;
    TextView Awards;
    TextView Genres;
    Button Add;
    Button Back;
    ImageView Poster;
    String PosterUrl;
    Intent intent;
    int views;
    int RatingSum;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item__details);

        //variable declaration
        Title=findViewById(R.id.Details_movie_title);
        Release_Date=findViewById(R.id.Details_movie_Release_Date);
        Rating=findViewById(R.id.Details_movie_rating);
        Views=findViewById(R.id.Details_movie_views);
        Plot=findViewById(R.id.Details_movie_plot);
        Actors=findViewById(R.id.Details_movie_actors);
        Production=findViewById(R.id.Details_movie_Production);
        Awards=findViewById(R.id.Details_movie_awards);
        Genres=findViewById(R.id.Details_movie_genres);
        Add=findViewById(R.id.Details_add_movie);
        Poster=findViewById(R.id.Details_movie_poster);
        Back=findViewById(R.id.Details_Back_Button);
        //gets the movies title from the previous screen
        intent=getIntent();

        GetMovie();

        //adds the searched movie to the users list
        Add.setOnClickListener(v ->
        {
            Add();
        });
        //goes back to previous screen
        Back.setOnClickListener(v ->
        {
            finish();
        });

    }

    //gets the movies title and displays its details
    private void GetMovie()
    {
        String Movie_Title=intent.getStringExtra("Title");
        //adds it to the api call url and makes the call
        String url="http://www.omdbapi.com/?apikey=98a0cc62&t="+Movie_Title.replace(" ","+")+"&plot=full";
        RequestQueue queue= Volley.newRequestQueue(this);
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url, null, response ->
        {
            try
            {
                //gets the response as long as there isn't a delay of more than 20 seconds
                //if the response is false
                if(response.getString("Response").equals("False"))
                {
                    Toast.makeText(this,"Movie Not Found",Toast.LENGTH_SHORT).show();
                }
                else //if the response is true adds the details to the ui
                {
                    Title.setText("Title:"+response.getString("Title"));
                    Release_Date.setText("Release Date:"+response.getString("Released"));
                    Plot.setText("Plot:"+response.getString("Plot"));
                    Actors.setText("Actors:"+response.getString("Actors"));
                    Genres.setText("Genres:"+response.getString("Genre"));
                    Production.setText("Production:"+response.getString("Director"));
                    Awards.setText("Awards:"+response.getString("Awards"));
                    PosterUrl=response.getString("Poster");
                    Glide.with(this).load(PosterUrl).into(Poster);
                    //checks if the movie is in the users list
                    FirebaseFirestore Database=FirebaseFirestore.getInstance();
                    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                    DocumentReference doc=Database.collection("Lists").document(user.getUid()+"List").collection("Movies").document(Title.getText().toString().substring(6));
                    doc.get().addOnSuccessListener(documentSnapshot ->
                    {
                        if (documentSnapshot.exists()) ///if it is makes sure the cant add the movie more than once
                        {
                            Add.setText("Added");
                            Add.setClickable(false);
                        }
                        else
                        {
                            Add.setText("Add");
                        }
                    });
                    views=0;
                    RatingSum=0;
                    //goes through the FireStore database and checks the amount of appearances and ratings of the movie
                    Database.collectionGroup("Movies").whereEqualTo("Title",Title.getText().toString().substring(6)).get().addOnSuccessListener(queryDocumentSnapshots ->
                    {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                        {
                            User_List_Movie Movie=documentSnapshot.toObject(User_List_Movie.class);
                            if (Movie.getRating().equals("-")) //if it hasn't been rated yet only increases view count
                            {
                                views++;
                            }
                            else //if has been rated also adds to rating sum
                            {
                                views++;
                                RatingSum += Integer.parseInt(Movie.getRating());
                            }
                        }
                        if(views==0)
                        {
                            Views.setText("Views:"+views);
                            Rating.setText("Rating:"+RatingSum);
                            Views.setVisibility(View.VISIBLE);
                            Rating.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            Rating.setText("Rating:"+RatingSum/views);
                            Views.setText("Views:"+views);
                            Views.setVisibility(View.VISIBLE);
                            Rating.setVisibility(View.VISIBLE);
                        }
                        //sets the rating average and view count to the TextViews
                    });
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        , Throwable::printStackTrace);
        queue.add(request);
    }

    //adds the movie to the users list
    public void Add()
    {
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore Database=FirebaseFirestore.getInstance();
        Map<String,Object> MyListRow=new HashMap<>();
        MyListRow.put("Title", Title.getText().toString().substring(6));
        MyListRow.put("Rating","-");
        MyListRow.put("Watch_Status","Plan To Watch");
        MyListRow.put("Poster_Path",PosterUrl);
        Database.collection("Lists").document(user.getUid()+"List").collection("Movies").document(Title.getText().toString().substring(6)).set(MyListRow).addOnSuccessListener(aVoid ->
        {
            Toast.makeText(this,"Movie Added to your List",Toast.LENGTH_SHORT).show();
        }) .addOnFailureListener(e ->
        {
            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
        });
        //after adding to the list makes sure the user cant add it more than once
        Add.setText("Added");
        Add.setClickable(false);
    }
}
