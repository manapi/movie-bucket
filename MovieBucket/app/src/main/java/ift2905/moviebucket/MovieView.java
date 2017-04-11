package ift2905.moviebucket;


import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;



public class MovieView extends AppCompatActivity {

    int id;
    final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";
    final String LANG = "en";
    final String BASE_URL = "http://image.tmdb.org/t/p/";
    final String SIZE_SMALL = "w342";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_view);

        id = (int) getIntent().getExtras().getLong("movie");
        MovieFetcher mf = new MovieFetcher(id);
        mf.execute();

        // TODO: Display movie informations
        // TODO: Add to list and schedule buttons
    }

    public class MovieFetcher extends AsyncTask<String, Object, MovieDb> {
        int idMovie;

        public MovieFetcher (int id){
            this.idMovie = id;
        }

        // Get movie
        @Override
        protected MovieDb doInBackground(String... params) {
            TmdbApi api = new TmdbApi(API_KEY);
            TmdbMovies tmdbm = new TmdbMovies(api);
            MovieDb movie = tmdbm.getMovie(idMovie, LANG);
            return movie;
        }

        @Override
        protected void onPostExecute(MovieDb movie) {

            setTitle(movie.getTitle());

            TextView title = (TextView) findViewById(R.id.movieTitle);
            title.setText(movie.getTitle());
            ImageView image = (ImageView) findViewById(R.id.moviePoster);


            Picasso.with(getApplicationContext())
                    .load(BASE_URL + SIZE_SMALL + movie.getPosterPath())
                    .into(image);


        };
    }

}
