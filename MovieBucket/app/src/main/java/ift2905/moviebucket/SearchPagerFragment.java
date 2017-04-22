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
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.Multi;

/**
 * Pager in fragment for search
 * Created by Amélie on 2017-04-22.
 */

public class SearchPagerFragment extends Fragment {

    private final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";

    protected SearchPagerAdapter adapterViewPager;
    protected String query;
    //protected ListFragment moviesRes, tvRes, peopleRes, genresRes;
    protected ListFragment list;
    protected ViewPager pager;
    protected List<Multi> res;
    protected MyAdapter adapter;
    protected ResultsListAdapter a;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Bundle args = getArguments();
        query = (String) args.get("query");

        SearchPagerFragment.FetchResults fetcher = new SearchPagerFragment.FetchResults();
        fetcher.execute();*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.pager_frag, container, false);

        TabLayout tabs = (TabLayout) rootView.findViewById(R.id.tabLayout);
        pager = (ViewPager) rootView.findViewById(R.id.viewPager);

        //adapterViewPager = new SearchPagerAdapter(getChildFragmentManager());
        adapter = new MyAdapter((getChildFragmentManager()));
        pager.setAdapter(adapter);

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

            list = new ListFragment();

            /*if(res != null) {
                list.setListAdapter(new ResultsListAdapter(res, getContext()));
            }
            else {
                list.setListAdapter(new ResultsListAdapter(new ArrayList<Multi>(), getContext()));
            }*/
            ResultsListAdapter adapt = new ResultsListAdapter(new ArrayList<Multi>(), getContext());
            if(position == 0) {
                res = adapt.getData();
                a = adapt;
            }
            list.setListAdapter(adapt);
            return list;
        }

        @Override
        public int getCount() {
            return 4;
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

    // TODO : add search options, get multiple pages...
    /**
     * Fetch search results from api and set to list adapter
     * Created by Amélie on 2017-04-09.
     */

    public class FetchResults extends AsyncTask<String, Object,  List<Multi>> {

        final String LANG = "en";
        final Boolean ADULT = false; //include adult movies in search results
        android.app.ListFragment fragment;

        @Override
        protected List<Multi> doInBackground(String... params) {

            //TODO: handle advanced search parameters
            TmdbApi api = new TmdbApi(API_KEY);
            TmdbSearch search = api.getSearch();
            //List<MovieDb> results = search.searchMovie(query, null, LANG, ADULT, 1).getResults();
            List<Multi> results = search.searchMulti(query, LANG, 1).getResults();

            return results;
        }

        @Override
        protected void onPostExecute(List<Multi> results) {
            res.clear();
            res.addAll(results);
            a.notifyDataSetChanged();
            //list.setListAdapter(new SearchAdapter(results, getContext()));
        }
    }

    public void search(String query) {
        this.query = query;
        FetchResults fetcher = new FetchResults();
        fetcher.execute();
    }

}
