package com.example.android.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;

import java.util.List;

public class ReviewLoader extends AsyncTaskLoader<List<Review>> {

    private String mUrl;

    ReviewLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Review> loadInBackground() {
        if (TextUtils.isEmpty(mUrl)) {
            return null;
        }
        return QueryUtils.fetchReviewsData(mUrl);
    }
}
