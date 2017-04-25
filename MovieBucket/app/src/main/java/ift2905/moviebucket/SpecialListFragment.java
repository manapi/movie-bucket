package ift2905.moviebucket;

import android.support.v4.app.ListFragment;

/**
 * Created by christophe on 23/04/17.
 */

public class SpecialListFragment extends ListFragment {

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) this.getActivity();
        ((MyListAdapter)this.getListAdapter()).updateCursor(activity);
        ((MyListAdapter) this.getListAdapter()).notifyDataSetChanged();
    }
}