package ift2905.moviebucket;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by Amélie on 2017-04-25.
 */

public class MovieResultsAdapter extends AbstractResultsAdapter{

    public MovieResultsAdapter(List<MovieDb> results, Context context) {
        super();
        this.results = new ArrayList<>();
        this.results.addAll(results);
        this.context = context;
        this.type = Type.movie;
    }

    @Override
    public long getItemId(int position) {
        return ((MovieDb)results.get(position)).getId();
    }

    public String getItemName(int position) {
        return ((MovieDb)results.get(position)).getTitle();
    }

    public String getItemDate(int position) {
        return ((MovieDb)results.get(position)).getReleaseDate();
    }

    public String getItemUrl(int position) {
        return ((MovieDb)results.get(position)).getPosterPath();
    }

}