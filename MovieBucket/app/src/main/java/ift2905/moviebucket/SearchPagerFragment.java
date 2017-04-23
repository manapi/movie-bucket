package ift2905.moviebucket;

import android.os.AsyncTask;
import android.os.Bundle;
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
import info.movito.themoviedbapi.model.people.Person;
import info.movito.themoviedbapi.model.tv.TvSeries;

/**
 * Pager in fragment for search
 * Created by Amélie on 2017-04-22.
 */

public class SearchPagerFragment extends Fragment {

    private int NUM_ITEMS = 4;

    //TODO : get from bundle
    private final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";
    private final String LANG = "en";
    private final Boolean ADULT = false; //include adult movies in search results

    protected String query;
    protected ListFragment list;

    protected ListFragment[] listArray;

    protected MovieResultsAdapter movieAdapter;
    protected SeriesResultsAdapter seriesAdapter;
    protected PeopleResultsAdapter peopleAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.pager_frag, container, false);

        TabLayout tabs = (TabLayout) rootView.findViewById(R.id.tabLayout);
        ViewPager pager = (ViewPager) rootView.findViewById(R.id.viewPager);

        listArray = new ListFragment[NUM_ITEMS];
        listArray[0] = new ListFragment();
        listArray[1] = new ListFragment();
        listArray[2] = new ListFragment();
        listArray[3] = new ListFragment();

        movieAdapter = new MovieResultsAdapter(new ArrayList<MovieDb>(), getContext());
        listArray[0].setListAdapter(movieAdapter);

        seriesAdapter = new SeriesResultsAdapter(new ArrayList<TvSeries>(), getContext());
        listArray[1].setListAdapter(seriesAdapter);

        peopleAdapter = new PeopleResultsAdapter(new ArrayList<Person>(), getContext());
        listArray[2].setListAdapter(peopleAdapter);

        pager.setAdapter(new MyAdapter((getChildFragmentManager())));

        //pager.setAdapter(adapterViewPager);
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

        tabs.setupWithViewPager(pager);

        return rootView;
    }

    private class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm){
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {

            /*switch(position) {
                case 0 :
                    movieAdapter = new MovieResultsAdapter(new ArrayList<MovieDb>(), getContext());
                    listArray[position].setListAdapter(movieAdapter);
                    break;
                case 1 :
                    seriesAdapter = new SeriesResultsAdapter(new ArrayList<TvSeries>(), getContext());
                    listArray[position].setListAdapter(seriesAdapter);
                    break;
                case 2 :
                    List<Person> data = peopleResults != null ? peopleResults : new ArrayList<Person>();
                    peopleAdapter = new PeopleResultsAdapter(data, getContext());
                    listArray[position].setListAdapter(peopleAdapter);
                    break;
                default :
            }*/
            return listArray[position];
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        private String[] titles = new String[]{
                "Movies",
                "TV",
                "People",
                "Genre"
        };

        @Override
        public CharSequence getPageTitle(int position) {

            return titles[position];
        }
    };

    public void search(String query) {
        this.query = query;
        new FetchMovies().execute();
        new FetchSeries().execute();
        new FetchPeople().execute();
    }

    /**
     * Fetch movie results from API
     */
    // TODO : get multiple pages...!!
    public class FetchMovies extends AsyncTask<String, Object,  List<MovieDb>> {

        @Override
        protected List<MovieDb> doInBackground(String... params) {

            TmdbApi api = new TmdbApi(API_KEY);
            TmdbSearch search = api.getSearch();
            List<MovieDb> results = search.searchMovie(query, null, LANG, ADULT, 1).getResults();

            return results;
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

            TmdbApi api = new TmdbApi(API_KEY);
            TmdbSearch search = api.getSearch();
            List<TvSeries> results = search.searchTv(query, LANG, 1).getResults();

            return results;
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

            TmdbApi api = new TmdbApi(API_KEY);
            TmdbSearch search = api.getSearch();
            List<Person> results = search.searchPerson(query, ADULT, 1).getResults();

            return results;
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
}
