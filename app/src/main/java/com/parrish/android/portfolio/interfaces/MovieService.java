package com.parrish.android.portfolio.interfaces;

import com.parrish.android.portfolio.models.MovieResponse;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import io.reactivex.Observable;

public interface MovieService {
    @GET("{sort}")
    Observable<MovieResponse> getMovies(@Path("sort") String sort, @Query("api_key") String api_key);
}
