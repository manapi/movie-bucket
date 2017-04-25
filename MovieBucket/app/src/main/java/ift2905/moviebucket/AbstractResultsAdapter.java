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

import info.movito.themoviedbapi.model.tv.TvSeries;

/**
 * Abstract adapter for search results
 * Created by Amélie on 2017-04-09.
 */

public abstract class AbstractResultsAdapter extends BaseAdapter {

    public enum Type {
        movie, tv, person, genre
    }


    public interface InnerListFragmentListener {
        void onSwitchToNextFragment(int personId);
    }

    final String BASE_URL = "http://image.tmdb.org/t/p/";
    final String SIZE_SMALL = "w154";

    protected Type type;

    protected List<Object> results;
    protected Context context;

    protected InnerListFragmentListener listener;

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int position) {
        return results.get(position);
    }

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
        ImageView image = (ImageView) convertView.findViewById(R.id.image);

        String year = getItemDate(position);
        if(year != null && year.length() >= 4) {
            year = year.substring(0, 4);
        }

        title.setText(getItemName(position));
        release.setText(year);

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
                if(type.equals(Type.movie) || type.equals(Type.tv)) {
                    Intent intent = new Intent(context, MovieView.class);
                    intent.putExtra(type.name(), getItemId(position));
                    context.startActivity(intent);
                }
                else if (type.equals(Type.genre) || type.equals(Type.person)) {
                    if(listener != null) {
                        listener.onSwitchToNextFragment((int) getItemId(position));
                    }
                }
            }
        });
        return convertView;
    }

    public List<Object> getData() {
        return results;
    }
}
