package ift2905.moviebucket;

import android.app.ListFragment;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.MovieDb;

/**
 * Fetch suggestions for discover fragment and set to list adapter
 */
public class FetchSuggestions extends AsyncTask<String, Object, List<MovieDb>> {

    final String LANG = "en";
    ListFragment fragment;
    Context context;
    String key;

    FetchSuggestions(ListFragment fragment, Context context, String key) {
        super();
        this.fragment = fragment;
        this.context = context;
        this.key = key;
    }

    @Override
    protected List<MovieDb> doInBackground(String... params) {

        TmdbApi api = new TmdbApi(key);
        List<MovieDb> suggestions = api.getMovies().getPopularMovies(LANG, 1).getResults();
        return suggestions;
    }
    @Override
    protected void onPostExecute(List<MovieDb> suggestions) {

        // TODO : replace with cards layout
        fragment.setListAdapter(new SearchAdapter(suggestions, context));
    }
}