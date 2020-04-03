package com.example.cinemap;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Search_fragment extends Fragment implements View.OnClickListener
{
    public static final String TAG="Search_fragment";
    TextView Title;
    EditText TitleInput;
    TextView Release_Date;
    TextView Rating;
    TextView Views;
    TextView Plot;
    TextView Actors;
    TextView Production;
    TextView Awards;
    TextView Genres;
    Button Add;
    Button Search;
    ImageView Poster;
    String PosterUrl;
    int views;
    int RatingSum;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        View LayoutView=inflater.inflate(R.layout.fragment_search,container,false);

        //layout variable declaration(visibility set to gone until a search result is found)
        Title=LayoutView.findViewById(R.id.search_movie_title);
        Title.setVisibility(View.GONE);
        TitleInput=LayoutView.findViewById(R.id.search_movie_input);
        Release_Date=LayoutView.findViewById(R.id.search_movie_Release_Date);
        Release_Date.setVisibility(View.GONE);
        Rating=LayoutView.findViewById(R.id.search_movie_rating);
        Rating.setVisibility(View.GONE);
        Views=LayoutView.findViewById(R.id.search_movie_views);
        Views.setVisibility(View.GONE);
        Plot=LayoutView.findViewById(R.id.search_movie_plot);
        Plot.setVisibility(View.GONE);
        Actors=LayoutView.findViewById(R.id.search_movie_actors);
        Actors.setVisibility(View.GONE);
        Production=LayoutView.findViewById(R.id.search_movie_Production);
        Production.setVisibility(View.GONE);
        Awards=LayoutView.findViewById(R.id.search_movie_awards);
        Awards.setVisibility(View.GONE);
        Genres=LayoutView.findViewById(R.id.search_movie_genres);
        Genres.setVisibility(View.GONE);
        Add=LayoutView.findViewById(R.id.search_add_movie);
        Add.setVisibility(View.GONE);
        Add.setClickable(false);
        Search=LayoutView.findViewById(R.id.search_movie);
        Poster=LayoutView.findViewById(R.id.search_movie_poster);
        Poster.setVisibility(View.GONE);
        Search.setOnClickListener(this);
        Add.setOnClickListener(this);

        return LayoutView;
    }

    //universal click handler
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.search_movie: //if search button is pressed starts search method
                search();
                break;
            case R.id.search_add_movie: //if add button is pressed starts add method
                add();
                break;
        }
    }

    //adds the searched movie to the users list
    private void add()
    {
        //gets the user id and adds the movie to the users list(in FireStore database)
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore Database=FirebaseFirestore.getInstance();
        Map<String,Object> MyListRow=new HashMap<>();
        MyListRow.put("Title", Title.getText().toString().substring(6));
        MyListRow.put("Rating","-");
        MyListRow.put("Watch_Status","Plan To Watch");
        MyListRow.put("Poster_Path",PosterUrl);
        Database.collection("Lists").document(user.getUid()+"List").collection("Movies").document(Title.getText().toString().substring(6)).set(MyListRow).addOnSuccessListener(aVoid ->
        {
            Toast.makeText(getContext(),"Movie Added to your List",Toast.LENGTH_SHORT).show();
        }) .addOnFailureListener(e ->
        {
            Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
            Log.d(TAG, e.toString());
        });
        //after adding to the list makes sure the user cant add it more than once
        Add.setText("Added");
        Add.setClickable(false);
    }

    // gets the searched title and displays its details
    private void search()
    {
        //gets the title(capitalization dose not matter)
        String Movie_Title=TitleInput.getText().toString().trim().toLowerCase();
        //adds it to the api call url and makes the call
        String url="http://www.omdbapi.com/?apikey=98a0cc62&t="+Movie_Title.replace(" ","+")+"&plot=full";
        RequestQueue queue= Volley.newRequestQueue(getContext());
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url, null, response ->
        {
            try
            {
                //gets the response as long as there isn't a delay of more than 20 seconds
                //if the response is false
                if(response.getString("Response").equals("False"))
                {
                    Toast.makeText(getContext(),"Movie Not Found",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //if the response is true checks if the movie year is before 2017
                    String year=response.getString("Year");
                    Date comp1=new SimpleDateFormat("yyyy-MM-dd").parse(year+"-01-01");
                    Date comp2=new SimpleDateFormat("yyyy-MM-dd").parse("2017-01-01");
                    if(comp1.before(comp2)) //if before 2017(not in the database)
                    {
                        Toast.makeText(getContext(),"Movie Not In The Database(Before 2017)",Toast.LENGTH_SHORT).show();
                    }
                    else //if its after 2017 than it resets all the vies back to visible and adds the movies data
                    {
                        Title.setVisibility(View.VISIBLE);
                        Title.setText("Title:"+response.getString("Title"));
                        Release_Date.setVisibility(View.VISIBLE);
                        Release_Date.setText("Release Date:"+response.getString("Released"));
                        Plot.setVisibility(View.VISIBLE);
                        Plot.setText("Plot:"+response.getString("Plot"));
                        Actors.setVisibility(View.VISIBLE);
                        Actors.setText("Actors:"+response.getString("Actors"));
                        Genres.setVisibility(View.VISIBLE);
                        Genres.setText("Genres:"+response.getString("Genre"));
                        Production.setVisibility(View.VISIBLE);
                        Production.setText("Production:"+response.getString("Director"));
                        Awards.setVisibility(View.VISIBLE);
                        Awards.setText("Awards:"+response.getString("Awards"));
                        Poster.setVisibility(View.VISIBLE);
                        PosterUrl=response.getString("Poster");
                        Glide.with(getContext()).load(PosterUrl).into(Poster);
                        Add.setVisibility(View.VISIBLE);
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
                        //goes through the FireStore database and checks the amount of appearances and ratings of the movie
                        views=0;
                        RatingSum=0;
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
                        }).addOnFailureListener(e ->
                        {
                            Log.d(TAG, e.toString());
                        });
                    }
                }
            }
            catch (JSONException | ParseException e)
            {
                e.printStackTrace();
            }
        }
        , error ->
        {
            error.printStackTrace();
        });
        queue.add(request);
    }
}
