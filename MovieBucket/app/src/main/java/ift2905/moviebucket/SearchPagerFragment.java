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
        new FetchTop().execute(query, "0");
        new FetchMovies().execute(query, "0");
        new FetchSeries().execute(query, "0");
        new FetchPeople().execute(query, "0");
    }

    /**
     * Fetch top results from API
     */
    public class FetchTop extends AsyncTask<String, Object,  List<Multi>> {

        String query;
        int page;

        @Override
        protected List<Multi> doInBackground(String... params) {
            if(params.length > 1) {
                query = params[0];
                page = new Integer(params[1]);

                try{
                    TmdbApi api = new TmdbApi(API_KEY);
                    TmdbSearch search = api.getSearch();
                    return search.searchMulti(query, lang, page).getResults();
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
                if(page == 0) {
                    topAdapter.getData().clear();
                    if(!results.isEmpty()) {
                        topAdapter.getData().addAll(results);

                    } else {
                        topAdapter.getData().add(0, null);
                    }
                }
                else {
                    topAdapter.getData().addAll(results);
                }
                topAdapter.notifyDataSetChanged();

                if(results.size() == 20 && page < 5) {
                    page++;
                    new FetchTop().execute(query, new Integer(page).toString());
                }
            }
        }
    }

    /**
     * Fetch movie results from API
     */
    public class FetchMovies extends AsyncTask<String, Object,  List<MovieDb>> {

        String query;
        int page;

        @Override
        protected List<MovieDb> doInBackground(String... params) {
            if(params.length > 1) {
                query = params[0];
                page = new Integer(params[1]);

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
                if(page == 0) {
                    movieAdapter.getData().clear();
                    if(!results.isEmpty()) {
                        movieAdapter.getData().addAll(results);

                    } else {
                        movieAdapter.getData().add(0, null);
                    }
                }
                else {
                    movieAdapter.getData().addAll(results);
                }
                movieAdapter.notifyDataSetChanged();

                if(results.size() == 20 && page < 5) {
                    page++;
                    new FetchMovies().execute(query, new Integer(page).toString());
                }
            }
        }
    }

    /**
     * Fetch TV series results from API
     */
    public class FetchSeries extends AsyncTask<String, Object,  List<TvSeries>> {

        String query;
        int page;

        @Override
        protected List<TvSeries> doInBackground(String... params) {
            if(params.length > 1) {
                query = params[0];
                page = new Integer(params[1]);

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
                if(page == 0) {
                    seriesAdapter.getData().clear();
                    if (!results.isEmpty()) {
                        seriesAdapter.getData().addAll(results);

                    } else {
                        seriesAdapter.getData().add(0, null);
                    }
                }
                else {
                    seriesAdapter.getData().addAll(results);
                }
                seriesAdapter.notifyDataSetChanged();

                if(results.size() == 20 && page < 5) {
                    page++;
                    new FetchSeries().execute(query, new Integer(page).toString());
                }
            }
        }
    }

    /**
     * Fetch people results from API
     */
    public class FetchPeople extends AsyncTask<String, Object,  List<Person>> {

        String query;
        int page;

        @Override
        protected List<Person> doInBackground(String... params) {
            if(params.length > 1) {
                query = params[0];
                page = new Integer(params[1]);

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
                if(page == 0) {
                    peopleAdapter.getData().clear();
                    if(!results.isEmpty()) {
                        peopleAdapter.getData().addAll(results);

                    } else {
                        peopleAdapter.getData().add(0, null);
                    }
                }
                else {
                    peopleAdapter.getData().addAll(results);
                }
                peopleAdapter.notifyDataSetChanged();

                if(results.size() == 20 && page < 5) {
                    page++;
                    new FetchPeople().execute(query, new Integer(page).toString());
                }
            }
        }
    }
    public void setAdult(Boolean bool){
        adult = bool;
    }
}

