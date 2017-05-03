package ift2905.moviebucket;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.MovieDb;

/**
 * Recycler view in fragment for discover
 */
public class RecyclerViewFragment extends Fragment {

    private static final int SPAN_COUNT = 2;
    private static final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";

    // Settings
    private static String lang;

    protected RecyclerView mRecyclerView;
    protected PosterCardAdapter mAdapter;

    // Data
    protected List<MovieDb> mList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Sets up the information language.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        lang = prefs.getString(SettingsFragment.KEY_LOCALE, "en");

        //Initialize dataset
        FetchSuggestions fetcher = new FetchSuggestions();
        fetcher.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_frag, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        //Set adapter if data has been fetched
        if(mList != null) {
            settingAdapter();
        }

        //Set grid layout
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
        mRecyclerView.setLayoutManager(mLayoutManager);

        return rootView;
    }

    /**
     * Set recycler view adapter
     */
    private void settingAdapter() {
        mAdapter = new PosterCardAdapter(getActivity(), mList);
        mRecyclerView.setAdapter(mAdapter);

        // New movie view activity on click
        mAdapter.setOnItemClickListener(new PosterCardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MovieDb movie) {
                Context context = getActivity();
                Intent intent = new Intent(context, MovieView.class);
                intent.putExtra(AbstractResultsAdapter.Type.movie.name(), ((long)movie.getId()));
                context.startActivity(intent);
            }
        });
    }

    /**
     * Refresh list content after settings change
     */
    public void refresh(){

        // Update language
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        lang = prefs.getString(SettingsFragment.KEY_LOCALE, "en");

        // Refetch data from API and update adapter
        new FetchSuggestions().execute();
    }

    /**
     * Fetch suggestions for discover fragment and set to list adapter
     */
    public class FetchSuggestions extends AsyncTask<String, Object, List<MovieDb>> {

        @Override
        protected List<MovieDb> doInBackground(String... params) {

            List<MovieDb> suggestions = new ArrayList<>();

            try {
                TmdbApi api = new TmdbApi(API_KEY);
                suggestions = api.getMovies().getPopularMovies(lang, 1).getResults();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return suggestions;
        }
        @Override
        protected void onPostExecute(List<MovieDb> suggestions) {

            mList = suggestions;
            settingAdapter();
        }
    }
}