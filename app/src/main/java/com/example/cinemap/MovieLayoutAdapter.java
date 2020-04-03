package com.example.cinemap;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MovieLayoutAdapter extends RecyclerView.Adapter<MovieLayoutAdapter.ViewHolder>
{
    private List<Movie> MovieList=new ArrayList<>();
    private Context context;
    

    public MovieLayoutAdapter(List<Movie> movieList, Context context)
    {
        this.MovieList = movieList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_layout,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Glide.with(context).load(MovieList.get(position).getPosterPath()).into(holder.MoviePoster);
        holder.MovieTitle.setText(MovieList.get(position).getTitle());
        holder.ListLayout.setOnClickListener(v ->
        {
            Intent intent=new Intent(context,Item_Details.class);
            intent.putExtra("Title",MovieList.get(position).getTitle());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount()
    {
        return MovieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView MovieTitle;
        ImageView MoviePoster;
        RelativeLayout ListLayout;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            MovieTitle=itemView.findViewById(R.id.movie_title);
            MoviePoster=itemView.findViewById(R.id.movie_image);
            ListLayout=itemView.findViewById(R.id.list_layout);
        }
    }
}