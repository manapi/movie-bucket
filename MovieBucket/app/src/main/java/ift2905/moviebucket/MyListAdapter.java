package ift2905.moviebucket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class MyListAdapter extends BaseAdapter {

    String list[];
    public Context context;
    String listType;
    View.OnClickListener onClickManager;

    public MyListAdapter(String list[], Context context, View.OnClickListener onClickManager) {
        super();
        this.list = new String[list.length];
        for (int i = 0; i < list.length; i++) {
            this.list[i] = list[i];
        }
        this.context = context;

        this.onClickManager = onClickManager;
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            //TODO: add in stuff for myHistory (both here and in xml layout)


            //Inflating all the buttons
            convertView = li.inflate(R.layout.mylist_row_item_view, parent, false);

            ImageButton starStatus;

            //checking if the movie is "starred" and adjusting the button used
            if (list[position].substring(0, 1).equals("0")) {

                starStatus = (ImageButton) convertView.findViewById(R.id.notstarred);
            } else starStatus = (ImageButton) convertView.findViewById(R.id.starred);

            //Making the button visible. Can't use setImageDrawable because it requires API 21+
            LinearLayout.LayoutParams paramsStar = (LinearLayout.LayoutParams) starStatus.getLayoutParams();
            paramsStar.weight = 1.0f;
            starStatus.setLayoutParams(paramsStar);

            TextView myTitleText = (TextView) convertView.findViewById(R.id.mytitle);

            //Trimming the movie's title if it exceeds 20 characters. Result is a bit wonky if
            //title has less than 3-5 caps in it TODO: polish this nonsense
            if (list[position].length() >= 20) {

                String newText = list[position].substring(1, 17) + "...";
                myTitleText.setText(newText);
            } else myTitleText.setText(list[position].substring(1));

            myTitleText.setOnClickListener(onClickManager);

            ImageButton more = (ImageButton) convertView.findViewById(R.id.more);

            more.setOnClickListener(onClickManager);

        }

        return convertView;
    }
}
