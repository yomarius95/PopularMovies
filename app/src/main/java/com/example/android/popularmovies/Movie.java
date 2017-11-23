package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private String mId;
    private String mTitle;
    private String mPosterUrl;
    private String mSynopsis;
    private String mRating;
    private String mReleaseDate;
    private byte[] mPosterByteArray;

    Movie(String id, String title, String posterUrl, String synopsis, String rating, String releaseDate) {
        mId = id;
        mTitle = title;
        mPosterUrl = posterUrl;
        mSynopsis = synopsis;
        mRating = rating;
        mReleaseDate = releaseDate;
        mPosterByteArray = null;
    }

    Movie(String id, String title, String synopsis, String rating, String releaseDate, byte[] posterByteArray) {
        mId = id;
        mTitle = title;
        mPosterUrl = null;
        mSynopsis = synopsis;
        mRating = rating;
        mReleaseDate = releaseDate;
        mPosterByteArray = posterByteArray;
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

    public byte[] getPosterByteArray() {
        return mPosterByteArray;
    }

    private Movie(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mPosterUrl = in.readString();
        mSynopsis = in.readString();
        mRating = in.readString();
        mReleaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mPosterUrl);
        dest.writeString(mSynopsis);
        dest.writeString(mRating);
        dest.writeString(mReleaseDate);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
