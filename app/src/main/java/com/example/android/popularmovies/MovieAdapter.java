package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private Context mContext;
    private ArrayList<Movie> mMovies;

    public MovieAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.movie_grid_item, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w342" + mMovies.get(position).getPosterUrl()).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        if (mMovies == null) return 0;
        return mMovies.size();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mImageView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mImageView = view.findViewById(R.id.iv_movie);
        }
    }

    public void setMovieData(ArrayList<Movie> movieData) {
        mMovies = movieData;
        notifyDataSetChanged();
    }

    public void resetMovieData() {
        mMovies = null;
    }
}
