package com.parrish.android.portfolio.adaptors.movie;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.parrish.android.portfolio.R;
import com.parrish.android.portfolio.helpers.Helper;
import com.squareup.picasso.Picasso;

import com.parrish.android.portfolio.models.movie.Result;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdaptor extends RecyclerView.Adapter<MovieAdaptor.MovieViewHolder> {
    @SuppressWarnings("unused")
    private final static String TAG = MovieAdaptor.class.getSimpleName();

    private Result[] mResults;
    private final Context mContext;
    private final MovieClickListener mOnMovieClickListener;

    public interface MovieClickListener {
        void onMovieClickListener(Result result, View view);
    }

    public MovieAdaptor(Context context, MovieClickListener movieClickListener) {
        this.mContext = context;
        this.mOnMovieClickListener = movieClickListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        view.setFocusable(true);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder movieViewHolder, int i) {
        Picasso.get().load(Helper.getThumbNailURL(mResults[i]))
                .into(movieViewHolder.mMovieThumbnail);
        ViewCompat.setTransitionName(movieViewHolder.mMovieThumbnail,
                mResults[i].getTitle());
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

    public class MovieViewHolder extends ViewHolder implements View.OnClickListener {

        @BindView(R.id.movie_thumbnail)
        public ImageView mMovieThumbnail;

        private MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Result result = mResults[position];
            mOnMovieClickListener.onMovieClickListener(result, view);
        }
    }
}
