package ift2905.moviebucket;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
    Button calendarButton;
    String lang;
    int runtimeDB;
    DBHandler dbh;
    int state;
    final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";


    final String BASE_URL = "http://image.tmdb.org/t/p/";
    final String SIZE_MEDIUM = "w500";
    String DEF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            //Sets up the information language.
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            lang = prefs.getString(SettingsFragment.KEY_LOCALE, "en");
            DEF = getString(R.string.default_info);

            dbh = new DBHandler(this);
            id = (int) getIntent().getExtras().getLong(AbstractResultsAdapter.Type.movie.name());
            setContentView(R.layout.activity_movie_view_m);
            if (id > 0){
                MovieFetcher mf = new MovieFetcher();
                mf.execute(Integer.valueOf(id).toString());
            } else {
                id = (int) getIntent().getExtras().getLong(AbstractResultsAdapter.Type.tv.name());
                TvFetcher tf = new TvFetcher();
                tf.execute(Integer.valueOf(id).toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        bucketButton = (Button)findViewById(R.id.buttonAddMb);
        bucketButton.setOnClickListener(this);

        historyButton = (Button)findViewById(R.id.buttonAddH);
        historyButton.setOnClickListener(this);

        calendarButton = (Button)findViewById(R.id.toCalendar);
        calendarButton.setOnClickListener(this);

        state = dbh.inWhichList(id);
        if(state == 1){
            bucketButton.setSelected(true);

        } else if(state == 2) {
            bucketButton.setEnabled(false);
            historyButton.setSelected(true);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonAddMb:
                if(state == 0) {
                    dbh.addToDB(id, title, 0, runtimeDB);
                    state = 1;
                    bucketButton.setSelected(true);
                } else if(state == 1){
                    dbh.removeFromDB(id);
                    state = 0;
                    bucketButton.setSelected(false);
                }
                break;

            case R.id.buttonAddH:
                if(state == 0) {
                    dbh.addToDB(id, title, 1, runtimeDB);
                    state = 2;
                    bucketButton.setEnabled(false);
                    historyButton.setSelected(true);

                } else if(state == 1) {
                    state = 2;
                    dbh.markAsViewed(id);
                    bucketButton.setEnabled(false);
                    historyButton.setSelected(true);

                } else {
                    state = 0;
                    dbh.removeFromDB(id);
                    bucketButton.setEnabled(true);
                    bucketButton.setSelected(false);
                    historyButton.setSelected(false);

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
                        calDate.getTimeInMillis() +runtimeDB*60*1000);
                intent.putExtra(CalendarContract.Events.TITLE, getString(R.string.watch)+" "+title);
                startActivity(intent);
                break;
        }
    }

    public class MovieFetcher extends AsyncTask<String, Object, MovieDb> {

        // Get movie
        @Override
        protected MovieDb doInBackground(String... params) {
            if (params.length > 0) {
                int id = new Integer(params[0]);

                try {
                    TmdbApi api = new TmdbApi(API_KEY);
                    TmdbMovies tmdbm = new TmdbMovies(api);
                    movie = tmdbm.getMovie(id, lang, TmdbMovies.MovieMethod.credits);
                    return movie;
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                        if (genres.isEmpty()) {
                            genres = g.getName();
                        }else {
                            genres = genres+", " +g.getName();
                        }

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
                        ratingView.setText(getString(R.string.unrated));
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
                    runtimeDB = (int)runTime;
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
                                directorTitleView.setText(getString(R.string.directors));
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
                                    writerTitleView.setText(getString(R.string.writers));
                                }
                            }
                        }
                        if (job.equals("Novel")){
                            novel = " " + getString(R.string.novel) + " " +pc.getName()+ ")";
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
                    List<PersonCast> listCast = movie.getCast();
                    ListIterator<PersonCast> castListIterator = listCast.listIterator();

                    boolean row1 = false;
                    boolean row2 = false;
                    boolean row3 = false;

                    while(castListIterator.hasNext() && !(row1 == true && row2 == true && row3 == true )){
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

                    if (row1 == false){
                        mainCast1.setText(DEF);
                        mainCast2.setVisibility(View.GONE);
                        mainCast3.setVisibility(View.GONE);
                        mainChar1.setVisibility(View.GONE);
                        mainChar2.setVisibility(View.GONE);
                        mainChar3.setVisibility(View.GONE);
                    } else if (row2 == false){
                        mainCast2.setVisibility(View.GONE);
                        mainCast3.setVisibility(View.GONE);
                        mainChar2.setVisibility(View.GONE);
                        mainChar3.setVisibility(View.GONE);
                    } else if (row3 == false) {
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
                            countryTitleView.setText(getString(R.string.countries));
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
                            .error(R.drawable.placeholder)
                            .placeholder(R.drawable.placeholder)
                            .into(image);
                } catch (Exception e){
                }

        };
    }

    public class TvFetcher extends AsyncTask<String, Object, TvSeries> {

        // Get TvSeries
        @Override
        protected TvSeries doInBackground(String... params) {
            if (params.length > 0) {
                int id = new Integer(params[0]);

                try {
                    TmdbApi api = new TmdbApi(API_KEY);
                    TmdbTV tmdbtv = api.getTvSeries();
                    tvSeries = tmdbtv.getSeries(id, lang, TmdbTV.TvMethod.credits);
                    return tvSeries;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(TvSeries tvSeries) {

            // Title
            TextView titleView = (TextView) findViewById(R.id.movieTitle);
            try {
                title = tvSeries.getName();
                setTitle(title);
                titleView.setText(title);
            } catch (Exception e){
                titleView.setText(DEF);
            }


            // Year
            TextView releaseView = (TextView) findViewById(R.id.movieYear);
            TextView releaseTitleView = (TextView) findViewById(R.id.yearTitle);
            releaseTitleView.setText(getString(R.string.release_year));
            try {
                String beg = tvSeries.getFirstAirDate().substring(0,4);
                if (tvSeries.getStatus().contains(getString(R.string.ended))) {
                    String end = tvSeries.getLastAirDate().substring(0,4);
                    releaseView.setText(beg + " - " + end);
                } else {
                    releaseView.setText( getString(R.string.since)+ " " + beg);
                }

            } catch (Exception e) {
                releaseView.setText(DEF);
            }


            // Genres
            TextView genresView = (TextView) findViewById(R.id.movieGenres);
            try {
                List<Genre> listGenres = tvSeries.getGenres();
                ListIterator<Genre> genreListIterator = listGenres.listIterator();
                String genres = "";
                while(genreListIterator.hasNext()){
                    Genre g = genreListIterator.next();
                    if (genres.isEmpty()) {
                        genres = g.getName();
                    }else {
                        genres = genres+", " +g.getName();
                    }

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
                if (tvSeries.getVoteCount() == 0){
                    ratingView.setText(getString(R.string.unrated));
                } else {
                    float rating = tvSeries.getVoteAverage();
                    ratingView.setText(rating + "/10");
                }
            } catch (Exception e){
                e.printStackTrace();
                ratingView.setText(DEF);
            }


            // Run time
            TextView runtimeView = (TextView) findViewById(R.id.movieRuntime);
            TextView runtimeTitleView = (TextView) findViewById(R.id.runtimeTitle);
            runtimeTitleView.setText(getString(R.string.episode_runtime));
            try {
                List<Integer> listRunTime = tvSeries.getEpisodeRuntime();
                ListIterator<Integer> runTimeListIterator = listRunTime.listIterator();
                String rtListString = "";

                while (runTimeListIterator.hasNext()){
                    int rt = runTimeListIterator.next();
                    String rtToString = Integer.toString(rt) + " min";

                    if(rtListString.isEmpty()) {
                        rtListString = rtToString;
                        runtimeDB = rt;
                    } else {
                        rtListString = rtListString +", " + rtToString;
                    }
                }
                if (rtListString.isEmpty()){
                    runtimeView.setText(DEF);
                    runtimeDB = 0;
                } else {
                    runtimeView.setText(rtListString);
                }
            } catch (Exception e){
                runtimeView.setText(DEF);
            }


            // Overview
            TextView overviewView = (TextView) findViewById(R.id.movieOverview);
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
            TextView creatorView = (TextView) findViewById(R.id.movieDirector);
            TextView creatorTitleView = (TextView) findViewById(R.id.directorTitle);
            creatorTitleView.setText(getString(R.string.creator));

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

                if (creator.isEmpty()){
                    creatorView.setText(DEF);
                } else {
                    creatorView.setText(creator);
                    if (count>1){
                        creatorTitleView.setText(getString(R.string.creators));
                    }
                }



            } catch (Exception e){
                e.printStackTrace();
                creatorView.setText(DEF);

            }

            // Cast
            TextView mainCast1 = (TextView) findViewById(R.id.movieActor1);
            TextView mainCast2 = (TextView) findViewById(R.id.movieActor2);
            TextView mainCast3 = (TextView) findViewById(R.id.movieActor3);
            TextView mainChar1 = (TextView) findViewById(R.id.movieChar1);
            TextView mainChar2 = (TextView) findViewById(R.id.movieChar2);
            TextView mainChar3 = (TextView) findViewById(R.id.movieChar3);

            try {
                List<PersonCast> listCast = movie.getCast();
                ListIterator<PersonCast> castListIterator = listCast.listIterator();

                boolean row1 = false;
                boolean row2 = false;
                boolean row3 = false;

                while(castListIterator.hasNext() && !(row1 == true && row2 == true && row3 == true )){
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

                if (row1 == false){
                    mainCast1.setText(DEF);
                    mainCast2.setVisibility(View.GONE);
                    mainCast3.setVisibility(View.GONE);
                    mainChar1.setVisibility(View.GONE);
                    mainChar2.setVisibility(View.GONE);
                    mainChar3.setVisibility(View.GONE);
                } else if (row2 == false){
                    mainCast2.setVisibility(View.GONE);
                    mainCast3.setVisibility(View.GONE);
                    mainChar2.setVisibility(View.GONE);
                    mainChar3.setVisibility(View.GONE);
                } else if (row3 == false) {
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

            //Episodes
            TextView episodesView = (TextView) findViewById(R.id.movieWriter);
            TextView episodeTitleView = (TextView)findViewById(R.id.writerTitle);
            episodeTitleView.setText(getString(R.string.number_of_episodes));
            try{
                int nbEp = tvSeries.getNumberOfEpisodes();

                if (nbEp == 0){
                    episodesView.setText(DEF);
                } else {
                    int nbSeas = tvSeries.getNumberOfSeasons();
                    String ep = " " + getString(R.string.episodes);
                    String seas = " " + getString(R.string.seasons);
                    if (nbEp <2){
                        ep = " " + getString(R.string.episode);
                    }
                    if (nbSeas<2){
                        seas = " " + getString(R.string.season);
                    }

                    episodesView.setText(Integer.toString(nbEp) + ep + Integer.toString(nbSeas) + seas);
                }


            } catch (Exception e) {
                episodesView.setText(DEF);
            }



            // Network
            TextView networkView = (TextView) findViewById(R.id.movieProduction);
            TextView networkTitleView = (TextView) findViewById(R.id.productionTitle);
            networkTitleView.setText(getString(R.string.network));
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
            TextView countryView = (TextView) findViewById(R.id.movieCountry);
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
                        countryTitleView.setText(getString(R.string.countries));
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
                        .load(BASE_URL + SIZE_MEDIUM + tvSeries.getPosterPath())
                        .error(R.drawable.placeholder)
                        .placeholder(R.drawable.placeholder)
                        .into(image);
            } catch (Exception e){
                // TODO : Find a default image
            }
        };
    }
}
