package ift2905.moviebucket;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by Amélie on 2017-04-20.
 */
public class PosterCardAdapter extends RecyclerView.Adapter<PosterCardAdapter.CustomViewHolder> {
    private List<MovieDb> list;
    private Context mContext;

    public PosterCardAdapter(Context context, List<MovieDb> list) {
        this.list = list;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_card, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        final MovieDb movie = list.get(i);

        //Render image using Picasso library
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w300" + movie.getPosterPath())
                    //.error(R.drawable.placeholder)
                    //.placeholder(R.drawable.placeholder)
                    .into(customViewHolder.imageView);


        //Setting text view title
        customViewHolder.textView.setText(Html.fromHtml(movie.getTitle()));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(movie);
            }
        };
        customViewHolder.imageView.setOnClickListener(listener);
        customViewHolder.textView.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView textView;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.textView = (TextView) view.findViewById(R.id.title);
        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(MovieDb movieDb);
    }
}