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
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieItemClickListener, LoaderManager.LoaderCallbacks<List<Movie>> {

    private final static String REQUEST_URL = "https://api.themoviedb.org/3/discover/movie?language=en-US&include_adult=false&include_video=false&page=1&api_key=788da7e2e8ea7beb70d996b49ca373e6&sort_by=vote_average.desc";
    private final static String NEW_REQUEST_URL = "https://api.themoviedb.org/3/discover/movie?api_key=788da7e2e8ea7beb70d996b49ca373e6&language=en-US&sort_by=vote_average.desc&include_adult=false&include_video=false&page=1&vote_count.gte=1000";
    private static final int MOVIES_LOADER_ID = 1;

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

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(MOVIES_LOADER_ID, null, this);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
        return new MovieLoader(this, NEW_REQUEST_URL);
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
        intent.putExtra("movie", clickedMovie);
        startActivity(intent);
    }
}
