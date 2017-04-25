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

/**
 * Adapter for movie search results
 * Created by Amélie on 2017-04-09.
 */
public class MovieResultsAdapter extends BaseAdapter {

    final String BASE_URL = "http://image.tmdb.org/t/p/";
    final String SIZE_SMALL = "w154";

    List<MovieDb> results;
    public Context context;

    public MovieResultsAdapter(List<MovieDb> results, Context context) {
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
    public long getItemId(int position) { return results.get(position).getId();        }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = li.inflate(R.layout.search_view, parent, false);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView release = (TextView) convertView.findViewById(R.id.release);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);

        MovieDb movie = results.get(position);
        String name = movie.getTitle();
        String year = movie.getReleaseDate();
        String url = BASE_URL + SIZE_SMALL + movie.getPosterPath();

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
                intent.putExtra("movie", getItemId(position));
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    public List<MovieDb> getData() {
        return results;
    }
}
