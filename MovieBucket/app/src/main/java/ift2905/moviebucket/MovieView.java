package ift2905.moviebucket;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbTV;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.ProductionCompany;
import info.movito.themoviedbapi.model.ProductionCountry;
import info.movito.themoviedbapi.model.people.Person;
import info.movito.themoviedbapi.model.people.PersonCast;
import info.movito.themoviedbapi.model.people.PersonCrew;
import info.movito.themoviedbapi.model.tv.Network;
import info.movito.themoviedbapi.model.tv.TvSeries;


public class MovieView extends AppCompatActivity implements View.OnClickListener {

    int id;
    Context context;
    MovieDb movie;
    TvSeries tvSeries;
    String title;
    Button bucketButton;
    Button historyButton;
    ImageButton calendarButton;
    DBHandler dbh;
    int state;
    final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";
    final String LANG = "en";
    final String BASE_URL = "http://image.tmdb.org/t/p/";
    final String SIZE_MEDIUM = "w342";
    final String DEF = "Unknown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            dbh = new DBHandler(this);
            id = (int) getIntent().getExtras().getLong(AbstractResultsAdapter.Type.movie.name());
            if (id > 0){
                setContentView(R.layout.activity_movie_view_m);
                MovieFetcher mf = new MovieFetcher(id);
                mf.execute();
            } else {
                setContentView(R.layout.activity_movie_view_tv);
                id = (int) getIntent().getExtras().getLong(AbstractResultsAdapter.Type.tv.name());
                TvFetcher tf = new TvFetcher(id);
                tf.execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        bucketButton = (Button)findViewById(R.id.buttonAddMb);
        bucketButton.setOnClickListener(this);

        historyButton = (Button)findViewById(R.id.buttonAddH);
        historyButton.setOnClickListener(this);

        calendarButton = (ImageButton)findViewById(R.id.toCalendar);
        calendarButton.setOnClickListener(this);

        state = dbh.inWhichList(id);
        if(state == 1){
            bucketButton.setText("-My Bucket");
        } else if(state == 2) {
            bucketButton.setEnabled(false);
            bucketButton.setText("In My History");
            historyButton.setText("-My History");
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonAddMb:
                if(state == 0) {
                    dbh.addToDB(id, title, 0);
                    state = 1;
                    bucketButton.setText("-My Bucket");

                } else if(state == 1){
                    dbh.removeFromDB(id);
                    bucketButton.setText("+My Bucket");
                    state = 0;

                }
                break;

            case R.id.buttonAddH:
                if(state == 0) {
                    dbh.addToDB(id, title, 1);
                    state = 2;
                    bucketButton.setEnabled(false);
                    bucketButton.setText("In my history");
                    historyButton.setText("-My history");

                } else if(state == 1) {
                    state = 2;
                    dbh.markAsViewed(id);
                    bucketButton.setEnabled(false);
                    bucketButton.setText("In my history");
                    historyButton.setText("-My History");

                } else {
                    state = 0;
                    dbh.removeFromDB(id);
                    bucketButton.setEnabled(true);
                    bucketButton.setText("+My Bucket");
                    historyButton.setText("+My History");

                }
                break;

            case R.id.toCalendar:
                Calendar cal = Calendar.getInstance();
                GregorianCalendar calDate = new GregorianCalendar();
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        calDate.getTimeInMillis());
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                        calDate.getTimeInMillis() +movie.getRuntime()*60*1000);
                intent.putExtra(CalendarContract.Events.TITLE, "Watch " + title);
                startActivity(intent);
                break;
        }
    }

    public class MovieFetcher extends AsyncTask<String, Object, MovieDb> {
        int id;

        public MovieFetcher (int id){
            this.id = id;
        }

        // Get movie
        @Override
        protected MovieDb doInBackground(String... params) {
            TmdbApi api = new TmdbApi(API_KEY);
            try {
                TmdbMovies tmdbm = new TmdbMovies(api);
                movie = tmdbm.getMovie(id, LANG, TmdbMovies.MovieMethod.credits);
                return movie;
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override

        protected void onPostExecute(MovieDb movie) {

            // Title
            TextView titleView = (TextView) findViewById(R.id.movieTitle);
            try {
                title = movie.getTitle();
                setTitle(title);
                titleView.setText(title);
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

            //Cast
            TextView mainCast1 = (TextView) findViewById(R.id.movieActor1);
            TextView mainCast2 = (TextView) findViewById(R.id.movieActor2);
            TextView mainCast3 = (TextView) findViewById(R.id.movieActor3);
            TextView mainChar1 = (TextView) findViewById(R.id.movieChar1);
            TextView mainChar2 = (TextView) findViewById(R.id.movieChar2);
            TextView mainChar3 = (TextView) findViewById(R.id.movieChar3);

            try {
                // TODO: Display rest of the cast
                List<PersonCast> listCast = movie.getCast();
                ListIterator<PersonCast> castListIterator = listCast.listIterator();

                boolean row1 = false;
                boolean row2 = false;
                boolean row3 = false;

                while(castListIterator.hasNext() && !(row1 == true && row2 == true && row3 == true)){
                    PersonCast pc = castListIterator.next();
                    if (row1 == false){
                        mainCast1.setText(pc.getName());
                        String character = pc.getCharacter();
                        if (character.isEmpty()){
                            mainChar1.setText(DEF);
                        } else {
                            mainChar1.setText(pc.getCharacter());
                        }
                        row1 = true;
                    }else if (row2 == false){
                        mainCast2.setText(pc.getName());
                        String character = pc.getCharacter();
                        if (character.isEmpty()){
                            mainChar2.setText(DEF);
                        } else {
                            mainChar2.setText(pc.getCharacter());
                        }
                        row2 = true;
                    }else if (row3 == false){
                        mainCast3.setText(pc.getName());
                        String character = pc.getCharacter();
                        if (character.isEmpty()){
                            mainChar3.setText(DEF);
                        } else {
                            mainChar3.setText(pc.getCharacter());
                        }
                        row3 = true;
                    }
                }

                if (row2 == false) {
                    mainCast2.setVisibility(View.GONE);
                    mainChar2.setVisibility(View.GONE);
                }
                if (row3 == false) {
                    mainCast3.setVisibility(View.GONE);
                    mainChar3.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                mainCast1.setText(DEF);
                mainCast2.setVisibility(View.GONE);
                mainCast3.setVisibility(View.GONE);
                mainChar1.setVisibility(View.GONE);
                mainChar2.setVisibility(View.GONE);
                mainChar3.setVisibility(View.GONE);
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
                ListIterator<ProductionCountry> countryIterator = listCountry.listIterator();
                String country = "";
                while(countryIterator.hasNext()){
                    ProductionCountry pc = countryIterator.next();
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

    public class TvFetcher extends AsyncTask<String, Object, TvSeries> {
        int id;

        public TvFetcher (int id){
            this.id = id;
        }

        // Get TvSeries
        @Override
        protected TvSeries doInBackground(String... params) {
            TmdbApi api = new TmdbApi(API_KEY);
            try {
                TmdbTV tmdbtv = api.getTvSeries();
                tvSeries = tmdbtv.getSeries(id, LANG, TmdbTV.TvMethod.credits);
                return tvSeries;
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(TvSeries tvSeries) {

            // Title
            TextView titleView = (TextView) findViewById(R.id.tvTitle);
            try {
                title = tvSeries.getName();
                setTitle(title);
                titleView.setText(title);
            } catch (Exception e){
                titleView.setText(DEF);
            }


            // Year
            TextView yearView = (TextView) findViewById(R.id.tvRelease);
            try {
                String beg = tvSeries.getFirstAirDate().substring(0,4);
                if (tvSeries.getStatus().contains("Ended")) {
                    String end = tvSeries.getLastAirDate().substring(0,4);
                    yearView.setText(beg + " - " + end);
                } else {
                    yearView.setText("Since " + beg);
                }

            } catch (Exception e) {
                yearView.setText(DEF);
            }


            // Genres
            TextView genresView = (TextView) findViewById(R.id.tvGenres);
            try {
                List<Genre> listGenres = tvSeries.getGenres();
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
            TextView ratingView = (TextView) findViewById(R.id.tvRating);
            try {
                if (tvSeries.getVoteCount() == 0){
                    ratingView.setText("Not rated yet");
                } else {
                    float rating = tvSeries.getVoteAverage();
                    ratingView.setText(rating + "/10");
                }
            } catch (Exception e){
                e.printStackTrace();
                ratingView.setText(DEF);
            }


            // Run time
            TextView runtimeView = (TextView) findViewById(R.id.tvRuntime);
            try {
                List<Integer> listRunTime = tvSeries.getEpisodeRuntime();
                ListIterator<Integer> runTimeListIterator = listRunTime.listIterator();
                String rtListString = "";

                while (runTimeListIterator.hasNext()){
                    int rt = runTimeListIterator.next();
                    String rtToString = Integer.toString(rt) + " min";

                    if(rtListString.isEmpty()) {
                        rtListString = rtToString;
                    } else {
                        rtListString = rtListString +", " + rtToString;
                    }
                }
                if (rtListString.isEmpty()){
                    runtimeView.setText(DEF);
                } else {
                    runtimeView.setText(rtListString);
                }
            } catch (Exception e){
                runtimeView.setText(DEF);
            }


            // Overview
            TextView overviewView = (TextView) findViewById(R.id.tvOverview);
            try{
                String overview = tvSeries.getOverview();
                if (overview.isEmpty()){
                    overviewView.setText(DEF);
                } else {
                    overviewView.setText(overview);
                }
            } catch (Exception e){
                overviewView.setText(DEF);
            }



            // Creator
            TextView creatorView = (TextView) findViewById(R.id.tvCreator);
            TextView creatorTitleView = (TextView) findViewById(R.id.creatorTitle);

            try{
                List<Person> listCreator = tvSeries.getCreatedBy();
                ListIterator<Person> creatorListIterator = listCreator.listIterator();
                String creator = "";
                int count = 0;

                while(creatorListIterator.hasNext()){
                    Person person = creatorListIterator.next();
                    count++;

                    if (creator.isEmpty()){
                     creator = person.getName();
                    } else {
                        creator = creator + ", " + person.getName();
                    }
                }

                creatorView.setText(creator);
                if (count>1){
                    creatorTitleView.setText("Creators");
                }

            } catch (Exception e){
                e.printStackTrace();
                creatorView.setText(DEF);

            }

            //Cast
            TextView mainCast1 = (TextView) findViewById(R.id.movieActor1);
            TextView mainCast2 = (TextView) findViewById(R.id.movieActor2);
            TextView mainCast3 = (TextView) findViewById(R.id.movieActor3);
            TextView mainChar1 = (TextView) findViewById(R.id.movieChar1);
            TextView mainChar2 = (TextView) findViewById(R.id.movieChar2);
            TextView mainChar3 = (TextView) findViewById(R.id.movieChar3);

            try {
                // TODO: Display rest of the cast
                List<PersonCast> listCast = tvSeries.getCredits().getCast();
                ListIterator<PersonCast> castListIterator = listCast.listIterator();

                boolean row1 = false;
                boolean row2 = false;
                boolean row3 = false;

                while(castListIterator.hasNext() && !(row1 == true && row2 == true && row3 == true)){
                    PersonCast pc = castListIterator.next();
                    if (row1 == false){
                        mainCast1.setText(pc.getName());
                        String character = pc.getCharacter();
                        if (character.isEmpty()){
                            mainChar1.setText(DEF);
                        } else {
                            mainChar1.setText(pc.getCharacter());
                        }
                        row1 = true;
                    }else if (row2 == false){
                        mainCast2.setText(pc.getName());
                        String character = pc.getCharacter();
                        if (character.isEmpty()){
                            mainChar2.setText(DEF);
                        } else {
                            mainChar2.setText(pc.getCharacter());
                        }
                        row2 = true;
                    }else if (row3 == false){
                        mainCast3.setText(pc.getName());
                        String character = pc.getCharacter();
                        if (character.isEmpty()){
                            mainChar3.setText(DEF);
                        } else {
                            mainChar3.setText(pc.getCharacter());
                        }
                        row3 = true;
                    }
                }

                if (row2 == false) {
                    mainCast2.setVisibility(View.GONE);
                    mainChar2.setVisibility(View.GONE);
                }
                if (row3 == false) {
                    mainCast3.setVisibility(View.GONE);
                    mainChar3.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                mainCast1.setText(DEF);
                mainCast2.setVisibility(View.GONE);
                mainCast3.setVisibility(View.GONE);
                mainChar1.setVisibility(View.GONE);
                mainChar2.setVisibility(View.GONE);
                mainChar3.setVisibility(View.GONE);
            }

            // Network
            TextView networkView = (TextView) findViewById(R.id.tvNetwork);
            try {
                List<Network> listNetwork = tvSeries.getNetworks();
                ListIterator<Network> networkListIterator = listNetwork.listIterator();
                String nwString = "";
                while(networkListIterator.hasNext()){
                    Network network = networkListIterator.next();
                    if (nwString.isEmpty()){
                        nwString = network.getName();
                    } else {
                        nwString = nwString + ", " + network.getName();
                    }
                }
                if (nwString.isEmpty()){
                    networkView.setText(DEF);
                } else {
                    networkView.setText(nwString);
                }
            } catch (Exception e){
                networkView.setText(DEF);
            }


            // Country
            TextView countryView = (TextView) findViewById(R.id.tvCountry);
            TextView countryTitleView = (TextView) findViewById(R.id.countryTitle);
            try {
                List<String> listCountry = tvSeries.getOriginCountry();
                ListIterator<String> countryIterator = listCountry.listIterator();
                String country = "";
                while(countryIterator.hasNext()){
                    String element = countryIterator.next();
                    if (country.isEmpty()){
                        country = element;
                    } else {
                        country = country + ", " + element;
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
            ImageView image = (ImageView) findViewById(R.id.tvPoster);
            try {
                Picasso.with(getApplicationContext())
                        .load(BASE_URL + SIZE_MEDIUM + tvSeries.getPosterPath())
                        .into(image);
            } catch (Exception e){
                // TODO : Find a default image
            }


        };
    }

}
