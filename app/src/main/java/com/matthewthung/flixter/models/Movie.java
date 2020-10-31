package com.matthewthung.flixter.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Movie {

    // Data members:
    int movieId;
    String backdropPath;
    String posterPath;
    String title;
    String overview;
    double rating;
    double popularity;

    // Empty constructor needed by the Parceler library.
    public Movie() {}

    // Constructor:
    public Movie(JSONObject jsonObject) throws JSONException {
        backdropPath = jsonObject.getString("backdrop_path");
        posterPath = jsonObject.getString("poster_path");
        title = jsonObject.getString("title");
        overview = jsonObject.getString("overview");
        rating = jsonObject.getDouble("vote_average");
        movieId = jsonObject.getInt("id");
        popularity = jsonObject.getDouble("popularity");
    }


    // Methods:
    public static List<Movie> fromJsonArray(JSONArray movieJsonArray) throws JSONException {
    List<Movie> movies = new ArrayList<>();

    for (int i = 0; i < movieJsonArray.length(); i++){
        movies.add(new Movie(movieJsonArray.getJSONObject(i)));
    }

    return movies;
    }

    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", backdropPath);
    }

    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", posterPath);
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public double getRating() { return rating; }

    public double getPopularity() { return popularity; }

    public int getMovieId() { return movieId; }
}
