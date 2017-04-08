package ift2905.moviebucket;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;
import com.squareup.picasso.Picasso;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;

public class Explore extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListFragment searchFragment, exploreFragment, myBucketFragment, myHistoryFragment;
    private Fragment aboutFragment;
    private String[] myBucket, myHistory;

    final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";
    final String LANG = "en";
    final Boolean ADULT = false; //include adult movies in search results
    final String BASE_URL = "http://image.tmdb.org/t/p/";
    final String SIZE_SMALL = "w154";
    final String SIZE_LARGE = "w500";



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

    }

    //TODO: handle back from search fragments back to discover fragment
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

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) Explore.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(Explore.this.getComponentName()));
            searchView.setIconifiedByDefault(false);
        }
        return super.onCreateOptionsMenu(menu);

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

    /**
     * Method to catch new search query, fetch and display results from API
     */
    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            searchFragment = new ListFragment();
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, searchFragment).commit();

            FetchResults searchFetcher = new FetchResults(query);
            searchFetcher.execute();
        }
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
            if (myBucketFragment == null) {
                myBucketFragment = new ListFragment();
                myBucketFragment.setListAdapter(new SimpleListAdapter(myBucket));
            }
            fragmentTransaction.replace(R.id.fragment_container, myBucketFragment).commit();
            setTitle("My Bucket");

        } else if (id == R.id.nav_myhistory) {
            if (myHistoryFragment == null) {
                myHistoryFragment = new ListFragment();
                myHistoryFragment.setListAdapter(new SimpleListAdapter(myHistory));
            }
            fragmentTransaction.replace(R.id.fragment_container, myHistoryFragment).commit();
            setTitle("My History");

        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(Explore.this, Settings.class);
            startActivity(intent);

        } else if (id == R.id.nav_about) {
            if (aboutFragment == null) {
                aboutFragment = new Fragment();
            }
            fragmentTransaction.replace(R.id.fragment_container, aboutFragment).commit();
            setTitle("About");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO: Save everything to local data base
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    // I'm not sure what this is... are we using it? -Amelie
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Explore Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    // TODO: Expend adapter to include buttons, on click listeners, etc
    // TODO: should take MovieDb instead of Strings
    public class SimpleListAdapter extends BaseAdapter {

        String list[];

        public SimpleListAdapter(String list[]) {
            super();
            this.list = new String[list.length];
            for (int i = 0; i < list.length; i++) {
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

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            tv.setText(list[position]);

            return convertView;
        }
    }

    // Adapter for discover and search
    public class DetailedListAdapter extends BaseAdapter {

        List<MovieDb> movies;

        public DetailedListAdapter(List<MovieDb> movies) {
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
            return movies.get(position).getId();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.detailed_view, parent, false);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView overview = (TextView) convertView.findViewById(R.id.overview);
            ImageView image = (ImageView) convertView.findViewById(R.id.image);

            title.setText(movies.get(position).getTitle());
            overview.setText(movies.get(position).getReleaseDate());

            Picasso.with(getApplicationContext())
                    .load(BASE_URL + SIZE_SMALL + movies.get(position).getBackdropPath())
                    .into(image);


            convertView.setOnClickListener(new View.OnClickListener() {

                // Create a new activity (detailed view of the selected movie)
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MovieView.class);
                    intent.putExtra("movie", getItemId(position));
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }


    /**
     * Fetch suggestions for discover fragment and set to list adapter
     */
    public class FetchSuggestions extends AsyncTask<String, Object, List<MovieDb>> {

        @Override
        protected List<MovieDb> doInBackground(String... params) {


            TmdbApi api = new TmdbApi(API_KEY);
            List<MovieDb> suggestions = api.getMovies().getPopularMovies(LANG, 1).getResults();
            return suggestions;
        }
        @Override
        protected void onPostExecute(List<MovieDb> suggestions) {

            exploreFragment.setListAdapter(new DetailedListAdapter(suggestions));
        }
    }

    // TODO : add search options, get multiple pages... test
    /**
     *  Fetch search results from api and set to list adapter
     */
    public class FetchResults extends AsyncTask<String, Object, List<MovieDb>> {

        String query;

        FetchResults(String query) {
            super();
            this.query = query;
        }
        @Override
        protected List<MovieDb> doInBackground(String... params) {

            //TODO: handle advanced search parameters

            TmdbApi api = new TmdbApi(API_KEY);
            TmdbSearch search = api.getSearch();
            List<MovieDb> results = search.searchMovie(query, null, LANG, ADULT, 1).getResults();

            return results;

        }

        @Override
        protected void onPostExecute(List<MovieDb> results) {

            searchFragment.setListAdapter(new DetailedListAdapter(results));

        }
    }

}


