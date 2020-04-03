package com.example.cinemap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class Movie_DB_Helper extends SQLiteOpenHelper
{

    //Database name
    public static final String Database_Name="Movie_Database";

    //Table names
    public static final String Movie_Table="Movie_Table";

    //Movie Table Column names
    public static final String Movie_Title="Title";
    public static final String Movie_Release_Date="Release_Date";
    public static final String Movie_Poster_Path="Poster_Path";
    public static final String Movie_ID="Movie_ID";

    public Movie_DB_Helper( Context context )
    {
        super(context,Database_Name,null ,1);
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE Movie_Table ( Movie_ID INTEGER PRIMARY KEY , Title TEXT, Release_Date TEXT, Poster_Path TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS "+Movie_Table);
        onCreate(db);
    }
}
