package ift2905.moviebucket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;



public class Explore extends AppCompatActivity {

    final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TmdbMovies movies = new TmdbApi(API_KEY).getMovies();
        MovieDb movie = movies.getMovie(5353, "en");
    }
}
