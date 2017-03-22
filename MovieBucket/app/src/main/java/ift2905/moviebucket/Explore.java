package ift2905.moviebucket;

import android.app.Fragment;
import android.app.FragmentManager;
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
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;

public class Explore extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListFragment exploreFragment, myBucketFragment, myHistoryFragment;
    private Fragment aboutFragment;
    private String[] suggestions, myBucket, myHistory;


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

        // TODO: Display suggestions on startup
        //Testing api to display content **to be replaced with fetching suggestions or reading them from db**
        FetchMovie fetcher = new FetchMovie();
        fetcher.execute();

        //Initialize explore fragment by default
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        exploreFragment = new ListFragment();
        fragmentTransaction.add(R.id.fragment_container, exploreFragment);
        fragmentTransaction.commit();

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

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //TODO: Update app bar title when switching views
        if (id == R.id.nav_explore) {
            fragmentTransaction.replace(R.id.fragment_container, exploreFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_mybucket) {
            if(myBucketFragment == null) {
                myBucketFragment = new ListFragment();
                myBucketFragment.setListAdapter(new SimpleListAdapter(myBucket));
            }
            fragmentTransaction.replace(R.id.fragment_container, myBucketFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_myhistory) {
            if(myHistoryFragment == null) {
                myHistoryFragment = new ListFragment();
                myHistoryFragment.setListAdapter(new SimpleListAdapter(myHistory));
            }
            fragmentTransaction.replace(R.id.fragment_container, myHistoryFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(Explore.this, Settings.class);
            startActivity(intent);

        } else if (id == R.id.nav_about) {
            if(aboutFragment == null) {
                aboutFragment = new Fragment();
            }
            fragmentTransaction.replace(R.id.fragment_container, aboutFragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        // TODO: Save everything to local data base
    }

    // TODO: Replace with custom defined adapter
    // Suggestions and search results should use the same one, and my history and my bucket a similar simpler one
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
            tv.setText(list[position % 5]);

            return convertView;
        }
    }

    // TODO : replace with proper suggestions and/or search calls to API
    public class FetchMovie extends AsyncTask<String, Object, MovieDb[]> {

        @Override
        protected MovieDb[] doInBackground(String... params) {

            // Fetcher un film
            MovieDb[] movies = new MovieDb[5];

            final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";
            TmdbApi api = new TmdbApi(API_KEY);

            try {
                TmdbMovies dbmovies = api.getMovies();
                for(int i=0; i<5; i++)
                    movies[i] = dbmovies.getMovie(5353, "en");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return movies;
        }

        @Override
        protected void onPostExecute(MovieDb[] movies) {
            suggestions = new String[5];

            for(int i=0; i<5; i++)
                suggestions[i] = movies[i].getTitle();

            exploreFragment.setListAdapter(new Explore.SimpleListAdapter(suggestions));
        }
    }
}


