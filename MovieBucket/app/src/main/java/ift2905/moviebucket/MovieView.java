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
import info.movito.themoviedbapi.model.ProductionCompany;
import info.movito.themoviedbapi.model.ProductionCountry;
import info.movito.themoviedbapi.model.people.PersonCrew;


public class MovieView extends AppCompatActivity {

    int id;
    final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";
    final String LANG = "en";
    final String BASE_URL = "http://image.tmdb.org/t/p/";
    final String SIZE_MEDIUM = "w342";
    final String DEF = "Unknown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_view);

        id = (int) getIntent().getExtras().getLong("movie");
        MovieFetcher mf = new MovieFetcher(id);
        mf.execute();

        // TODO: Display cast, suggestions
        // TODO: Add to history and schedule buttons
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
            MovieDb movie = tmdbm.getMovie(idMovie, LANG, TmdbMovies.MovieMethod.credits);
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
                titleView.setText(DEF);
            }


            // Year
            TextView yearView = (TextView) findViewById(R.id.movieYear);
            try {
                yearView.setText(movie.getReleaseDate().substring(0,4));
            } catch (Exception e) {
                yearView.setText(DEF);
            }


            // Genres
            TextView genresView = (TextView) findViewById(R.id.movieGenres);
            try {
                List<Genre> listGenres = movie.getGenres();
                ListIterator<Genre> genreListIterator = listGenres.listIterator();
                String genres = "";
                while(genreListIterator.hasNext()){
                    Genre g = genreListIterator.next();
                    genres = genres+g.getName()+"\n";
                }
                if (genres.isEmpty()){
                    genresView.setText(DEF);
                } else {
                    genresView.setText(genres);
                }
            } catch (Exception e){
                genresView.setText(DEF);
            }


            //Rating
            TextView ratingView = (TextView) findViewById(R.id.movieRating);
            try {
                if (movie.getVoteCount() == 0){
                    ratingView.setText("Not rated yet");
                } else {
                    float rating = movie.getVoteAverage();
                    ratingView.setText(rating + "/10");
                }
            } catch (Exception e){
                e.printStackTrace();
                ratingView.setText(DEF);
            }


            // Run time
            TextView runtimeView = (TextView) findViewById(R.id.movieRuntime);
            try {
                long runTime = movie.getRuntime();
                if (runTime == 0){
                    runtimeView.setText(DEF);
                } else {
                    long hours = runTime/60;
                    long minutes = runTime-(hours*60);
                    String minutesT = Long.toString(minutes);
                    if (minutes<10){
                        minutesT = "0"+minutesT;
                    }
                    runtimeView.setText(Long.toString(hours)+"h"+minutesT);
                }
            } catch (Exception e){
                runtimeView.setText(DEF);
            }


            // Overview
            TextView overviewView = (TextView) findViewById(R.id.movieOverview);
            try{
                String overview = movie.getOverview();
                if (overview.isEmpty()){
                    overviewView.setText(DEF);
                } else {
                    overviewView.setText(overview);
                }
            } catch (Exception e){
                overviewView.setText(DEF);
            }

            // Director and writer
            TextView directorView = (TextView) findViewById(R.id.movieDirector);
            TextView directorTitleView = (TextView) findViewById(R.id.directorTitle);
            TextView writerView = (TextView) findViewById(R.id.movieWriter);
            TextView writerTitleView = (TextView) findViewById(R.id.writerTitle);

            try{
                List<PersonCrew> listCrew = movie.getCrew();
                ListIterator<PersonCrew> crewListIterator = listCrew.listIterator();
                String director = "";
                String writer = "";
                String novel = "";

                while(crewListIterator.hasNext()){
                    PersonCrew pc = crewListIterator.next();
                    String job = pc.getJob();

                    //Director
                    if (job.equals("Director")){
                        if (director.isEmpty()){
                            director = pc.getName();
                        } else {
                            director = director + ", " + pc.getName();
                            directorTitleView.setText("Directors");
                        }
                    }

                    // Writer
                    if (job.equals("Writer")|| job.equals("Screenplay") || job.equals("Story")){
                        String name = pc.getName();
                        if (writer.isEmpty()){
                            writer = name;
                        } else {
                            if (!writer.contains(name)){
                                writer = writer + ", " + pc.getName();
                                writerTitleView.setText("Writers");
                            }
                        }
                    }
                    if (job.equals("Novel")){
                        novel = " (based on novel by "+pc.getName()+")";
                    }
                }

                writer = writer + novel;
                if (director.isEmpty()){
                    directorView.setText(DEF);
                } else {
                    directorView.setText(director);
                }

                if (writer.isEmpty()){
                    writerView.setText(DEF);
                } else {
                    writerView.setText(writer);
                }

            } catch (Exception e){
                e.printStackTrace();
                directorView.setText(DEF);
                writerView.setText(DEF);

            }


            // Production
            TextView prodView = (TextView) findViewById(R.id.movieProduction);
            try {
                List<ProductionCompany> listCompany = movie.getProductionCompanies();
                ListIterator<ProductionCompany> prodCompanyListIterator = listCompany.listIterator();
                String prod = "";
                while(prodCompanyListIterator.hasNext()){
                    ProductionCompany pc = prodCompanyListIterator.next();
                    if (prod.isEmpty()){
                        prod = pc.getName();
                    } else {
                        prod = prod + ", " + pc.getName();
                    }
                }
                if (prod.isEmpty()){
                    prodView.setText(DEF);
                } else {
                    prodView.setText(prod);
                }
            } catch (Exception e){
                prodView.setText(DEF);
            }

            // Country
            TextView countryView = (TextView) findViewById(R.id.movieCountry);
            TextView countryTitleView = (TextView) findViewById(R.id.countryTitle);
            try {
                List<ProductionCountry> listCountry = movie.getProductionCountries();
                ListIterator<ProductionCountry> genreCountryIterator = listCountry.listIterator();
                String country = "";
                while(genreCountryIterator.hasNext()){
                    ProductionCountry pc = genreCountryIterator.next();
                    if (country.isEmpty()){
                        country = pc.getName();
                    } else {
                        country = country + ", " + pc.getName();
                        countryTitleView.setText("Countries");
                    }
                }
                if (country.isEmpty()){
                    countryView.setText(DEF);
                } else {
                    countryView.setText(country);
                }
            } catch (Exception e){
                countryView.setText(DEF);
            }


            // Poster
            ImageView image = (ImageView) findViewById(R.id.moviePoster);
            try {
                Picasso.with(getApplicationContext())
                        .load(BASE_URL + SIZE_MEDIUM + movie.getPosterPath())
                        .into(image);
            } catch (Exception e){
                // TODO : Find a default image
            }


        };
    }

}
