package ift2905.moviebucket;


import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.ListIterator;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;



public class MovieView extends AppCompatActivity {

    int id;
    final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";
    final String LANG = "en";
    final String BASE_URL = "http://image.tmdb.org/t/p/";
    final String SIZE_MEDIUM = "w342";

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

            // Title
            TextView titleView = (TextView) findViewById(R.id.movieTitle);
            try {
                setTitle(movie.getTitle());
                titleView.setText(movie.getTitle());
            } catch (Exception e){
                titleView.setText("Unknown");
            }


            // Year
            TextView yearView = (TextView) findViewById(R.id.movieYear);
            try {
                yearView.setText(movie.getReleaseDate().substring(0,4));
            } catch (Exception e) {
                yearView.setText("Unknown");
            }


            // Genres
            TextView genresView = (TextView) findViewById(R.id.movieGenres);
            try {
                List<Genre> listGenres = movie.getGenres();
                ListIterator genreIterator = listGenres.listIterator();
                String genres = "";
                while(genreIterator.hasNext()){
                    Genre g = (Genre)genreIterator.next();
                    genres = genres+g.getName()+"\n";
                }
                if (genres.isEmpty()){
                    genresView.setText("Unknown");
                } else {
                    genresView.setText(genres);
                }
            } catch (Exception e){
                genresView.setText("Unknown");
            }


            //Rating
            TextView ratingView = (TextView) findViewById(R.id.movieRating);
            try {
                float rating = movie.getVoteAverage();
                ratingView.setText(Float.toString(rating)+"/10");
            } catch (Exception e){
                ratingView.setText("Unknown");
            }


            // Run time
            TextView runtimeView = (TextView) findViewById(R.id.movieRuntime);
            try {
                long runTime = movie.getRuntime();
                long hours = runTime/60;
                long minutes = runTime-(hours*60);
                String minutesT = Long.toString(minutes);
                if (minutes<10){
                    minutesT = "0"+minutesT;
                }
                runtimeView.setText(Long.toString(hours)+"h"+minutesT);
            } catch (Exception e){
                runtimeView.setText("Unknown");
            }


            // Overview
            TextView overviewView = (TextView) findViewById(R.id.movieOverview);
            try{
                overviewView.setText(movie.getOverview());

            } catch (Exception e){
                overviewView.setText("Unknown");
            }

            // Poster
            ImageView image = (ImageView) findViewById(R.id.moviePoster);
            try {
                Picasso.with(getApplicationContext())
                        .load(BASE_URL + SIZE_MEDIUM + movie.getPosterPath())
                        .into(image);
            } catch (Exception e){
                // TODO: find default image
            }


        };
    }

}
