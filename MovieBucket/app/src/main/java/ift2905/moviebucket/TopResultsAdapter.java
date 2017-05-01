package ift2905.moviebucket;

/**
 * Created by Amélie on 2017-04-26.
 */

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.people.Person;
import info.movito.themoviedbapi.model.tv.TvSeries;

/**
 * List adapter for top results
 * Created by Amélie on 2017-04-25.
 */

public class TopResultsAdapter extends AbstractResultsAdapter{

    public TopResultsAdapter(List<Multi> results, Context context) {
        super();
        this.results = new ArrayList<>();
        this.results.addAll(results);
        this.context = context;
    }

    public Multi.MediaType getItemType(int position) { return ((Multi)results.get(position)).getMediaType(); }

    @Override
    public long getItemId(int position) {
        if(getItem(position) != null) {
            switch(getItemType(position)) {
                case MOVIE:
                    return ((MovieDb)results.get(position)).getId();
                case TV_SERIES:
                    return ((TvSeries)results.get(position)).getId();
                case PERSON:
                    return ((Person)results.get(position)).getId();
                default:
                    return 0;
            }
        }
        return 0;
    }

    public String getItemName(int position) {
        switch(getItemType(position)) {
            case MOVIE:
                return ((MovieDb)results.get(position)).getTitle();
            case TV_SERIES:
                return ((TvSeries)results.get(position)).getName();
            case PERSON:
                return ((Person)results.get(position)).getName();
            default:
                return null;
        }
    }

    public String getItemDate(int position) {
        switch(getItemType(position)) {
            case MOVIE:
                return ((MovieDb)results.get(position)).getReleaseDate();
            case TV_SERIES:
                return ((TvSeries)results.get(position)).getFirstAirDate();
            default:
                return null;
        }
    }

    public String getItemUrl(int position) {
        switch(getItemType(position)) {
            case MOVIE:
                return ((MovieDb)results.get(position)).getPosterPath();
            case TV_SERIES:
                return ((TvSeries)results.get(position)).getPosterPath();
            case PERSON:
                return ((Person)results.get(position)).getProfilePath();
            default:
                return null;
        }
    }
}
