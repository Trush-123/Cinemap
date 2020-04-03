package com.example.cinemap;

public class User_List_Movie
{
    String Poster_Path;
    String Title;
    String Watch_Status;
    String Rating;

    //public no  argument constructor for firebase
    public User_List_Movie()
    {
    }

    public User_List_Movie(String poster_Path, String title, String watch_Status, String rating)
    {
        this.Poster_Path = poster_Path;
        this.Title = title;
        this.Watch_Status = watch_Status;
        this.Rating = rating;
    }

    public String getPoster_Path()
    {
        return Poster_Path;
    }

    public void setPoster_Path(String poster_Path)
    {
        Poster_Path = poster_Path;
    }

    public String getTitle()
    {
        return Title;
    }

    public void setTitle(String title)
    {
        Title = title;
    }

    public String getWatch_Status()
    {
        return Watch_Status;
    }

    public void setWatch_Status(String watch_Status)
    {
        Watch_Status = watch_Status;
    }

    public String getRating()
    {
        return Rating;
    }

    public void setRating(String rating)
    {
        Rating = rating;
    }
}
