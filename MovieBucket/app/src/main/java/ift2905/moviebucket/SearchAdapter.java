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
 * Adapter for search results
 * Created by Amélie on 2017-04-09.
 */

//
public class SearchAdapter extends BaseAdapter {

    final String BASE_URL = "http://image.tmdb.org/t/p/";
    final String SIZE_SMALL = "w154";

    List<MovieDb> movies;
    public Context context;

    public SearchAdapter(List<MovieDb> movies, Context context) {
        super();
        this.movies = new ArrayList<>();
        this.movies.addAll(movies);
        this.context = context;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return movies.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = li.inflate(R.layout.detailed_view, parent, false);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView overview = (TextView) convertView.findViewById(R.id.overview);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);

        title.setText(movies.get(position).getTitle());
        overview.setText(movies.get(position).getReleaseDate());

        Picasso.with(context)
                .load(BASE_URL + SIZE_SMALL + movies.get(position).getBackdropPath())
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
}