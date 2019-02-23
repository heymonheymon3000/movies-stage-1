package com.parrish.android.portfolio.activities.movie.details;

import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Callback;

import com.parrish.android.portfolio.BuildConfig;
import com.parrish.android.portfolio.adaptors.movie.details.MovieTrailersAdaptor;
import com.parrish.android.portfolio.helpers.Helper;
import com.parrish.android.portfolio.interfaces.MovieService;
import com.parrish.android.portfolio.models.movie.details.MovieDetailsResponse;
import com.parrish.android.portfolio.models.movie.Result;
import com.parrish.android.portfolio.R;
import com.parrish.android.portfolio.models.movie.details.MovieVideoResponse;
import com.parrish.android.portfolio.network.ApiUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MovieDetailsActivity extends AppCompatActivity
        implements MovieTrailersAdaptor.TrailerClickListener {

    private final String TAG = MovieDetailsActivity.class.getSimpleName();

    @BindView(R.id.movie_details_title)
    public TextView movieDetailsTitleTextView;

    @BindView(R.id.movie_thumbnail)
    public ImageView movieThumbnailImageView;

    @BindView(R.id.movie_year)
    public TextView movieYear;

    @BindView(R.id.movie_duration)
    public TextView movieDuration;

    @BindView(R.id.movie_rating)
    public TextView movieRating;

    @BindView(R.id.movie_favorite_button)
    public TextView movieFavoriteButton;

    @BindView(R.id.movie_description)
    public TextView movieDescription;

    @BindView(R.id.trailers_tv)
    public TextView trailersTextView;

    @BindView(R.id.trailers_rv)
    public RecyclerView recyclerView;

    private MovieTrailersAdaptor movieTrailersAdaptor;

    private final static String RESULT_CACHE_KEY = "RESULT_CACHE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        supportPostponeEnterTransition();
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if(intent.hasExtra(Intent.EXTRA_TEXT)) {
            Result result = intent.getParcelableExtra(Intent.EXTRA_TEXT);

            movieDetailsTitleTextView.setText(result.getTitle());
            movieThumbnailImageView.setTransitionName(result.getTitle());

            Picasso.get().load(Helper.getThumbNailURL(result))
                .into(movieThumbnailImageView, new Callback(){
                    @Override
                    public void onSuccess() {
                        supportStartPostponedEnterTransition();
                        scheduleStartPostponedTransition(movieThumbnailImageView);
                    }

                    @Override
                    public void onError(Exception e) {
                        supportStartPostponedEnterTransition();
                        scheduleStartPostponedTransition(movieThumbnailImageView);
                    }

                    /**
                     * Schedules the shared element transition to be started immediately
                     * after the shared element has been measured and laid out within the
                     * activity's view hierarchy. Some common places where it might make
                     * sense to call this method are:
                     *
                     * (1) Inside a Fragment's onCreateView() method (if the shared element
                     *     lives inside a Fragment hosted by the called Activity).
                     *
                     * (2) Inside a Picasso Callback object (if you need to wait for Picasso to
                     *     asynchronously load/scale a bitmap before the transition can begin).
                     **/
                    private void scheduleStartPostponedTransition(final View sharedElement) {
                        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                            new ViewTreeObserver.OnPreDrawListener() {
                                @Override
                                public boolean onPreDraw() {
                                    sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                                    startPostponedEnterTransition();
                                    return true;
                                }
                            });
                    }
                });
            movieYear.setText(getYear(result.getReleaseDate()));
            setMovieDuration(result.getId());
            movieRating.setText(getVoteAverage(result.getVoteAverage()));
            movieDescription.setText(result.getOverview());
            trailersTextView.setText(getString(R.string.trailers));
            setupRecyclerView();

            if(savedInstanceState == null || !savedInstanceState.containsKey(RESULT_CACHE_KEY)) {
                loadTrailers(result.getId());
            } else {
                movieTrailersAdaptor.setResults(
                    (com.parrish.android.portfolio.models.movie.details.Result[])
                        savedInstanceState.getParcelableArray(RESULT_CACHE_KEY));
            }

            Animation animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            View view = findViewById(R.id.container);
            view.startAnimation(animFadeIn);
        }

        ActionBar actionBar = this.getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray(RESULT_CACHE_KEY, movieTrailersAdaptor.getResults());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTrailerClickListener(com.parrish.android.portfolio.models.movie.details.Result result) {

    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        movieTrailersAdaptor = new MovieTrailersAdaptor(this, this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(movieTrailersAdaptor);
    }

    private void loadTrailers(Integer id) {
        MovieService movieService = ApiUtils.getMovieService();
        movieService.getMovieTrailers(id, BuildConfig.MOVIE_API_KEY)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<MovieVideoResponse>() {
                private com.parrish.android.portfolio.models.movie.details.Result[] resultCache;

                @Override
                public void onSubscribe(Disposable d) {
                    movieTrailersAdaptor.setResults(null);
                }

                @Override
                public void onNext(MovieVideoResponse movieVideoResponse) {
                    //TODO: Terry redo this.
                    List<com.parrish.android.portfolio.models.movie.details.Result> results =
                        movieVideoResponse.getResults();

                    List<com.parrish.android.portfolio.models.movie.details.Result> transformedResults = new ArrayList<>();

                    for(com.parrish.android.portfolio.models.movie.details.Result result : results) {
                        if(result.getType().equals("Trailer")) {
                            transformedResults.add(result);
                        }
                    }

                    resultCache = new com.parrish.android.portfolio.models.movie.details.Result[transformedResults.size()];
                    for(int i = 0; i < resultCache.length; i++) {
                        resultCache[i] = transformedResults.get(i);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(MovieDetailsActivity.this,
                            getString(R.string.no_internet_connection),
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onComplete() {
                    movieTrailersAdaptor.setResults(resultCache);
                }
            });
    }

    private String getYear(String releaseDate) {
        return releaseDate.split("-")[0];
    }

    private void setMovieDuration(Integer id) {
        MovieService movieService = ApiUtils.getMovieService();
        movieService.getMovieDetails(id, BuildConfig.MOVIE_API_KEY)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<MovieDetailsResponse>() {
                private Integer runTime;

                @Override
                public void onSubscribe(Disposable d) {}

                @Override
                public void onNext(MovieDetailsResponse movieDetailsResponse) {
                    runTime = movieDetailsResponse.getRuntime();
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(MovieDetailsActivity.this,
                            getString(R.string.no_internet_connection),
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onComplete() {
                    StringBuilder sb = new StringBuilder();
                    sb.append(runTime);
                    sb.append("min ");
                    movieDuration.setText(sb.toString());
                }
            });
    }

    private String getVoteAverage(Double voteAverage) {
        StringBuilder sb = new StringBuilder();
        sb.append(voteAverage);
        sb.append("/");
        sb.append(10);
        return sb.toString();
    }
}
