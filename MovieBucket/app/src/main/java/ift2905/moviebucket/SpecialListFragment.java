package ift2905.moviebucket;

import android.support.v4.app.ListFragment;



/*
 * Created by christophe on 23/04/17.
 */

public class SpecialListFragment extends ListFragment {

    @Override
    public void onResume() {
        super.onResume();
        //updates the listView by first updating the DB and associated cursor then notifies adapter
        ((MyListAdapter)this.getListAdapter()).updateCursor();
        ((MyListAdapter) this.getListAdapter()).notifyDataSetChanged();

    }

}