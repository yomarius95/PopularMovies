package com.example.android.popularmovies;

public class Trailer {
    private String mName;
    private String mUrl;

    Trailer(String name, String url) {
        mName = name;
        mUrl = "https://www.youtube.com/watch?v=" + url;
    }

    public String getName() {
        return mName;
    }

    public String getUrl() {
        return mUrl;
    }
}
