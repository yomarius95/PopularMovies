package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private ArrayList<Trailer> mTrailers;

    private final TrailerItemClickListener mClickHandler;

    interface TrailerItemClickListener {
        void onTrailerItemClick(String url);
    }

    TrailerAdapter(TrailerItemClickListener listener) {
        mClickHandler = listener;
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.trailer_list_item, parent, false);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        holder.mTextView.setText(mTrailers.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (mTrailers == null) return 0;
        return mTrailers.size();
    }

    class TrailerAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        final TextView mTextView;

        public TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.trailer_title_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mClickHandler.onTrailerItemClick(mTrailers.get(clickedPosition).getUrl());
        }
    }

    void setTrailerData(ArrayList<Trailer> trailerData) {
        mTrailers = trailerData;
        notifyDataSetChanged();
    }

    void resetTrailerData() {
        mTrailers = null;
    }
}
