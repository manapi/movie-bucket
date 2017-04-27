package ift2905.moviebucket;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;



/**
 * Created by christophe on 27/04/17.
 */

public class IntuitiveTextView extends AppCompatTextView {

    private long movieId;
    public IntuitiveTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMovieId(long id){
        this.movieId = id;
    }

    public long getMovieId(){
        return this.movieId;
    }
}
