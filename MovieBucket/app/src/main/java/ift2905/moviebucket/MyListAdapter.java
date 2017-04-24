package ift2905.moviebucket;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static ift2905.moviebucket.R.layout.mylist_row_item_view;

// TODO: Expand adapter to include buttons, on click listeners, etc
//half-way there! need to add in the pop in menu when title is clicked
// TODO: should take MovieDb instead of Strings
    /*Easy change. However, still using a string array here for testing purposes. And then there's
    the problem of putting favorite and viewed data in the same array. Or one could use the chicken
    solution and change to constructor to take in multiple arrays.*/
//TODO: bugfix
    /*the app seems to forget previous elements after scrolling to the last 5-10 elements of a list
    probably has something to do with the layout used in the xml; maybe I should try using a
    ListView as the parent instead of a LinearLayout? Anyways, I'm leaving this here as a
    reminder to myself to fix that nonsense*/

/**
 * Adapter for My Bucket and My History lists
 * Created by Amélie on 2017-04-09.
 */

public class MyListAdapter extends CursorAdapter {

    Cursor c;
    String header;
    Context context;

    //TODO: see if the "to" parameter is  really necessary.
    public MyListAdapter(String to, Context context, Cursor c) {
        super(context, c, 0);
        this.c = c;
        this.header = to;
        this.context = context;
        this.notifyDataSetChanged();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Initializes the view. Mandatory method from CursorAdapter.
        return LayoutInflater.from(context).inflate(mylist_row_item_view, parent, false);
    }

    @Override
    public void bindView(View view, Context ctx, Cursor cursor) {
        //Connects the cursor values to our View.

        //Title section
        TextView myTitleText = (TextView) view.findViewById(R.id.mytitle);
        String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        myTitleText.setText(processTitle(title));

        //Star section
        BadassImageButton starStatus;


        int fav = cursor.getInt(cursor.getColumnIndexOrThrow("favorite"));
        if(fav == 0) {
            starStatus = (BadassImageButton) view.findViewById(R.id.notstarred);
        } else {
            starStatus = (BadassImageButton) view.findViewById(R.id.starred);
        }

        LinearLayout.LayoutParams paramsStar = (LinearLayout.LayoutParams) starStatus.getLayoutParams();
        paramsStar.weight = 1.0f;
        starStatus.setLayoutParams(paramsStar);

        //"More" Section
        BadassImageButton more = (BadassImageButton) view.findViewById(R.id.more);
        long entryId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
        more.setMovieId(entryId);

        more.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int viewId = v.getId();
                switch (viewId) {
                    case R.id.starred:

                        //TODO: from the ID, update the fav column of this row, then modify the image to notStarred.
                        break;

                    case R.id.notstarred:
                        break;

                    case R.id.more:
                        //TODO: find some way to add icons to popup menu, failing that, implement the menu some other way

                        BadassImageButton moreButton = (BadassImageButton) v;

                        GodlyPopupMenu popup = new GodlyPopupMenu(context, v, moreButton.getMovieId());
                        final long popupId = popup.getMovieId();
                        popup.getMenuInflater().inflate(R.menu.popup_menu_my_bucket, popup.getMenu());

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            public boolean onMenuItemClick(MenuItem item) {
                                switch(item.getItemId()) {
                                    case R.id.markAsViewed:
                                        //TODO: update the Viewed column of this row to 1, then update the list.
                                        break;
                                    case R.id.schedule:
                                        //TODO: plug Adele's snippet for Calendar here
                                        break;
                                    case R.id.delete:
                                        //TODO: Delete the row whose id is popupId
                                        break;
                                    case R.id.details:
                                        //TODO: plug Amelie's snippet that gets us in MovieView.
                                        break;
                                }

                                Toast.makeText(context, "You Clicked : " + popupId +", have a cookie!", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        });

                        popup.show();

                        break;

                    case R.id.mytitle:
                        //the code here creates a new intent for the Movie View activity and adds to it
                        //the selected movie's Movie Db id as an extra
                        break;
                }
            }
        });

    }

    public static String processTitle(String title) {
        if (title.length() > 20) {
            String newText = title.substring(0, 17) + "...";
            return newText;
        } else {
            return title;
        }
    }

    public void updateCursor(MainActivity activity){
        activity.updateDBHandler();
        Cursor cursor = activity.getDBHandler().movieLister(this.header);
        this.changeCursor(cursor);
    }

}
