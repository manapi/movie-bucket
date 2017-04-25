package ift2905.moviebucket;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.model.people.Person;

/**
 * Created by Amélie on 2017-04-25.
 */

public class PeopleResultsAdapter extends AbstractResultsAdapter{

    public PeopleResultsAdapter(List<Person> results, Context context, InnerListFragmentListener listener) {
        super();
        this.results = new ArrayList<>();
        this.results.addAll(results);
        this.context = context;
        this.type = Type.person;
        this.listener = listener;
    }

    @Override
    public long getItemId(int position) {
        return ((Person)results.get(position)).getId();
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
