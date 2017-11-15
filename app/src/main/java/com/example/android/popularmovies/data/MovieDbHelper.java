package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.popularmovies.data.MovieContract.*;

public class MovieDbHelper extends SQLiteOpenHelper{
    // The database name
    private static final String DATABASE_NAME = "movie.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +
                FavoriteEntry._ID + " INTEGER PRIMARY KEY NOT NULL," +
                FavoriteEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL," +
                FavoriteEntry.COLUMN_NAME_SYNOPSIS + " TEXT NOT NULL," +
                FavoriteEntry.COLUMN_NAME_RATING + " TEXT NOT NULL," +
                FavoriteEntry.COLUMN_NAME_RELEASE_DATE + " TEXT NOT NULL," +
                FavoriteEntry.COLUMN_NAME_POSTER_URL + " TEXT NOT NULL" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}