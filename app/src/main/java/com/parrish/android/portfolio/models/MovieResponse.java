package com.parrish.android.portfolio.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class MovieResponse implements Parcelable {

    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("total_results")
    @Expose
    private Integer totalResults;
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @SerializedName("results")
    @Expose
    private List<Result> results = new ArrayList<Result>();

    public MovieResponse() {}

    public MovieResponse(Parcel in) {
        page = in.readInt();
        totalResults = in.readInt();
        totalPages = in.readInt();
        results = in.createTypedArrayList(Result.CREATOR);
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(page);
        parcel.writeInt(totalResults);
        parcel.writeInt(totalPages);
        parcel.writeTypedList(results);
    }

    public static final Creator<MovieResponse> CREATOR = new Creator<MovieResponse>() {

        @Override
        public MovieResponse createFromParcel(Parcel parcel) {
            return new MovieResponse(parcel);
        }

        @Override
        public MovieResponse[] newArray(int size) {
            return new MovieResponse[size];
        }
    };

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("page", page)
                .append("totalResults", totalResults)
                .append("totalPages", totalPages)
                .append("results", results).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(results)
                .append(totalResults).append(page)
                .append(totalPages).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof MovieResponse)) {
            return false;
        }
        MovieResponse rhs = ((MovieResponse) other);
        return new EqualsBuilder().append(results, rhs.results)
                .append(totalResults, rhs.totalResults)
                .append(page, rhs.page).append(totalPages, rhs.totalPages).isEquals();
    }
}