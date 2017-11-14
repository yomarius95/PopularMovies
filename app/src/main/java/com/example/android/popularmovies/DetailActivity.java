package com.example.android.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract.*;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import static com.example.android.popularmovies.MainActivity.MOVIE_OBJECT_STRING;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerItemClickListener {

    private String movieId;
    private boolean isFavorite = false;
    private Movie mMovie;

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

        mTrailerRV.setNestedScrollingEnabled(false);
        mReviewRV.setNestedScrollingEnabled(false);

        mTrailerRV.setLayoutManager(new LinearLayoutManager(this));
        mReviewRV.setLayoutManager(new LinearLayoutManager(this));

        mTrailerAdapter = new TrailerAdapter(this);
        mReviewAdapter = new ReviewAdapter();

        mTrailerRV.setAdapter(mTrailerAdapter);
        mReviewRV.setAdapter(mReviewAdapter);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(MOVIE_OBJECT_STRING)) {

            mMovie = intent.getExtras().getParcelable(MOVIE_OBJECT_STRING);
            assert mMovie != null;
            setTitle(mMovie.getTitle());
            movieId = mMovie.getId();

            getLoaderManager().initLoader(TRAILERS_LOADER_ID, null, trailerLoaderListener);
            getLoaderManager().initLoader(REVIEWS_LOADER_ID, null, reviewLoaderListener);

            Picasso.with(this).load("http://image.tmdb.org/t/p/w500" + mMovie.getPosterUrl()).into(background);

            title.setText(mMovie.getTitle());
            releaseDate.append(" " + mMovie.getReleaseDate());
            voteAverage.append(" " + mMovie.getRating());
            synopsis.setText(mMovie.getSynopsis());

            Log.i("DetailActivity.java", mMovie.getId());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorite_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toggle_favorite:
                if (isFavorite) {
                    isFavorite = false;
                    item.setIcon(R.drawable.ic_favorite_border_white_24dp);
                    //remove from database
                } else {
                    isFavorite = true;
                    item.setIcon(R.drawable.ic_favorite_white_24dp);
                    //insert in to database
                    saveMovie();
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveMovie() {
        ContentValues values = new ContentValues();
        values.put(FavoriteEntry.COLUMN_NAME_TITLE, mMovie.getTitle());
        values.put(FavoriteEntry.COLUMN_NAME_SYNOPSIS, mMovie.getSynopsis());
        values.put(FavoriteEntry.COLUMN_NAME_RATING, mMovie.getRating());
        values.put(FavoriteEntry.COLUMN_NAME_RELEASE_DATE, mMovie.getReleaseDate());
        values.put(FavoriteEntry.COLUMN_NAME_POSTER_URL, mMovie.getPosterUrl());

        Uri newUri = getContentResolver().insert(FavoriteEntry.CONTENT_URI, values);

        long newRowId = ContentUris.parseId(newUri);
        Toast.makeText(this, getString(R.string.movie_saved, newRowId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTrailerItemClick(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
