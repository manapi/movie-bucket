package ift2905.moviebucket;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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

    private ListFragment searchFragment, discoverFragment, myBucketFragment, myHistoryFragment;
    private Fragment aboutFragment;
    private String[] myBucket, myHistory;
    private SearchView searchView;

    final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // Displaying suggestions on startup : currently, popular movies (other options possible, what do we want?)
        discoverFragment = new ListFragment();
        FetchSuggestions fetcher = new FetchSuggestions(discoverFragment, MainActivity.this, API_KEY);
        fetcher.execute();

        //Initialize discover fragment by default
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, discoverFragment).commit();
        setTitle(R.string.title_fragment_discover);

        // TODO: Read everything from local data base
        //To be replaced with actual data
        //I'm thinking of filling the myBucket array with strings of
        //this format:Favorite(1 or 0 whether it's starred or not)+title

        myBucket = new String[20];
        myBucket[0] = "1Langouste";
        myBucket[1] = "1Rock Lobster";
        myBucket[2] = "1BasedGodRockLobster";
        myBucket[3] = "1All Hail The BasedGodLobster";
        myBucket[4] = "0Creative Bankrupcy";
        myBucket[5] = "0Shenanigans";
        myBucket[6] = "0More Shenanigans";
        myBucket[7] = "0Some More Shenanigans";
        myBucket[8] = "1Even More Shenanigans";
        myBucket[9] = "1Shenanigans Redux";
        myBucket[10] = "0Shenanigans: The Reckoning";
        myBucket[11] = "1Shenanigans Rising";
        myBucket[12] = "0Shenanigans Revengeance";
        myBucket[13] = "0Shenanigans Forever";
        myBucket[14] = "0Shenanigans vs. Shenanigans";
        myBucket[15] = "1Shenanigans Reloaded";
        myBucket[16] = "0Shenanigans 5";
        myBucket[17] = "0The Making Of Shenanigans";
        myBucket[18] = "0Shenanigans: Director's Cut";
        myBucket[19] = "1Shenanigans: Deluxe Edition";

        myHistory = new String[1];
        myHistory[0] = "0my history";

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
            if (myBucketFragment == null) {
                myBucketFragment = new ListFragment();
                myBucketFragment.setListAdapter(new MyListAdapter(myBucket, MainActivity.this, basedGodRockLobster));
            }
            fragmentTransaction.replace(R.id.fragment_container, myBucketFragment).commit();
            setTitle(R.string.title_fragment_my_bucket);

        } else if (id == R.id.nav_myhistory) {
            if (myHistoryFragment == null) {
                myHistoryFragment = new ListFragment();
                myHistoryFragment.setListAdapter(new MyListAdapter(myHistory, MainActivity.this, basedGodRockLobster));
            }
            fragmentTransaction.replace(R.id.fragment_container, myHistoryFragment).commit();
            setTitle(R.string.title_fragment_my_history);

            //TODO: remove settings from drawer and put in overflow??
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);

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
        // TODO: Save everything to local data base
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    //TODO: implement the onClick method of this onClickListener
    public View.OnClickListener basedGodRockLobster = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int viewId = v.getId();



            switch(viewId){

                case R.id.starred :
                    //the code here would obtain the position of the item and update its star status
                    //in the Db
                    break;

                case R.id.notstarred :
                    //same as previous one
                    break;

                case R.id.more :
                    //TODO: find some way to add icons to popup menu, failing that, implement the menu some other way
                    //The code here generates the popup menu with the "delete","viewed" and
                    //"calendar" options. This'll be quite the headache

                    PopupMenu popup = new PopupMenu(MainActivity.this, v);

                    popup.getMenuInflater().inflate(R.menu.popup_menu_my_bucket, popup.getMenu());


                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            Toast.makeText(MainActivity.this,"You Clicked : " + item.getTitle() + ", have a cookie!",Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    });
		     
		            popup.show();
			
                    break;

                case R.id.mytitle:
                    //the code here creates a new intent for the Movie View activity and adds to it
                    //the selected movie's Movie Db id as an extra
                    break;
            }
        }
    };
}


