package com.example.android.popularmovies;

public class Review {
    private String mAuthor;
    private String mContent;
    private String mUrl;

    Review(String author, String content, String url) {
        mAuthor = author;
        mContent = content;
        mUrl = url;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    public String getUrl() {
        return mUrl;
    }
}
