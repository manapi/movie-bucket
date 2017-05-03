package ift2905.moviebucket;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;

/**
 * List adapter for movie results
 * Created by Amélie on 2017-04-25.
 */

public class MovieResultsAdapter extends AbstractResultsAdapter{

    public MovieResultsAdapter(List<MovieDb> results, Context context) {
        super();
        this.results = new ArrayList<>();
        this.results.addAll(results);
        this.context = context;
    }

    public Multi.MediaType getItemType(int position) {
        return Multi.MediaType.MOVIE; }

    @Override
    public long getItemId(int position) {
        if(getItem(position) != null) {
            return ((MovieDb)results.get(position)).getId();
        }
        return 0;
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
