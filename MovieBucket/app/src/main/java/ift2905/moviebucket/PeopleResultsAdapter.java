package ift2905.moviebucket;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.people.Person;

/**
 * List adapter for people results
 * Created by Amélie on 2017-04-25.
 */

public class PeopleResultsAdapter extends AbstractResultsAdapter{

    public PeopleResultsAdapter(List<Person> results, Context context) {
        super();
        this.results = new ArrayList<>();
        this.results.addAll(results);
        this.context = context;
    }

    public Multi.MediaType getItemType(int position) { return Multi.MediaType.PERSON; }

    @Override
    public long getItemId(int position) {
        if(getItem(position) != null) {
            return ((Person)results.get(position)).getId();
        }
        return 0;
    }

    public String getItemName(int position) {
        return ((Person)results.get(position)).getName();
    }

    public String getItemDate(int position) {
        return null;
    }

    public String getItemUrl(int position) {
        return ((Person)results.get(position)).getProfilePath();
    }

}
