package com.parrish.android.portfolio.network;

import com.parrish.android.portfolio.interfaces.MovieService;

public class ApiUtils {
    private static final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";

    public static MovieService getMovieService() {
        return RetrofitClient.getClient(MOVIE_BASE_URL).create(MovieService.class);
    }
}
