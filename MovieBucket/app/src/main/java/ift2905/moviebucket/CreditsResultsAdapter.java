package ift2905.moviebucket;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.model.people.PersonCredit;

/**
 * Created by Amélie on 2017-04-25.
 */

public class CreditsResultsAdapter extends AbstractResultsAdapter{

    public CreditsResultsAdapter(List<PersonCredit> results, Context context) {
        super();
        this.results = new ArrayList<>();
        this.results.addAll(results);
        this.context = context;
        this.type = Type.movie;
    }

    @Override
    public long getItemId(int position) {
        return ((PersonCredit)results.get(position)).getMovieId();
    }

    public String getItemName(int position) {
        return ((PersonCredit)results.get(position)).getMovieTitle();
    }

    public String getItemDate(int position) {
        return ((PersonCredit)results.get(position)).getReleaseDate();
    }

    public String getItemUrl(int position) {
        return ((PersonCredit)results.get(position)).getPosterPath();
    }


}
