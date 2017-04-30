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
 * Pager view in fragment for search
 * Created by Amélie on 2017-04-22.
 */

public class SearchPagerFragment extends Fragment {

    private final static String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";

    // Settings
    private static String lang;
    static Boolean adult; //include adult movies in search results

    protected ListFragment[] pagerList;

    protected TopResultsAdapter topAdapter;
    protected MovieResultsAdapter movieAdapter;
    protected SeriesResultsAdapter seriesAdapter;
    protected PeopleResultsAdapter peopleAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Sets up the information language.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        lang = prefs.getString(SettingsFragment.KEY_LOCALE, "en");
        adult = prefs.getBoolean(SettingsFragment.KEY_ADULT_PREF, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.pager_frag, container, false);

        TabLayout tabs = (TabLayout) rootView.findViewById(R.id.tabLayout);
        ViewPager mPager = (ViewPager) rootView.findViewById(R.id.viewPager);

        // Create fragments to be added to pager
        pagerList = new ListFragment[4];
        pagerList[0] = new ListFragment();
        pagerList[1] = new ListFragment();
        pagerList[2] = new ListFragment();
        pagerList[3] = new ListFragment();

        // Set adapters to fragments
        topAdapter = new TopResultsAdapter(new ArrayList<Multi>(), getContext());
        pagerList[0].setListAdapter(topAdapter);

        movieAdapter = new MovieResultsAdapter(new ArrayList<MovieDb>(), getContext());
        pagerList[1].setListAdapter(movieAdapter);

        seriesAdapter = new SeriesResultsAdapter(new ArrayList<TvSeries>(), getContext());
        pagerList[2].setListAdapter(seriesAdapter);

        peopleAdapter = new PeopleResultsAdapter(new ArrayList<Person>(), getContext());
        pagerList[3].setListAdapter(peopleAdapter);

        // Set pager adapter
        MyAdapter mAdapter = new MyAdapter((getChildFragmentManager()));
        mPager.setAdapter(mAdapter);

        tabs.setupWithViewPager(mPager);

        return rootView;
    }

    private class MyAdapter extends FragmentPagerAdapter {

        private final String[] titles = new String[]{
                getString(R.string.spf_top),
                getString(R.string.spf_movies),
                getString(R.string.spf_tv),
                getString(R.string.spf_people),
        };

        private FragmentManager mFragmentManager;

        public MyAdapter(FragmentManager fm){
            super(fm);
            mFragmentManager = fm;
        }
        @Override
        public Fragment getItem(int position) {

            return pagerList[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) { return titles[position]; }
    };

    /**
     * Fetch all search results from API
     */
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
            if(params.length > 0) {
                String query = params[0];

                try{
                    TmdbApi api = new TmdbApi(API_KEY);
                    TmdbSearch search = api.getSearch();
                    return search.searchMulti(query, lang, 1).getResults();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
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
            if(params.length > 0) {
                String query = params[0];

                try {
                    TmdbApi api = new TmdbApi(API_KEY);
                    TmdbSearch search = api.getSearch();
                    return search.searchMovie(query, null, lang, adult, 1).getResults();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
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

            if(params.length > 0) {
                String query = params[0];

                try{
                    TmdbApi api = new TmdbApi(API_KEY);
                    TmdbSearch search = api.getSearch();
                    return search.searchTv(query, lang, 1).getResults();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
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

            if(params.length > 0) {
                String query = params[0];

                try {
                    TmdbApi api = new TmdbApi(API_KEY);
                    TmdbSearch search = api.getSearch();
                    return search.searchPerson(query, adult, 1).getResults();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<Person> results) {
            if(peopleAdapter != null) {
                peopleAdapter.getData().clear();
                peopleAdapter.getData().addAll(results);
                peopleAdapter.notifyDataSetChanged();
            }
        }
    }
    public void setAdult(Boolean bool){
        adult = bool;
    }
}

