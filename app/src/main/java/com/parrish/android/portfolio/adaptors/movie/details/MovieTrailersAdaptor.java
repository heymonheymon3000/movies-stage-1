package com.parrish.android.portfolio.adaptors.movie.details;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parrish.android.portfolio.R;
import com.parrish.android.portfolio.models.movie.details.Result;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieTrailersAdaptor extends
        RecyclerView.Adapter<MovieTrailersAdaptor.MovieTrailersViewHolder> {
    private final static String TAG = MovieTrailersAdaptor.class.getSimpleName();

    private Result[] mResults;
    private final Context mContext;
    private final TrailerClickListener mTrailerClickListener;

    public interface TrailerClickListener {
        void onTrailerClickListener(Result result);
    }

    public MovieTrailersAdaptor(Context context, TrailerClickListener trailerClickListener) {
        this.mContext = context;
        this.mTrailerClickListener = trailerClickListener;
    }

    @NonNull
    @Override
    public MovieTrailersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int layoutIdForListItem = R.layout.movie_trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        view.setFocusable(true);

        return new MovieTrailersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieTrailersViewHolder movieTrailersViewHolder, int i) {
        Result result = mResults[i];
        movieTrailersViewHolder.trailerTitle.setText(result.getName().trim());
        movieTrailersViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTrailerClickListener.onTrailerClickListener(result);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mResults == null) return 0;
        return mResults.length;
    }

    public void setResults(Result[] results) {
        this.mResults = results;
        notifyDataSetChanged();
    }

    public Result[] getResults() {
        return mResults;
    }

    public class MovieTrailersViewHolder extends ViewHolder {
        @BindView(R.id.play_icon)
        public ImageView imageView;

        @BindView(R.id.trailer_title)
        public TextView trailerTitle;

        public MovieTrailersViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
