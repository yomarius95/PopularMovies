package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.example.android.popularmovies.data.MovieContract.*;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class MovieContentProvider extends ContentProvider{
    private static final int FAVORITES = 100;
    private static final int FAVORITES_ID = 101;

    // Creates a UriMatcher object.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITES, FAVORITES);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITES + "/#", FAVORITES_ID);
    }

    /** Tag for the log messages */
    public static final String LOG_TAG = MovieContentProvider.class.getSimpleName();

    private MovieDbHelper movieDbHelper;

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = movieDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                cursor = database.query(FavoriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case FAVORITES_ID:
                selection = FavoriteEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(FavoriteEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                return insertMovie(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertMovie(Uri uri, ContentValues contentValues){

        String title = contentValues.getAsString(FavoriteEntry.COLUMN_NAME_TITLE);
        if(title == null) {
            throw new IllegalArgumentException("Movie requires a title");
        }

        String synopsis = contentValues.getAsString(FavoriteEntry.COLUMN_NAME_SYNOPSIS);
        if(synopsis == null) {
            throw new IllegalArgumentException("Movie requires a description");
        }

        String rating = contentValues.getAsString(FavoriteEntry.COLUMN_NAME_RATING);
        if(rating == null) {
            throw new IllegalArgumentException("Movie requires a rating");
        }

        String releaseDate = contentValues.getAsString(FavoriteEntry.COLUMN_NAME_RELEASE_DATE);
        if(releaseDate == null) {
            throw new IllegalArgumentException("Movie requires a release date");
        }

        String posterUrl = contentValues.getAsString(FavoriteEntry.COLUMN_NAME_POSTER_URL);
        if(posterUrl == null) {
            throw new IllegalArgumentException("Movie requires a poster url");
        }

        // Get writable database
        SQLiteDatabase database = movieDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(FavoriteEntry.TABLE_NAME, null, contentValues);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if(id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Track the number of rows that were deleted
        int rowsDeleted;

        // Get writeable database
        SQLiteDatabase database = movieDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                // Delete all rows that match the selection and selection args
                // For  case FAVORITES:
                rowsDeleted = database.delete(FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITES_ID:
                // Delete a single row given by the ID in the URI
                selection = FavoriteEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the numbers of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                return updateMovie(uri, contentValues, selection, selectionArgs);
            case FAVORITES_ID:
                // For the FAVORITES_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = FavoriteEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateMovie(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateMovie(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){

        if(contentValues.containsKey(FavoriteEntry.COLUMN_NAME_TITLE)) {
            String title = contentValues.getAsString(FavoriteEntry.COLUMN_NAME_TITLE);
            if(title == null) {
                throw new IllegalArgumentException("Movie requires a title");
            }
        }

        if(contentValues.containsKey(FavoriteEntry.COLUMN_NAME_SYNOPSIS)) {
            String synopsis = contentValues.getAsString(FavoriteEntry.COLUMN_NAME_SYNOPSIS);
            if(synopsis == null) {
                throw new IllegalArgumentException("Movie requires a description");
            }
        }

        if(contentValues.containsKey(FavoriteEntry.COLUMN_NAME_RATING)) {
            String rating = contentValues.getAsString(FavoriteEntry.COLUMN_NAME_RATING);
            if(rating == null) {
                throw new IllegalArgumentException("Movie requires a rating");
            }
        }

        if(contentValues.containsKey(FavoriteEntry.COLUMN_NAME_RELEASE_DATE)) {
            String releaseDate = contentValues.getAsString(FavoriteEntry.COLUMN_NAME_RELEASE_DATE);
            if(releaseDate == null) {
                throw new IllegalArgumentException("Movie requires a release date");
            }
        }

        if(contentValues.containsKey(FavoriteEntry.COLUMN_NAME_POSTER_URL)) {
            String posterUrl = contentValues.getAsString(FavoriteEntry.COLUMN_NAME_POSTER_URL);
            if(posterUrl == null) {
                throw new IllegalArgumentException("Movie requires a poster url");
            }
        }

        // If there are no values to update, then don't try to update the database
        if(contentValues.size() == 0) {
            return 0;
        }

        // Get writable database
        SQLiteDatabase database = movieDbHelper.getWritableDatabase();

        //Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(FavoriteEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        // Notify all listeners that the data has changed for the favorites content URI
        // uri: content://com.example.android.popularmovies/favorites
        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of database rows affected by the update statement
        return rowsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                return FavoriteEntry.CONTENT_LIST_TYPE;
            case FAVORITES_ID:
                return FavoriteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
