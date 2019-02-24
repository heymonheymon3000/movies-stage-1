package com.parrish.android.portfolio.activities.movie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.parrish.android.portfolio.BuildConfig;
import com.parrish.android.portfolio.R;
import com.parrish.android.portfolio.activities.movie.details.MovieDetailsActivity;
import com.parrish.android.portfolio.activities.settings.SettingsActivity;
import com.parrish.android.portfolio.adaptors.movie.MovieAdaptor;

import android.support.v4.util.Pair;

import com.parrish.android.portfolio.helpers.Helper;
import com.parrish.android.portfolio.interfaces.MovieService;
import com.parrish.android.portfolio.models.movie.MovieResponse;
import com.parrish.android.portfolio.models.movie.Result;
import com.parrish.android.portfolio.network.ApiUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MovieActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        MovieAdaptor.MovieClickListener{
    @SuppressWarnings("unused")
    private final static String TAG = MovieActivity.class.getSimpleName();

    @BindView(R.id.rv_movies)
    public RecyclerView recyclerView;

    private MovieAdaptor movieAdaptor;

    private String sortOrder;

    private final static String RESULT_CACHE_KEY = "RESULT_CACHE_KEY";
    private final static String SORT_ORDER_KEY = "SORT_ORDER_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);
        setupSharedPreferences();
        setupRecyclerView();

        if(savedInstanceState == null || !savedInstanceState.containsKey(RESULT_CACHE_KEY)) {
            loadMovies();
        } else {
            loadMoviesFromCache(savedInstanceState);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_order_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            Intent startSettingActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.pref_sort_order_key))) {
            sortOrder = sharedPreferences.getString(getString(R.string.pref_sort_order_key),
                    getString(R.string.pref_sort_order_default));
            loadMovies();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray(RESULT_CACHE_KEY, movieAdaptor.getResults());
        outState.putString(SORT_ORDER_KEY, sortOrder);
        super.onSaveInstanceState(outState);
    }

    private void setupRecyclerView() {
        int numberOfColumns = Helper.calculateNoOfColumns(this, 100);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns);
        movieAdaptor = new MovieAdaptor(this, this);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(movieAdaptor);
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadSortOrderFromSharedPreferences(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void loadSortOrderFromSharedPreferences(SharedPreferences sharedPreferences) {
        sortOrder = sharedPreferences.getString(getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_default));
    }

    private void loadMovies() {
        MovieService movieService = ApiUtils.getMovieService();
        movieService.getMovies(sortOrder, BuildConfig.MOVIE_API_KEY)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<MovieResponse>() {
                List<Result> results = new ArrayList<>();

                @Override
                public void onSubscribe(Disposable d) {}

                @Override
                public void onNext(MovieResponse movieResponse) {
                    results = movieResponse.getResults();
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(MovieActivity.this,
                        getString(R.string.no_internet_connection),
                        Toast.LENGTH_LONG).show();
                }

                @Override
                public void onComplete() {
                    Result[] r = new Result[results.size()];
                    for(int i = 0; i < results.size(); i++) {
                        r[i] = results.get(i);
                    }
                    movieAdaptor.setResults(r);
                    setTitle(getTitle());
                }

                private String getTitle() {
                    if(sortOrder.equals(getString(R.string.pref_sort_order_most_popular_value))) {
                        return getString(R.string.pref_sort_order_most_popular_label);
                    } else {
                        return getString(R.string.pref_sort_order_top_rated_label);
                    }
                }
            });
    }

    private void loadMoviesFromCache(Bundle savedInstanceState) {
        (new Thread() {
            public void run() {
                movieAdaptor.setResults((Result[])
                        savedInstanceState.getParcelableArray(RESULT_CACHE_KEY));
                sortOrder = savedInstanceState.getString(SORT_ORDER_KEY);
                if(sortOrder != null) {
                    if(sortOrder.equals(getString(R.string.pref_sort_order_most_popular_value))) {
                        setTitle(getString(R.string.pref_sort_order_most_popular_label));
                    } else {
                        setTitle(getString(R.string.pref_sort_order_top_rated_label));
                    }
                } else {
                    setTitle(getString(R.string.pref_sort_order_default));
                }

                Animation animFadeIn = AnimationUtils.loadAnimation(
                        MovieActivity.this, R.anim.fade_in);
                View view = findViewById(R.id.container);
                view.startAnimation(animFadeIn);
            }
        }).start();
    }

    @Override
    public void onMovieClickListener(Result result, View view) {
        Pair<View, String> p1 = Pair.create(view, result.getTitle());
        @SuppressWarnings("unchecked") ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1);
        Intent startMovieDetailsActivity = new Intent(this, MovieDetailsActivity.class);
        startMovieDetailsActivity.putExtra(Intent.EXTRA_TEXT, result);
        startActivity(startMovieDetailsActivity, options.toBundle());
    }
}
