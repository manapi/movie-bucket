package ift2905.moviebucket;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.View;

/**
 * Created by christophe on 21/04/17.
 */

public class GodlyPopupMenu extends PopupMenu {

    private long movieId;
    private long mRuntime;

    public GodlyPopupMenu(Context context, View anchor, long movieId, long mRuntime){
        super(context, anchor);

        //TODO: make this class able to use the setForceShowIcon(boolean) method
        //That's what's needed to make icons visible in a popup menu
        this.movieId = movieId;
        this.mRuntime = mRuntime;

    }
    /*
    public void setMovieId(long id){
        this.movieId = id;
    }
    */
    
    public long getMovieId(){
        return this.movieId;
    }

    public long getMRuntime(){
        return this.mRuntime;
    }
    
}
