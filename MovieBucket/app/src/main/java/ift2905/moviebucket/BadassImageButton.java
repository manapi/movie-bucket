package ift2905.moviebucket;

import android.content.Context;

/**
 * Created by christophe on 21/04/17.
 */

public class BadassImageButton extends android.support.v7.widget.AppCompatImageButton{

    private long movieId;

    public BadassImageButton(Context context){
        super(context);
    }

    public void setMovieId(long id){
        this.movieId = id;

    }

    public long getMovieId(){
        return this.movieId;

    }
}
