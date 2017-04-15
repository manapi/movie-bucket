package ift2905.moviebucket;

import android.app.ListFragment;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.Multi;


// TODO : add search options, get multiple pages...
/**
 * Fetch search results from api and set to list adapter
 * Created by Amélie on 2017-04-09.
 */

public class FetchResults extends AsyncTask<String, Object,  List<Multi>> {

    final String LANG = "en";
    final Boolean ADULT = false; //include adult movies in search results
    String query;
    ListFragment fragment;
    Context context;
    String key;

    FetchResults(String query, ListFragment fragment, Context context, String key) {
        super();
        this.query = query;
        this.fragment = fragment;
        this.context = context;
        this.key = key;
    }

    @Override
    protected List<Multi> doInBackground(String... params) {

        //TODO: handle advanced search parameters
        TmdbApi api = new TmdbApi(key);
        TmdbSearch search = api.getSearch();
        //List<MovieDb> results = search.searchMovie(query, null, LANG, ADULT, 1).getResults();
        List<Multi> results = search.searchMulti(query, LANG, 1).getResults();

        return results;
    }

    @Override
    protected void onPostExecute(List<Multi> results) {

        fragment.setListAdapter(new SearchAdapter(results, context));
    }
}
