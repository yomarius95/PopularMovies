package com.example.android.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    private Movie mMovie;

    @BindView(R.id.detail_background_image)
    ImageView background;
    @BindView(R.id.detail_tv_title)
    TextView title;
    @BindView(R.id.detail_tv_release_date) TextView releaseDate;
    @BindView(R.id.detail_tv_vote_average)
    TextView voteAverage;
    @BindView(R.id.detail_tv_synopsis) TextView synopsis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        mMovie = getIntent().getExtras().getParcelable(MainActivity.MOVIE_OBJECT_STRING);
        setTitle(mMovie.getTitle());

        Picasso.with(this).load("http://image.tmdb.org/t/p/w500" + mMovie.getPosterUrl()).into(background);

        title.setText(mMovie.getTitle());
        releaseDate.setText(mMovie.getReleaseDate());
        voteAverage.setText(mMovie.getRating());
        synopsis.setText(mMovie.getSynopsis());
    }
}
