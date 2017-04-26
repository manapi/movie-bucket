package ift2905.moviebucket;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.tv.TvSeries;

/**
 * Abstract adapter for search results
 * Created by Amélie on 2017-04-09.
 */

public abstract class AbstractResultsAdapter extends BaseAdapter {

    public enum Type {
        top, movie, tv, person
    }


    public interface InnerListFragmentListener {
        void onSwitchToNextFragment(int personId);
    }

    final String BASE_URL = "http://image.tmdb.org/t/p/";
    final String SIZE_SMALL = "w154";

    protected List<Object> results;
    protected Context context;

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int position) {
        return results.get(position);
    }

    abstract public Multi.MediaType getItemType(int position);

    @Override
    abstract public long getItemId(int position);

    abstract public String getItemName(int position);

    abstract public String getItemDate(int position);

    abstract public String getItemUrl(int position);

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = li.inflate(R.layout.search_view, parent, false);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView release = (TextView) convertView.findViewById(R.id.release);
        TextView type = (TextView) convertView.findViewById(R.id.type);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);

        String year = getItemDate(position);
        if(year != null && year.length() >= 4) {
            year = year.substring(0, 4);
        }

        title.setText(getItemName(position));
        release.setText(year);

        String arg;
        switch(getItemType(position)){
            case MOVIE:
                arg = "Movie";
                break;
            case TV_SERIES:
                arg = "TV";
                break;
            case PERSON:
                arg = "Person";
                break;
            default:
                arg = null;
        }
        type.setText(arg);

        String url = getItemUrl(position);
        if(url != null) {
            url = BASE_URL + SIZE_SMALL + url;
        }

        Picasso.with(context)
                .load(url)
                .into(image);

        convertView.setOnClickListener(new View.OnClickListener() {

            // Create a new activity (detailed view of the selected movie)
            @Override
            public void onClick(View v) {
                String arg;
                Intent intent;
                switch(getItemType(position)) {
                    case MOVIE:
                        arg = Type.movie.name();
                        intent = new Intent(context, MovieView.class);
                        intent.putExtra(arg, getItemId(position));
                        break;
                    case TV_SERIES:
                        arg = Type.tv.name();
                        intent = new Intent(context, MovieView.class);
                        intent.putExtra(arg, getItemId(position));
                        break;
                    case PERSON:
                        arg = Type.person.name();
                        intent = new Intent(context, PersonView.class);
                        intent.putExtra(arg, getItemId(position));
                        intent.putExtra("name", getItemName(position));
                        break;
                    default :
                        intent = null;
                }

                if (intent != null) {
                    context.startActivity(intent);
                }

            }
        });
        return convertView;
    }

    public List<Object> getData() {
        return results;
    }
}
