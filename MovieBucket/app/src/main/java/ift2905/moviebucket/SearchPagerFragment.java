package ift2905.moviebucket;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.people.Person;
import info.movito.themoviedbapi.model.tv.TvSeries;

/**
 * Pager in fragment for search
 * Created by Amélie on 2017-04-22.
 */

public class SearchPagerFragment extends Fragment {

    //TODO : get from bundle
    private final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";
    private String lang;
    private Boolean adult; //include adult movies in search results

    protected ListFragment[] listArray;

    protected TopResultsAdapter topAdapter;
    protected MovieResultsAdapter movieAdapter;
    protected SeriesResultsAdapter seriesAdapter;
    protected PeopleResultsAdapter peopleAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Sets up the information language.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        lang = prefs.getString(SettingsActivity.KEY_LOCALE, "en");
        adult = prefs.getBoolean(SettingsActivity.KEY_ADULT_PREF, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.pager_frag, container, false);

        TabLayout tabs = (TabLayout) rootView.findViewById(R.id.tabLayout);
        ViewPager mPager = (ViewPager) rootView.findViewById(R.id.viewPager);

        listArray = new ListFragment[4];
        listArray[0] = new ListFragment();
        listArray[1] = new ListFragment();
        listArray[2] = new ListFragment();
        listArray[3] = new ListFragment();

        topAdapter = new TopResultsAdapter(new ArrayList<Multi>(), getContext());
        listArray[0].setListAdapter(topAdapter);

        movieAdapter = new MovieResultsAdapter(new ArrayList<MovieDb>(), getContext());
        listArray[1].setListAdapter(movieAdapter);

        seriesAdapter = new SeriesResultsAdapter(new ArrayList<TvSeries>(), getContext());
        listArray[2].setListAdapter(seriesAdapter);

        peopleAdapter = new PeopleResultsAdapter(new ArrayList<Person>(), getContext());
        listArray[3].setListAdapter(peopleAdapter);

        MyAdapter mAdapter = new MyAdapter((getChildFragmentManager()));

        mPager.setAdapter(mAdapter);

        /*vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                Toast.makeText(getActivity(),
                        "Selected page position: " + position, Toast.LENGTH_SHORT).show();
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });*/

        tabs.setupWithViewPager(mPager);

        return rootView;
    }

    public void onBackPressed() {
        /*if(mPager.getCurrentItem() == 0) {
            if (mAdapter.getItem(0) instanceof DetallesFacturaFragment) {
                ((DetallesFacturaFragment) mAdapter.getItem(0)).backPressed();
            }
            else if (mAdapter.getItem(0) instanceof FacturasFragment) {
                finish();
            }
        }*/
    }

    private class MyAdapter extends FragmentPagerAdapter {

        private final String[] titles = new String[]{
                "Top",
                "Movies",
                "TV",
                "People",
        };

        private FragmentManager mFragmentManager;

        public MyAdapter(FragmentManager fm){
            super(fm);
            mFragmentManager = fm;
        }
        @Override
        public Fragment getItem(int position) {

            return listArray[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return titles[position];
        }
    };

    public void search(String query) {
        new FetchTop().execute(query);
        new FetchMovies().execute(query);
        new FetchSeries().execute(query);
        new FetchPeople().execute(query);
    }

    /**
     * Fetch top results from API
     */
    // TODO : get multiple pages...!!
    public class FetchTop extends AsyncTask<String, Object,  List<Multi>> {

        @Override
        protected List<Multi> doInBackground(String... params) {
            if(params[0] != null) {
                String query = params[0];
                TmdbApi api = new TmdbApi(API_KEY);
                TmdbSearch search = api.getSearch();
                return search.searchMulti(query, lang, 1).getResults();
            }
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<Multi> results) {
            if(topAdapter != null) {
                topAdapter.getData().clear();
                topAdapter.getData().addAll(results);
                topAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Fetch movie results from API
     */
    // TODO : get multiple pages...!!
    public class FetchMovies extends AsyncTask<String, Object,  List<MovieDb>> {

        @Override
        protected List<MovieDb> doInBackground(String... params) {
            if(params[0] != null) {
                String query = params[0];
                TmdbApi api = new TmdbApi(API_KEY);
                TmdbSearch search = api.getSearch();
                return search.searchMovie(query, null, lang, adult, 1).getResults();
            }
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<MovieDb> results) {
            if(movieAdapter != null) {
                movieAdapter.getData().clear();
                movieAdapter.getData().addAll(results);
                movieAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Fetch TV series results from API
     */
    // TODO : get multiple pages...!!
    public class FetchSeries extends AsyncTask<String, Object,  List<TvSeries>> {

        @Override
        protected List<TvSeries> doInBackground(String... params) {

            if(params[0] != null) {
                String query = params[0];
                TmdbApi api = new TmdbApi(API_KEY);
                TmdbSearch search = api.getSearch();
                return search.searchTv(query, lang, 1).getResults();
            }
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<TvSeries> results) {
            if(seriesAdapter != null) {
                seriesAdapter.getData().clear();
                seriesAdapter.getData().addAll(results);
                seriesAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Fetch people results from API
     */
    // TODO : get multiple pages...!!
    public class FetchPeople extends AsyncTask<String, Object,  List<Person>> {

        @Override
        protected List<Person> doInBackground(String... params) {

            if(params[0] != null) {
                String query = params[0];
                TmdbApi api = new TmdbApi(API_KEY);
                TmdbSearch search = api.getSearch();
                return search.searchPerson(query, adult, 1).getResults();
            }
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<Person> results) {
            if(listArray[3] != null && peopleAdapter != null) {
                peopleAdapter.getData().clear();
                peopleAdapter.getData().addAll(results);
                peopleAdapter.notifyDataSetChanged();
                listArray[3].setListAdapter(peopleAdapter);
            }
        }
    }
}

