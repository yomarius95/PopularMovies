package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieItemClickListener, LoaderManager.LoaderCallbacks<List<Movie>> {

    private final static String REQUEST_URL = "https://api.themoviedb.org/3/discover/movie?language=en-US&include_adult=false&include_video=false&page=1";
    private final static String API_KEY_STRING = "788da7e2e8ea7beb70d996b49ca373e6";
    private final static String API_KEY_KEY = "api_key";
    private final static String SORT_BY_KEY = "sort_by";
    private final static String SORT_BY_RATING = "vote_average.desc";
    private final static String VOTE_COUNT_KEY = "vote_count.gte";
    private final static String VOTE_COUNT_VALUE = "5000";
    public final static String MOVIE_OBJECT_STRING = "movie";
    private static final int MOVIES_LOADER_ID = 1;

    private boolean sortByVote = false;
    private LoaderManager loaderManager;

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_grid);
        mRecyclerView.setHasFixedSize(true);

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
        else{
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        mMovieAdapter = new MovieAdapter(this, this);
        mRecyclerView.setAdapter(mMovieAdapter);

        mRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        loaderManager = getLoaderManager();
        loaderManager.initLoader(MOVIES_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        MenuItem item = menu.findItem(R.id.sort_menu);
        Spinner spinner = (Spinner) item.getActionView();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.order));
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 1:
                        sortByVote = true;
                        break;
                    default:
                        sortByVote = false;
                        break;
                }
                loaderManager.restartLoader(MOVIES_LOADER_ID, null, MainActivity.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        return true;
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {

        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter(API_KEY_KEY, API_KEY_STRING);

        if(sortByVote) {
            uriBuilder.appendQueryParameter(SORT_BY_KEY, SORT_BY_RATING);
            uriBuilder.appendQueryParameter(VOTE_COUNT_KEY, VOTE_COUNT_VALUE);
        }

        return new MovieLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        mMovieAdapter.setMovieData((ArrayList<Movie>) movies);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mMovieAdapter.resetMovieData();
    }

    @Override
    public void onMovieItemClick(Movie clickedMovie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(MOVIE_OBJECT_STRING, clickedMovie);
        startActivity(intent);
    }
}
