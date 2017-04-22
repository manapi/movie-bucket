package ift2905.moviebucket;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.people.Person;
import info.movito.themoviedbapi.model.tv.TvSeries;

/**
 * Adapter for search results
 * Created by Amélie on 2017-04-09.
 */
public class ResultsListAdapter extends BaseAdapter {

    final String BASE_URL = "http://image.tmdb.org/t/p/";
    final String SIZE_SMALL = "w154";

    List<Multi> results;
    public Context context;

    public ResultsListAdapter(List<Multi> results, Context context) {
        super();
        this.results = new ArrayList<>();
        this.results.addAll(results);
        this.context = context;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        Multi item = results.get(position);

        switch(item.getMediaType()) {
            case MOVIE :
                return ((MovieDb)item).getId();
            case TV_SERIES:
                return ((TvSeries)item).getId();
            case PERSON:
                return ((Person)item).getId();
            default:
                return 0;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = li.inflate(R.layout.search_view, parent, false);

        //TODO: how to handle long title?
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView release = (TextView) convertView.findViewById(R.id.release);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);

        String name = null;
        String year = null;
        String url = null;

        if(results.get(position).getMediaType().equals(Multi.MediaType.MOVIE)) {
            MovieDb movie = (MovieDb) results.get(position);
            name = movie.getTitle();
            year = movie.getReleaseDate();
            url = BASE_URL + SIZE_SMALL + movie.getPosterPath();
        }
        else if (results.get(position).getMediaType().equals(Multi.MediaType.TV_SERIES)){
            TvSeries tv = (TvSeries) results.get(position);
            name = tv.getName();
            year = tv.getFirstAirDate();
            url = BASE_URL + SIZE_SMALL + tv.getPosterPath();
        }
        else if (results.get(position).getMediaType().equals(Multi.MediaType.PERSON)){
            Person person = (Person) results.get(position);
            name = person.getName();
            url = BASE_URL + SIZE_SMALL + person.getProfilePath();
        }

        if(year != null && year.length() >= 4) {
            year = year.substring(0, 4);
        }

        title.setText(name);
        release.setText(year);

        Picasso.with(context)
                .load(url)
                .into(image);

        convertView.setOnClickListener(new View.OnClickListener() {

            // Create a new activity (detailed view of the selected movie)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MovieView.class);
                if(results.get(position).getMediaType().equals(Multi.MediaType.MOVIE)) {
                    intent.putExtra("movie", getItemId(position));
                    context.startActivity(intent);
                } else if (results.get(position).getMediaType().equals((Multi.MediaType.TV_SERIES))){
                    intent.putExtra("tv", getItemId(position));
                    context.startActivity(intent);
                }
                //TODO: handle on click for TvSeries
                //TODO : handle on click for Person
            }
        });
        return convertView;
    }

    public List<Multi> getData() {
        return results;
    }
}
