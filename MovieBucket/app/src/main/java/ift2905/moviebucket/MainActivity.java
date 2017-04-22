package ift2905.moviebucket;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerViewFragment discoverFragment;
    private ListFragment searchFragment, myBucketFragment, myHistoryFragment;
    private Fragment aboutFragment;
    private SearchView searchView;
    private DBHandler dbh;
    final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbh = new DBHandler(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Displaying suggestions on startup : currently, popular movies
        discoverFragment = new RecyclerViewFragment();

        //Initialize discover fragment by default
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, discoverFragment).commit();
        setTitle(R.string.title_fragment_discover);
    }

    //TODO: handle back from search fragments back to *any* fragment?
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
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
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
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
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

            FetchResults searchFetcher = new FetchResults(query, searchFragment, MainActivity.this, API_KEY);
            searchFetcher.execute();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        if (id == R.id.nav_discover) {
            fragmentTransaction.replace(R.id.fragment_container, discoverFragment).commit();
            setTitle(R.string.title_fragment_discover);

        } else if (id == R.id.nav_mybucket) {
            //TODO: Update the list on every press.
            if (myBucketFragment == null) {
                myBucketFragment = new ListFragment();
                Cursor cursor = dbh.movieLister("Bucket");
                myBucketFragment.setListAdapter(new MyListAdapter("Bucket", MainActivity.this, cursor));
            }
            fragmentTransaction.replace(R.id.fragment_container, myBucketFragment).commit();
            setTitle(R.string.title_fragment_my_bucket);

        } else if (id == R.id.nav_myhistory) {
            //TODO: Update the list on every press.
            if (myHistoryFragment == null) {
                myHistoryFragment = new ListFragment();
                Cursor cursor = dbh.movieLister("History");
                myHistoryFragment.setListAdapter(new MyListAdapter("History", MainActivity.this, cursor));
            }
            fragmentTransaction.replace(R.id.fragment_container, myHistoryFragment).commit();
            setTitle(R.string.title_fragment_my_history);

        } else if (id == R.id.nav_about) {
            if (aboutFragment == null) {
                aboutFragment = new Fragment();
            }
            fragmentTransaction.replace(R.id.fragment_container, aboutFragment).commit();
            setTitle(R.string.title_fragment_about);
        }

        //Expand / collapse search when switching fragments
        if(searchView != null && id == R.id.nav_discover) {
            searchView.setIconifiedByDefault(false);
        } else {
            searchView.setIconifiedByDefault(true);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbh.getDb().close();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
