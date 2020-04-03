package com.example.cinemap;

public class Movie
{
    private String Title;
    private String PosterPath;

    public Movie(String title, String posterPath)
    {
        this.Title = title;
        this.PosterPath = posterPath;
    }

    public String getTitle()
    {
        return Title;
    }

    public void setTitle(String title)
    {
        Title = title;
    }

    public String getPosterPath()
    {
        return PosterPath;
    }

    public void setPosterPath(String posterPath)
    {
        PosterPath = posterPath;
    }

}
