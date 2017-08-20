package com.example.android.popularmovies;

public class Movie {
    private String mId;
    private String mTitle;
    private String mPosterUrl;
    private String mSynopsis;
    private String mRating;
    private String mReleaseDate;

    Movie(String id, String title, String posterUrl, String synopsis, String rating, String releaseDate) {
        mId = id;
        mTitle = title;
        mPosterUrl = posterUrl;
        mSynopsis = synopsis;
        mRating = rating;
        mReleaseDate = releaseDate;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPosterUrl() {
        return mPosterUrl;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public String getRating() {
        return mRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }
}
