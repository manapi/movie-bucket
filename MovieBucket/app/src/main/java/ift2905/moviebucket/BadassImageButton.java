package ift2905.moviebucket;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by christophe on 21/04/17.
 */

public class BadassImageButton extends android.support.v7.widget.AppCompatImageButton{

    private long movieId;
    private long mRuntime;
    public BadassImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMovieId(long id){
        this.movieId = id;
    }

    public long getMovieId(){
        return this.movieId;
    }

    public long getmRuntime(){
        return mRuntime;
    }

    public void setmRuntime(long runtime){
        this.mRuntime = runtime;
    }


}
