package com.example.android.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;

import java.util.List;

public class TrailerLoader extends AsyncTaskLoader<List<Trailer>> {

    private String mUrl;

    TrailerLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Trailer> loadInBackground() {
        if (TextUtils.isEmpty(mUrl)) {
            return null;
        }
        return QueryUtils.fetchTrailerData(mUrl);
    }
}
