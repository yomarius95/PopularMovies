package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private ArrayList<Review> mReviews;

    ReviewAdapter() {

    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.review_list_item, parent, false);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        holder.mAuthorTV.append(mReviews.get(position).getAuthor());
        holder.mContentTV.setText(mReviews.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        if (mReviews == null) return 0;
        return mReviews.size();
    }

    class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        final TextView mAuthorTV;
        final TextView mContentTV;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            mAuthorTV = itemView.findViewById(R.id.review_author_tv);
            mContentTV = itemView.findViewById(R.id.review_content_tv);
        }
    }

    void setReviewData(ArrayList<Review> reviewData) {
        mReviews = reviewData;
        notifyDataSetChanged();
    }

    void resetReviewData() {
        mReviews = null;
    }
}
