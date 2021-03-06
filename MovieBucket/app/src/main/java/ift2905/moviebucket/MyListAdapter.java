package ift2905.moviebucket;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.provider.CalendarContract;
import android.support.v4.widget.CursorAdapter;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static ift2905.moviebucket.R.drawable.ic_star_black_24dp;
import static ift2905.moviebucket.R.drawable.ic_star_border_black_24dp;
import static ift2905.moviebucket.R.layout.mylist_row_item_view;

/**
 * Adapter for My Bucket and My History lists
 * Created by Amélie on 2017-04-09.
 */

public class MyListAdapter extends CursorAdapter{

    Cursor c;
    final String header;
    Context context;
    DBHandler dbh;

    //the "header" parameter is necessary since it specifies which list requires a Db query and stuff
    public MyListAdapter(String header, Context context, Cursor c) {
        super(context, c, 0);
        this.c = c;
        this.header = header;
        this.context = context;
        dbh = new DBHandler(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Initializes the view. Mandatory method from CursorAdapter.
        return LayoutInflater.from(context).inflate(mylist_row_item_view, parent, false);
    }

    @Override
    public void bindView(View view, Context ctx, Cursor cursor) {
        //Connects the cursor values to our View.

        //Grabs the ID of a row.
        long entryId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
        long mRuntime = cursor.getLong(cursor.getColumnIndexOrThrow("runtime"));

        //Title section
        IntuitiveTextView myTitleText = (IntuitiveTextView) view.findViewById(R.id.mytitle);
        final String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        myTitleText.setText(processTitle(title));

        myTitleText.setMovieId(entryId);

        myTitleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntuitiveTextView title = (IntuitiveTextView) v;
                Intent intent = new Intent(context, MovieView.class);
                Boolean isMovie = dbh.checkIfMovie(title.getMovieId());
                if (isMovie) {
                    intent.putExtra("movie", title.getMovieId());
                } else {
                    intent.putExtra("tv", title.getMovieId());
                }
                context.startActivity(intent);
            }
        });

        //Star section
        if(header.equals("Bucket")){
            final BadassImageButton starStatus;

            int fav = cursor.getInt(cursor.getColumnIndexOrThrow("favorite"));
            starStatus = (BadassImageButton) view.findViewById(R.id.star);

            if(fav == 0) {
                starStatus.setImageResource(ic_star_border_black_24dp);
            } else {
                starStatus.setImageResource(ic_star_black_24dp);
            }

            starStatus.setMovieId(entryId);
            LinearLayout.LayoutParams paramsStar = (LinearLayout.LayoutParams) starStatus.getLayoutParams();
            paramsStar.weight = 0.6f;
            starStatus.setLayoutParams(paramsStar);
            starStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dbh.swapFavoriteValue(starStatus.getMovieId());
                    updateCursor();
                    notifyDataSetChanged();
                }
            });
        }else if(header.equals("History")){

            BadassImageButton checkmark = (BadassImageButton) view.findViewById(R.id.checked);

            LinearLayout.LayoutParams paramsCheck = (LinearLayout.LayoutParams) checkmark.getLayoutParams();
            paramsCheck.weight = 0.6f;
            checkmark.setLayoutParams(paramsCheck);
        }

        //"More" Section
        BadassImageButton more = (BadassImageButton) view.findViewById(R.id.more);

        more.setMovieId(entryId);
        more.setmRuntime(mRuntime);


        more.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int[] location = new int[2];
                // Get the x, y location and store it in the location[] array
                // location[0] = x, location[1] = y.
                v.getLocationOnScreen(location);

                //Initialize the Point with x, and y positions
                Point point = new Point();
                point.x = location[0];
                point.y = location[1];

                View fakePopupMenuLayout;

                if (header.equals("Bucket")){
                    fakePopupMenuLayout = ((LayoutInflater)context.getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fake_popup_menu_bucket, null);
                }else{
                    fakePopupMenuLayout = ((LayoutInflater)context.getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fake_popup_menu_history, null);
                }

                // Creating the PopupWindow
                final PopupWindow ersatzPopupMenu = new PopupWindow(context);
                ersatzPopupMenu.setContentView(fakePopupMenuLayout);
                ersatzPopupMenu.setOutsideTouchable(true);
                ersatzPopupMenu.setFocusable(true);

                final long mId = ((BadassImageButton) v).getMovieId();
                final long mRuntime = ((BadassImageButton) v).getmRuntime();
                
                if(header.equals("Bucket")){
                    Button markAsViewed = (Button) fakePopupMenuLayout.findViewById(R.id.markAsViewed);
                    markAsViewed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dbh.markAsViewed(mId);
                            updateCursor();
                            notifyDataSetChanged();
                            ersatzPopupMenu.dismiss();
                        }
                    });
                }
                Button schedule = (Button) fakePopupMenuLayout.findViewById(R.id.schedule);
                schedule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar cal = Calendar.getInstance();
                        GregorianCalendar calDate = new GregorianCalendar();
                        Intent calIntent = new Intent(Intent.ACTION_EDIT);
                        calIntent.setType("vnd.android.cursor.item/event");
                        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                calDate.getTimeInMillis());
                        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                                calDate.getTimeInMillis() + mRuntime * 60 * 1000);
                        calIntent.putExtra(CalendarContract.Events.TITLE, context.getResources().getString(R.string.watch)+ " " + title);
                        ersatzPopupMenu.dismiss();
                        context.startActivity(calIntent);
                    }
                });

                Button delete = (Button) fakePopupMenuLayout.findViewById(R.id.delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbh.removeFromDB(mId);
                        updateCursor();
                        notifyDataSetChanged();
                        ersatzPopupMenu.dismiss();
                    }
                });

                //Showing the menu under the button, if possible
                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                float dpOffsetY = 48f;
                float fPixelsY = metrics.density * dpOffsetY;
                int OFFSET_Y = (int) (fPixelsY + 0.5f);
                ersatzPopupMenu.showAtLocation(fakePopupMenuLayout, Gravity.NO_GRAVITY, point.x, point.y + OFFSET_Y);
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

    //builds a new Db object with updated info and updates the associated cursor.
    // Required for refreshing view.
    public void updateCursor(){

        int pageNumber = 0;
        if(header.equals("History")) {
            pageNumber = 1;
        }
        Cursor cursor = dbh.movieLister(pageNumber);
        changeCursor(cursor);
    }
}

