package ift2905.moviebucket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;

public class MovieView extends AppCompatActivity {

    private int movieId = 0;
    final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";
    final String LANG = "en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_view);

        movieId = (int) getIntent().getExtras().getLong("movie");
        //TmdbApi api = new TmdbApi(API_KEY);
        //MovieDb movie = getMovie (movieId, LANG);
        //String title = api.getMovies().;
        //setTitle(Long.toString(movieId));
        setTitle(Integer.toString(movieId));

        // TODO: Display movie informations
        // TODO: Add to list and schedule buttons
    }
}
