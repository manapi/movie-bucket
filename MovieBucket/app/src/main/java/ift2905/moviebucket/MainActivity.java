package ift2905.moviebucket;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Startup activity with navigation drawer and search
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OfflineFragment.OnFragmentInteractionListener {

    // Fragments
    private RecyclerViewFragment discoverFragment;
    private SpecialListFragment myBucketFragment, myHistoryFragment;
    private AbootFragment aboutFragment;

    private SearchView searchView;
    private DBHandler dbh;

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

        // Initialize discover fragment
        if (checkConnectivity()) {
            discoverFragment = new RecyclerViewFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, discoverFragment).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new OfflineFragment()).commit();
        }
        setTitle(R.string.title_fragment_discover);

        //Sets the used Settings.
        //PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            // Case 1 : close drawer
            drawer.closeDrawer(GravityCompat.START);
        } else if(!navigationView.getMenu().findItem(R.id.nav_discover).isChecked()) {
            // Case 2 : go back to discover fragment
            if(checkConnectivity()) {
                if(discoverFragment == null) {
                    discoverFragment = new RecyclerViewFragment();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, discoverFragment).commit();
            }
            else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OfflineFragment()).commit();
            }
            navigationView.getMenu().findItem(R.id.nav_discover).setChecked(true);
            setTitle(R.string.title_fragment_discover);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        // Set up search widget
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
            searchView.setIconifiedByDefault(true);

            // Handle search fragment navigation
            searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus == true){
                        SearchPagerFragment searchPagerFragment = new SearchPagerFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, searchPagerFragment).addToBackStack(null).commit();
                    }
                    else if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        // Simulate back
                        getSupportFragmentManager().popBackStack();
                        searchView.setIconified(true);
                    }
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks for settings
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Catch new search query and send it to search fragment
     */
    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            // Send query to search fragment
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if(currentFragment instanceof SearchPagerFragment) {
                ((SearchPagerFragment)currentFragment).search(query);
            }
        }
    }

    /**
     * Handle drawer navigation
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        // Collapse search widget
        if(searchView != null) {
            searchView.setIconified(true);
            searchView.setIconified(true);
        }

        // Drawer navigation
        if (id == R.id.nav_discover) {
            if(checkConnectivity()) {
                if (discoverFragment == null) {
                    discoverFragment = new RecyclerViewFragment();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, discoverFragment).commit();
            }
            else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OfflineFragment()).commit();
            }

            setTitle(R.string.title_fragment_discover);

        } else if (id == R.id.nav_mybucket) {
            if (myBucketFragment == null) {
                myBucketFragment = new SpecialListFragment();
                Cursor cursor = dbh.movieLister(0);
                MyListAdapter adapter = new MyListAdapter("Bucket", MainActivity.this, cursor);
                myBucketFragment.setListAdapter(adapter);
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myBucketFragment).commit();
            setTitle(R.string.title_fragment_my_bucket);

        } else if (id == R.id.nav_myhistory) {
            //TODO: Update the list on every press.
            if (myHistoryFragment == null) {
                myHistoryFragment = new SpecialListFragment();
                Cursor cursor = dbh.movieLister(1);
                myHistoryFragment.setListAdapter(new MyListAdapter("History", MainActivity.this, cursor));
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myHistoryFragment).commit();
            setTitle(R.string.title_fragment_my_history);

        } else if (id == R.id.nav_about) {
            if (aboutFragment == null) {
                aboutFragment = new AbootFragment();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, aboutFragment).commit();
            setTitle(R.string.title_fragment_about);
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

    /**
     * Retry connectivity listener from offline fragment
     */
    public void onFragmentInteraction() {
        if(checkConnectivity()) {
            if (discoverFragment == null) {
                discoverFragment = new RecyclerViewFragment();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, discoverFragment).commit();
        }
    }

    /**
     * Check if device is connected to internet
     */
    private boolean checkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());
    }
}
