package com.example.cinemap;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder>
{
    List<User_List_Movie> List=new ArrayList<>();
    Context context;
    String status;

    public UserListAdapter(java.util.List<User_List_Movie> list, Context context)
    {
        this.List = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View LayoutView= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_adapter,parent,false);
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
        holder.Status.setOnClickListener(v ->
        {
            if (status.equals("Watched"))
            {
                holder.Status.setImageResource(R.drawable.ic_remove_red_eye_black_24dp_nw);
                status="Not Watched";
            }
            else
            {
                holder.Status.setImageResource(R.drawable.ic_remove_red_eye_black_24dp);
                status="Watched";
            }
        });
        holder.Update.setOnClickListener(v ->
        {
            if(status.equals("Not Watched")&&!holder.Rating.getText().toString().equals("-"))
            {
                Toast.makeText(context,"You Can Not Rate a Movie You Haven't Watched yet",Toast.LENGTH_SHORT).show();
            }
            else
            {
                if (Integer.parseInt(holder.Rating.getText().toString())<0&&!holder.Rating.getText().toString().equals("-")||Integer.parseInt(holder.Rating.getText().toString())>10&&!holder.Rating.getText().toString().equals("-"))
                {
                    Toast.makeText(context,"Invalid Rating",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    FirebaseFirestore Database=FirebaseFirestore.getInstance();
                    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                    DocumentReference documentReference=Database.collection("Lists").document(user.getUid()+"List").collection("Movies").document(List.get(position).getTitle());
                    Map<String,Object> update=new HashMap<>();
                    update.put("Watch_Status", status);
                    update.put("Rating",holder.Rating.getText().toString());
                    documentReference.update(update);
                    Toast.makeText(context,"Details Updated",Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        ImageButton Status;
        EditText Rating;
        TextView OutOf10;
        ImageButton Update;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            UserListLayout=itemView.findViewById(R.id.user_list_layout);
            Poster=itemView.findViewById(R.id.User_List_Movie_Image);
            Title=itemView.findViewById(R.id.User_List_Movie_Title);
            Status=itemView.findViewById(R.id.User_List_Movie_Status);
            Rating=itemView.findViewById(R.id.User_List_Movie_Rating);
            OutOf10=itemView.findViewById(R.id.User_List_OutOfTen);
            Update=itemView.findViewById(R.id.User_List_Movie_Update);
        }
    }
}
