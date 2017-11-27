package com.example.android.popularmovies;

import android.app.ActivityOptions;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.android.popularmovies.data.MovieContract.*;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieItemClickListener, LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String TOP_RATED_REQUEST_URL = "http://api.themoviedb.org/3/movie/top_rated";
    private static final String POPULAR_REQUEST_URL = "https://api.themoviedb.org/3/movie/popular";
    private static final String API_KEY_KEY = "api_key";
    public static final String MOVIE_OBJECT_STRING = "movie";
    private static final int MOVIES_LOADER_ID = 1;
    private static final int FAVORITES_LOADER_ID = 2;
    private static final String SORT_BY = "sort";

    private boolean sortByRating = false;
    private LoaderManager loaderManager;
    private ConnectivityManager cm;
    private NetworkInfo activeNetwork;
    private MovieAdapter mMovieAdapter;
    private int spinnerPosition = -1;
    private Spinner spinner;

    @BindView(R.id.loading_spinner)
    ProgressBar loadingSpinner;
    @BindView(R.id.empty_view)
    TextView emptyView;
    @BindView(R.id.rv_movie_grid)
    RecyclerView mRecyclerView;

    private LoaderManager.LoaderCallbacks<Cursor> favoritesLoaderListener
            = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            String[] projection = {
                    FavoriteEntry._ID,
                    FavoriteEntry.COLUMN_NAME_TITLE,
                    FavoriteEntry.COLUMN_NAME_SYNOPSIS,
                    FavoriteEntry.COLUMN_NAME_RATING,
                    FavoriteEntry.COLUMN_NAME_RELEASE_DATE,
                    FavoriteEntry.COLUMN_NAME_POSTER_URL
            };

            return new CursorLoader(MainActivity.this,
                    FavoriteEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            ArrayList<Movie> movieList = new ArrayList<>();
            while(cursor.moveToNext()){

                int idColumnIndex = cursor.getColumnIndex(FavoriteEntry._ID);
                int titleColumnIndex = cursor.getColumnIndex(FavoriteEntry.COLUMN_NAME_TITLE);
                int synopsisColumnIndex = cursor.getColumnIndex(FavoriteEntry.COLUMN_NAME_SYNOPSIS);
                int ratingColumnIndex = cursor.getColumnIndex(FavoriteEntry.COLUMN_NAME_RATING);
                int releaseDateColumnIndex = cursor.getColumnIndex(FavoriteEntry.COLUMN_NAME_RELEASE_DATE);
                int posterUrlColumnIndex = cursor.getColumnIndex(FavoriteEntry.COLUMN_NAME_POSTER_URL);

                int id = cursor.getInt(idColumnIndex);
                String title = cursor.getString(titleColumnIndex);
                String synopsis = cursor.getString(synopsisColumnIndex);
                String rating = cursor.getString(ratingColumnIndex);
                String releaseDate = cursor.getString(releaseDateColumnIndex);
                byte[] posterByteArray = cursor.getBlob(posterUrlColumnIndex);

                Movie movie = new Movie(String.valueOf(id), title, synopsis, rating, releaseDate, posterByteArray);
                movieList.add(movie);
            }

            mMovieAdapter.setMovieData(movieList);
            mRecyclerView.setVisibility(View.VISIBLE);
            loadingSpinner.setVisibility(View.GONE);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mMovieAdapter.resetMovieData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            spinnerPosition = savedInstanceState.getInt(SORT_BY);
        }

        mRecyclerView.setHasFixedSize(true);

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }
        else{
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        }

        mMovieAdapter = new MovieAdapter(this, this);
        mRecyclerView.setAdapter(mMovieAdapter);

        mRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        loaderManager = getLoaderManager();

        if(isConnected) {
            loaderManager.initLoader(MOVIES_LOADER_ID, null, this);
        } else {
            loadingSpinner.setVisibility(View.GONE);
            emptyView.setText(R.string.no_internet);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        MenuItem item = menu.findItem(R.id.sort_menu);
        spinner = (Spinner) item.getActionView();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.order));
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                boolean loadFavorites = false;

                switch (i) {
                    case 1:
                        sortByRating = true;
                        break;
                    case 2:
                        loadFavorites = true;
                        break;
                    default:
                        sortByRating = false;
                        break;
                }
                activeNetwork = cm.getActiveNetworkInfo();
                if(loadFavorites){
                    emptyView.setVisibility(View.GONE);
                    loadingSpinner.setVisibility(View.VISIBLE);
                    loaderManager.restartLoader(FAVORITES_LOADER_ID, null, favoritesLoaderListener);
                } else if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                    emptyView.setVisibility(View.GONE);
                    loadingSpinner.setVisibility(View.VISIBLE);
                    loaderManager.restartLoader(MOVIES_LOADER_ID, null, MainActivity.this);
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    loadingSpinner.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setText(R.string.no_internet);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        if (spinnerPosition != -1) {
            spinner.setSelection(spinnerPosition);
        }
        return true;
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri;

        if (sortByRating) {
            baseUri = Uri.parse(TOP_RATED_REQUEST_URL);
        } else {
            baseUri = Uri.parse(POPULAR_REQUEST_URL);
        }
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter(API_KEY_KEY, BuildConfig.THE_MOVIE_DB_API_TOKEN);

        return new MovieLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        mMovieAdapter.setMovieData((ArrayList<Movie>) movies);
        mRecyclerView.setVisibility(View.VISIBLE);
        loadingSpinner.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mMovieAdapter.resetMovieData();
    }

    @Override
    public void onMovieItemClick(Movie clickedMovie, ImageView sharedImageView) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(MOVIE_OBJECT_STRING, clickedMovie);
        Bundle bundle = ActivityOptions
                .makeSceneTransitionAnimation(
                        this,
                        sharedImageView,
                        sharedImageView.getTransitionName())
                .toBundle();
        startActivity(intent, bundle);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SORT_BY, spinner.getSelectedItemPosition());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences();
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }

    private void savePreferences(){
        SharedPreferences spinnerPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = spinnerPreferences.edit();
        editor.putInt(SORT_BY, spinner.getSelectedItemPosition());
        editor.apply();
    }

    private void loadPreferences(){
        SharedPreferences spinnerPreferences = getPreferences(MODE_PRIVATE);
        spinnerPosition = spinnerPreferences.getInt(SORT_BY, -1);
    }
}
