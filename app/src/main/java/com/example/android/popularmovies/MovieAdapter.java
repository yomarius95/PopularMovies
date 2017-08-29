package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private Context mContext;
    private ArrayList<Movie> mMovies;

    private final MovieItemClickListener mClickHandler;

    interface MovieItemClickListener {
        void onMovieItemClick(Movie clickedMovie, ImageView sharedImageView);
    }

    MovieAdapter(Context context, MovieItemClickListener listener) {
        mContext = context;
        mClickHandler = listener;
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

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        final ImageView mImageView;

        MovieAdapterViewHolder(View view) {
            super(view);
            mImageView = view.findViewById(R.id.iv_movie);
            mImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            ImageView imageView = (ImageView) view;
            mClickHandler.onMovieItemClick(mMovies.get(clickedPosition), imageView);
        }
    }

    void setMovieData(ArrayList<Movie> movieData) {
        mMovies = movieData;
        notifyDataSetChanged();
    }

    void resetMovieData() {
        mMovies = null;
    }
}
