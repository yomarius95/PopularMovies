package com.example.android.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract.*;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.popularmovies.MainActivity.MOVIE_OBJECT_STRING;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerItemClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private String movieId;
    private boolean isFavorite = false;
    private Movie mMovie;
    private Uri currentMovieUri;
    private Bitmap posterBitmap;

    private static final int FAVORITE_LOADER_ID = 2;
    private static final int REVIEWS_LOADER_ID = 3;
    private static final int TRAILERS_LOADER_ID = 4;

    private static final String BASE_REQUEST_URL = "https://api.themoviedb.org/3/movie";

    private LoaderManager.LoaderCallbacks<List<Review>> reviewLoaderListener
            = new LoaderManager.LoaderCallbacks<List<Review>>() {
        @Override
        public Loader<List<Review>> onCreateLoader(int id, Bundle args) {
            Uri baseUri;
            baseUri = Uri.parse(BASE_REQUEST_URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();

            uriBuilder.appendPath(movieId);
            uriBuilder.appendPath("reviews");
            uriBuilder.appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_TOKEN);

            return new ReviewLoader(DetailActivity.this, uriBuilder.toString());
        }

        @Override
        public void onLoadFinished(Loader<List<Review>> loader, List<Review> reviews) {
            mReviewAdapter.setReviewData((ArrayList<Review>) reviews);
            mReviewRV.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoaderReset(Loader<List<Review>> loader) {
            mReviewAdapter.resetReviewData();
        }
    };

    private LoaderManager.LoaderCallbacks<List<Trailer>> trailerLoaderListener
            = new LoaderManager.LoaderCallbacks<List<Trailer>>() {
        @Override
        public Loader<List<Trailer>> onCreateLoader(int id, Bundle args) {

            Uri baseUri;
            baseUri = Uri.parse(BASE_REQUEST_URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();

            uriBuilder.appendPath(movieId);
            uriBuilder.appendPath("videos");
            uriBuilder.appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_TOKEN);

            return new TrailerLoader(DetailActivity.this, uriBuilder.toString());
        }

        @Override
        public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> trailers) {
            mTrailerAdapter.setTrailerData((ArrayList<Trailer>) trailers);
            mTrailerRV.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoaderReset(Loader<List<Trailer>> loader) {
            mTrailerAdapter.resetTrailerData();
        }
    };

    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    @BindView(R.id.detail_background_image)
    ImageView background;
    @BindView(R.id.detail_tv_title)
    TextView title;
    @BindView(R.id.detail_tv_release_date)
    TextView releaseDate;
    @BindView(R.id.detail_tv_vote_average)
    TextView voteAverage;
    @BindView(R.id.detail_tv_synopsis)
    TextView synopsis;
    @BindView(R.id.rv_trailer)
    RecyclerView mTrailerRV;
    @BindView(R.id.rv_review)
    RecyclerView mReviewRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(MOVIE_OBJECT_STRING)) {

            mMovie = intent.getExtras().getParcelable(MOVIE_OBJECT_STRING);
            assert mMovie != null;
            setTitle(mMovie.getTitle());
            movieId = mMovie.getId();
            currentMovieUri = ContentUris.withAppendedId(FavoriteEntry.CONTENT_URI, Integer.parseInt(movieId));

            if(mMovie.getPosterUrl() == null){
                getLoaderManager().initLoader(FAVORITE_LOADER_ID, null, this);
            } else {
                mTrailerRV.setNestedScrollingEnabled(false);
                mReviewRV.setNestedScrollingEnabled(false);

                mTrailerRV.setLayoutManager(new LinearLayoutManager(this));
                mReviewRV.setLayoutManager(new LinearLayoutManager(this));

                mTrailerAdapter = new TrailerAdapter(this);
                mReviewAdapter = new ReviewAdapter();

                mTrailerRV.setAdapter(mTrailerAdapter);
                mReviewRV.setAdapter(mReviewAdapter);

                getLoaderManager().initLoader(TRAILERS_LOADER_ID, null, trailerLoaderListener);
                getLoaderManager().initLoader(REVIEWS_LOADER_ID, null, reviewLoaderListener);

                Picasso.with(this).load("http://image.tmdb.org/t/p/w500" + mMovie.getPosterUrl()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        posterBitmap = bitmap;
                        background.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }
        }

        title.setText(mMovie.getTitle());
        releaseDate.append(" " + mMovie.getReleaseDate());
        voteAverage.append(" " + mMovie.getRating());
        synopsis.setText(mMovie.getSynopsis());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorite_menu, menu);
        if(isMovieFavorite()){
            menu.getItem(0).setIcon(R.drawable.ic_favorite_white_24dp);
        } else {
            menu.getItem(0).setIcon(R.drawable.ic_favorite_border_white_24dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toggle_favorite:
                if (isFavorite) {
                    isFavorite = false;
                    item.setIcon(R.drawable.ic_favorite_border_white_24dp);
                    deleteMovie();
                } else {
                    isFavorite = true;
                    item.setIcon(R.drawable.ic_favorite_white_24dp);
                    saveMovie();
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveMovie() {
        ContentValues values = new ContentValues();
        values.put(FavoriteEntry._ID, Integer.parseInt(mMovie.getId()));
        values.put(FavoriteEntry.COLUMN_NAME_TITLE, mMovie.getTitle());
        values.put(FavoriteEntry.COLUMN_NAME_SYNOPSIS, mMovie.getSynopsis());
        values.put(FavoriteEntry.COLUMN_NAME_RATING, mMovie.getRating());
        values.put(FavoriteEntry.COLUMN_NAME_RELEASE_DATE, mMovie.getReleaseDate());
        values.put(FavoriteEntry.COLUMN_NAME_POSTER_URL, DbBitmapUtility.getBytes(posterBitmap));

        Uri newUri = getContentResolver().insert(FavoriteEntry.CONTENT_URI, values);

        long newRowId = ContentUris.parseId(newUri);
        Toast.makeText(this, getString(R.string.movie_saved, newRowId), Toast.LENGTH_SHORT).show();
    }

    private void deleteMovie() {
        getContentResolver().delete(currentMovieUri, null, null);
    }

    @Override
    public void onTrailerItemClick(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private boolean isMovieFavorite(){
        Cursor cursor = getContentResolver().query(currentMovieUri, null, null, null, null);
        if(cursor.moveToNext()){
            isFavorite = true;
        } else {
            isFavorite = false;
        }
        return isFavorite;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                FavoriteEntry._ID,
                FavoriteEntry.COLUMN_NAME_POSTER_URL
        };

        return new CursorLoader(this,
                currentMovieUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToNext()){
            int posterColumnIndex = cursor.getColumnIndex(FavoriteEntry.COLUMN_NAME_POSTER_URL);
            byte[] array = cursor.getBlob(posterColumnIndex);
            background.setImageBitmap(DbBitmapUtility.getImage(array));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        background.setImageBitmap(null);
    }
}
