package com.parrish.android.portfolio.models.movie.details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("WeakerAccess")
public class SpokenLanguage {
    @SerializedName("iso_639_1")
    @Expose
    private String iso6391;
    @SerializedName("name")
    @Expose
    private String name;

    @SuppressWarnings("unused")
    public String getIso6391() {
        return iso6391;
    }

    @SuppressWarnings("unused")
    public void setIso6391(String iso6391) {
        this.iso6391 = iso6391;
    }

    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }

    @SuppressWarnings("unused")
    public void setName(String name) {
        this.name = name;
    }
}
