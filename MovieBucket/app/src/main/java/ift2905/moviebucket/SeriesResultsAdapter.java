package ift2905.moviebucket;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.model.tv.TvSeries;

/**
 * Created by Amélie on 2017-04-25.
 */

public class SeriesResultsAdapter extends AbstractResultsAdapter{

    public SeriesResultsAdapter(List<TvSeries> results, Context context) {
        super();
        this.results = new ArrayList<>();
        this.results.addAll(results);
        this.context = context;
        this.type = Type.tv;
    }

    @Override
    public long getItemId(int position) {
        return ((TvSeries)results.get(position)).getId();
    }

    public String getItemName(int position) {
        return ((TvSeries)results.get(position)).getName();
    }

    public String getItemDate(int position) {
        return ((TvSeries)results.get(position)).getFirstAirDate();
    }

    public String getItemUrl(int position) {
        return ((TvSeries)results.get(position)).getPosterPath();
    }

}
