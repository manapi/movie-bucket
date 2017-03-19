package ift2905.moviebucket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;

public class MovieView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_view);

        // TODO: Display movie informations
        // TODO: Add to list and schedule buttons
    }
}
