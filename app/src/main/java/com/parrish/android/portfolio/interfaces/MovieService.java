package com.parrish.android.portfolio.interfaces;

import com.parrish.android.portfolio.models.movie.MovieResponse;
import com.parrish.android.portfolio.models.movie.details.Genre;
import com.parrish.android.portfolio.models.movie.details.MovieDetailsResponse;
import com.parrish.android.portfolio.models.movie.details.MovieVideoResponse;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import io.reactivex.Observable;

public interface MovieService {
    @GET("{sort}")
    Observable<MovieResponse> getMovies(@Path("sort") String sort, @Query("api_key") String api_key);

    @GET("{id}")
    Observable<MovieDetailsResponse> getMovieDetails(@Path("id") Integer id, @Query("api_key") String api_key);

    @GET("{id}/videos")
    Observable<MovieVideoResponse> getMovieTrailers(@Path("id") Integer id, @Query("api_key") String api_key);
}
