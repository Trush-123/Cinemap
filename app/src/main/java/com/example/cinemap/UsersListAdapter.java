package com.example.cinemap;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ViewHolder> implements Filterable
{

    List<User> Users=new ArrayList<>();
    List<User> SearchUsers;
    Context context;

    public UsersListAdapter(List<User> users,  Context context)
    {
        Users = users;
        SearchUsers = new ArrayList<>(Users);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View LayoutView=LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list_adapter,parent,false);
        ViewHolder viewHolder=new ViewHolder(LayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Glide.with(context).load(Users.get(position).getProfileImage()).into(holder.Profile_Image);
        holder.UserName.setText(Users.get(position).getUsername());
        holder.UserId.setText(Users.get(position).getUid());
        holder.Layout.setOnClickListener(v ->
        {
            Intent intent=new Intent(context,User_Profile_Activity.class);
            intent.putExtra("uid", Users.get(position).getUid());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount()
    {
        return Users.size();
    }

    @Override
    public Filter getFilter()
    {
        return SearchFilter;
    }
    private Filter SearchFilter =new Filter()
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            List<User> FilterUsers=new ArrayList<>();
            if(constraint==null||constraint.length()==0)
            {
                FilterUsers.addAll(SearchUsers);
            }
            else
            {
                String filter=constraint.toString().toLowerCase().trim();
                for(User user: SearchUsers)
                {
                    if(user.getUsername().toLowerCase().contains(filter))
                    {
                        FilterUsers.add(user);
                    }
                }
            }
            FilterResults Results=new FilterResults();
            Results.values=FilterUsers;
            return Results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            Users.clear();
            Users.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView Profile_Image;
        TextView UserName;
        TextView UserId;
        RelativeLayout Layout;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            Profile_Image=itemView.findViewById(R.id.Users_List_Profile_Image);
            UserName=itemView.findViewById(R.id.Users_List_UserName);
            UserId=itemView.findViewById(R.id.Users_List_Uid);
            Layout=itemView.findViewById(R.id.Users_list_Layout);
        }
    }
}
