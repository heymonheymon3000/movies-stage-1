package com.parrish.android.portfolio.helpers;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;

import com.parrish.android.portfolio.models.movie.Result;

public class Helper {
    public static int calculateNoOfColumns(Context context, float columnWidthDp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (screenWidthDp / columnWidthDp + 0.5);
    }

    public static String getThumbNailURL(Result result) {
        Uri.Builder builder = new Uri.Builder();
        //noinspection SpellCheckingInspection
        builder.scheme("http")
                .path("image.tmdb.org/t/p")
                .appendPath("w342")
                .appendPath(result.getPosterPath().replace("/",""));
        Uri thumbNailUri = builder.build();

        return thumbNailUri.toString();
    }
}
