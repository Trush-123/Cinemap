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

public class UserListActivityAdapter extends RecyclerView.Adapter<UserListActivityAdapter.ViewHolder>
{
    List<User_List_Movie> List=new ArrayList<>();
    Context context;
    String status;


    public UserListActivityAdapter(java.util.List<User_List_Movie> list, Context context)
    {
        this.List = list;
        this.context = context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View LayoutView= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_activity_adapter,parent,false);
        ViewHolder holder=new ViewHolder(LayoutView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Glide.with(context).load(List.get(position).getPoster_Path()).into(holder.Poster);
        holder.Title.setText(List.get(position).getTitle());
        holder.Rating.setText(List.get(position).getRating());
        if(List.get(position).getWatch_Status().equals("Watched"))
        {
            holder.Status.setImageResource(R.drawable.ic_remove_red_eye_black_24dp);
            status="Watched";
        }
        else
        {
            holder.Status.setImageResource(R.drawable.ic_remove_red_eye_black_24dp_nw);
            status="Not Watched";
        }
        holder.UserListLayout.setOnClickListener(v ->
        {
            Intent intent=new Intent(context,Item_Details.class);
            intent.putExtra("Title",List.get(position).getTitle());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount()
    {
        return List.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        RelativeLayout UserListLayout;
        ImageView Poster;
        TextView Title;
        ImageView Status;
        TextView Rating;
        TextView OutOf10;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            UserListLayout=itemView.findViewById(R.id.user_list_layout);
            Poster=itemView.findViewById(R.id.User_List_Movie_Image);
            Title=itemView.findViewById(R.id.User_List_Movie_Title);
            Status=itemView.findViewById(R.id.User_List_Movie_Status);
            Rating=itemView.findViewById(R.id.User_List_Movie_Rating);
            OutOf10=itemView.findViewById(R.id.User_List_OutOfTen);
        }
    }
}
