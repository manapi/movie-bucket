package ift2905.moviebucket;

import android.content.Context;
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
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.people.Person;
import info.movito.themoviedbapi.model.people.PersonCredit;
import info.movito.themoviedbapi.model.people.PersonCredits;
import info.movito.themoviedbapi.model.tv.TvSeries;

/**
 * Pager in fragment for search
 * Created by Amélie on 2017-04-22.
 */

public class SearchPagerFragment extends Fragment {

    //TODO : get from bundle
    private final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";
    private final String LANG = "en";
    private final Boolean ADULT = false; //include adult movies in search results

    protected String query;
    protected ListFragment list;
    protected MyAdapter mAdapter;
    ViewPager mPager;
    FragmentManager mFragmentManager;
    protected ListFragment[] listArray;

    protected MovieResultsAdapter movieAdapter;
    protected SeriesResultsAdapter seriesAdapter;
    protected PeopleResultsAdapter peopleAdapter;
    protected GenreAdapter genreAdapter;

    protected PeopleListListener peopleListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.pager_frag, container, false);

        TabLayout tabs = (TabLayout) rootView.findViewById(R.id.tabLayout);
        mPager = (ViewPager) rootView.findViewById(R.id.viewPager);

        listArray = new ListFragment[4];
        listArray[0] = new ListFragment();
        listArray[1] = new ListFragment();
        listArray[2] = new ListFragment();
        listArray[3] = new ListFragment();

        movieAdapter = new MovieResultsAdapter(new ArrayList<MovieDb>(), getContext());
        listArray[0].setListAdapter(movieAdapter);

        seriesAdapter = new SeriesResultsAdapter(new ArrayList<TvSeries>(), getContext());
        listArray[1].setListAdapter(seriesAdapter);

        peopleListener = new PeopleListListener();
        peopleAdapter = new PeopleResultsAdapter(new ArrayList<Person>(), getContext(), peopleListener);
        listArray[2].setListAdapter(peopleAdapter);

        new FetchGenres().execute();

        mAdapter = new MyAdapter((getChildFragmentManager()));

        mFragmentManager = getChildFragmentManager();

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

    private final class PeopleListListener implements
                AbstractResultsAdapter.InnerListFragmentListener {
            public void onSwitchToNextFragment(int personId) {
                /*mFragmentManager.beginTransaction().remove(listArray[3])
                        .commit();*/
                if (listArray[2].getListAdapter() instanceof PeopleResultsAdapter){
                    new FetchCreditsFromPerson().execute(personId + "");

                }else{
                    listArray[2].setListAdapter(peopleAdapter);
                }
                mAdapter.notifyDataSetChanged();
            }
        }

    private final class GenreListListener implements
            AbstractResultsAdapter.InnerListFragmentListener {
        public void onSwitchToNextFragment(int genreId) {
                /*mFragmentManager.beginTransaction().remove(listArray[3])
                        .commit();*/
            if (listArray[3].getListAdapter() instanceof GenreAdapter){
                new FetchMoviesFromGenre().execute(genreId + "");

            }else{
                listArray[3].setListAdapter(genreAdapter);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private class MyAdapter extends FragmentPagerAdapter {

        private final String[] titles = new String[]{
                "Movies",
                "TV",
                "People",
                "Genre"
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
            if(listArray[2] != null && peopleAdapter != null) {
                peopleAdapter.getData().clear();
                peopleAdapter.getData().addAll(results);
                peopleAdapter.notifyDataSetChanged();
                listArray[2].setListAdapter(peopleAdapter);
            }
        }
    }

    public class FetchCreditsFromPerson extends AsyncTask<String, Object,  List<PersonCredit>> {

        @Override
        protected List<PersonCredit> doInBackground(String... params) {

            if(params[0] != null) {
                int id =  new Integer(params[0]);
                TmdbApi api = new TmdbApi(API_KEY);
                PersonCredits cred = api.getPeople().getPersonCredits(id);

                ArrayList<PersonCredit> credits = new ArrayList<>();

                for(PersonCredit c : cred.getCrew()) {
                    if (!credits.contains(c)){
                        credits.add(c);
                    }
                }
                credits.addAll(cred.getCast());
                return credits;
            }
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<PersonCredit> credits) {
            if(listArray[2] != null) {
                listArray[2].setListAdapter(new CreditsResultsAdapter(credits, getContext()));
            }
        }
    }

    public class FetchMoviesFromGenre extends AsyncTask<String, Object,  List<MovieDb>> {

        @Override
        protected List<MovieDb> doInBackground(String... params) {

            if(params[0] != null) {
                int id =  new Integer(params[0]);
                TmdbApi api = new TmdbApi(API_KEY);

                return api.getGenre().getGenreMovies(id, LANG, 1, false).getResults();
            }
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<MovieDb> results) {
            if(listArray[3] != null) {
                listArray[3].setListAdapter(new MovieResultsAdapter(results, getContext()));
            }
        }
    }

    public class FetchGenres extends AsyncTask<String, Object,  List<Genre>> {

        @Override
        protected List<Genre> doInBackground(String... params) {

            TmdbApi api = new TmdbApi(API_KEY);
            return api.getGenre().getGenreList(LANG);
        }

        @Override
        protected void onPostExecute(List<Genre> genres) {

            if(listArray[3] != null) {
                GenreListListener listener = new GenreListListener();
                genreAdapter = new GenreAdapter(genres, getContext(), listener);
                listArray[3].setListAdapter(genreAdapter);

            }
        }
    }

    private class GenreAdapter extends AbstractResultsAdapter {

        private GenreAdapter(List<Genre> genres, Context context, GenreListListener listener) {
            super();
            this.results = new ArrayList<>();
            this.results.addAll(genres);
            this.context = context;
            this.type = Type.genre;
            this.listener = listener;
        }

        @Override
        public long getItemId(int position) {
            return ((Genre) results.get(position)).getId();
        }

        public String getItemName(int position) {
            return ((Genre) results.get(position)).getName();
        }

        public String getItemDate(int position) {
            return null;
        }

        public String getItemUrl(int position) {
            return null;
        }
    }
}

