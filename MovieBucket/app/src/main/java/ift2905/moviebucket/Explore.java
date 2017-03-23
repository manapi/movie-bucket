package ift2905.moviebucket;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbDiscover;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class Explore extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListFragment exploreFragment, myBucketFragment, myHistoryFragment;
    private Fragment aboutFragment;
    private String[] suggestions, myBucket, myHistory;

    final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";
    final String LANG = "en";
    final Boolean ADULT = false; //include adult movies in search results


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Displaying suggestions on startup : currently, popular movies (other options possible, what do we want?)
        FetchSuggestions fetcher = new FetchSuggestions();
        fetcher.execute();

        //Initialize explore fragment by default
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        exploreFragment = new ListFragment();
        fragmentTransaction.add(R.id.fragment_container, exploreFragment).commit();
        setTitle("Explore");

        // TODO: Read everything from local data base
        //To be replaced with actual data
        myBucket = new String[1];
        myBucket[0] = "my bucket";

        myHistory = new String[1];
        myHistory[0] = "my history";

        // TODO: Read text input from user, upon click search and display results
        // TODO: On click on list item, create MovieView activity from item
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.explore, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        // TODO: replace with advanced search button
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        //TODO: Show/hide app bar search field when switching views
        if (id == R.id.nav_explore) {
            fragmentTransaction.replace(R.id.fragment_container, exploreFragment).commit();
            setTitle("Explore");

        } else if (id == R.id.nav_mybucket) {
            if(myBucketFragment == null) {
                myBucketFragment = new ListFragment();
                myBucketFragment.setListAdapter(new SimpleListAdapter(myBucket));
            }
            fragmentTransaction.replace(R.id.fragment_container, myBucketFragment).commit();
            setTitle("My Bucket");

        } else if (id == R.id.nav_myhistory) {
            if(myHistoryFragment == null) {
                myHistoryFragment = new ListFragment();
                myHistoryFragment.setListAdapter(new SimpleListAdapter(myHistory));
            }
            fragmentTransaction.replace(R.id.fragment_container, myHistoryFragment).commit();
            setTitle("My History");

        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(Explore.this, Settings.class);
            startActivity(intent);

        } else if (id == R.id.nav_about) {
            if(aboutFragment == null) {
                aboutFragment = new Fragment();
            }
            fragmentTransaction.replace(R.id.fragment_container, aboutFragment).commit();
            setTitle("About");
        }

        /*Bundle args = new Bundle();
        args.putInt("INDEX",0);
        aboutFragment.setArguments(args);*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        // TODO: Save everything to local data base
    }

    // TODO: Expend adapter to include buttons, on click listeners, etc
    // TODO: should take MovieDb instead of Strings
    public class SimpleListAdapter extends BaseAdapter {

        String list[];

        public SimpleListAdapter(String list[]){
            super();
            this.list = new String[list.length];
            for(int i=0; i< list.length; i++){
                this.list[i] = list[i];
            }
        }

        @Override
        public int getCount() {
            return list.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null) {
                convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            tv.setText(list[position]);

            return convertView;
        }
    }

    // Adapter for suggestions and search
    // TODO : display images, on click listener...
    public class DetailedListAdapter extends BaseAdapter {

        List<MovieDb> movies;

        public DetailedListAdapter(List<MovieDb> movies){
            super();
            this.movies = movies;
        }

        @Override
        public int getCount() {
            return movies.size();
        }

        @Override
        public Object getItem(int position) {
            return movies.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.detailed_view, parent, false);

            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView overview = (TextView) convertView.findViewById(R.id.overview);
            ImageView image = (ImageView) convertView.findViewById(R.id.image);

            title.setText(movies.get(position).getTitle());
            overview.setText(movies.get(position).getReleaseDate().subSequence(0,4));

            //TODO : no image from API???
            List<Artwork> art = movies.get(position).getImages();
            if (art != null) {
                Picasso.with(getApplicationContext())
                        .load(art.get(0).getFilePath())
                        .into(image);
            }
            return convertView;
        }
    }

    public class FetchSuggestions extends AsyncTask<String, Object, List<MovieDb>> {

        @Override
        protected List<MovieDb> doInBackground(String... params) {


            TmdbApi api = new TmdbApi(API_KEY);

            List<MovieDb> suggestions = api.getMovies().getPopularMovies(LANG, 1).getResults();

            return suggestions;
        }

        @Override
        protected void onPostExecute(List<MovieDb> suggestions) {

            exploreFragment.setListAdapter(new Explore.DetailedListAdapter(suggestions));
        }
    }

    // TODO : add search options, get multiple pages... test
    public class FetchSearchResults extends AsyncTask<String, Object, List<MovieDb>> {

        @Override
        protected List<MovieDb> doInBackground(String... params) {

            //how to get query... from params?
            String query;

            if(params[0] != null) {
                query = params[0];

                if (params.length > 1) {
                    //retrieve advanced options
                }

                TmdbApi api = new TmdbApi(API_KEY);

                TmdbSearch search = api.getSearch();
                List<MovieDb> results = search.searchMovie(query, null, LANG, ADULT, 1).getResults();

                return results;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<MovieDb> results) {

            //set adapter?
        }
    }

}


