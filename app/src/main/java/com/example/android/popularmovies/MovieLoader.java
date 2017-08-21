package com.example.android.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;

import java.util.List;

class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    private String mUrl;

    MovieLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        if (TextUtils.isEmpty(mUrl)) {
            return null;
        }
        return QueryUtils.fetchMoviesData(mUrl);
    }
}
