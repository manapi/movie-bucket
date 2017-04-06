package ift2905.moviebucket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;


public class MyBucket extends AppCompatActivity {

    final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";
    final String LANG = "en";
    final Boolean ADULT = false;

    private GoogleApiClient client;

    //adding a unique listener with a switch inside of it cuz the teacher thinks it's cool
    private View.OnClickListener BasedGodRockLobster = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            /*thinking of giving each button an unique Id following this structure: xxxn where
            xxx is a 3 letter tag with the button's role and n would be a number indicating the
            id of the movie object said button is linked to. Of course, I might change all this
            since I'm not so sure the view Id is an actual string...*/

            //bogus string until I confirm the nature of the info passed along by getId()
            String idTag = "bogus";

            String role = idTag.substring(0,3);

            switch (role) {
                case "fav":
                    /*TODO: updateFave();
                    this will change the star icon and update the movie's
                    favorite value in the local DB */
                    break;

                case "ttl":
                    /*TODO: collapse();
                     this will collapse the previously expanded movie item
                    TODO: expand();
                     this will expand the movie item that was clicked by
                     revealing the "calendar", "seen" and "delete" buttons*/
                    break;

                case "det":
                    /*creates an intent for the MovieView activity which will provide detailed
                    information about the movie that was clicked*/
                    moreInfo(Long.parseLong(idTag.substring(3)));
                    break;


                case "cal":
                    calendarShenanigans(Long.parseLong(idTag.substring(3)));
                    break;

                case "saw":
                    /*TODO: updateHistory();
                     queue up the currently expanded movie item to be removed
                     from MyBucket and added to MyHistory*/
                    break;
                case "del":
                    /*TODO: deleteMovie();
                     queue up currently expanded movie item to be deleted from MyBucket and
                     local DB
                     */

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bucket);

        /*TODO: fetch movie titles through asyncTask

          TODO: create a myBucketFragment or obtain it from the intent?

          TODO: create lotsa buttons

          TODO: bind all buttons to the BasedGodRockLobster

          TODO: inflate all them buttons
         */

    }

    //starts MovieView activity after passing along context and id of the selected movie
    public void moreInfo(long id){

        Intent intent = new Intent(this, MovieView.class);
        intent.putExtra("movie", id);
        startActivity(intent);

    }

    //starts calendar activity after passing along context and movie id
    public void calendarShenanigans(long id){
        /*TODO:
         -implement code to obtain movie title from the id
         */
        String title = "bogusString";

        //Intent intent = new Intent(this, scheduler.class);
        //intent.putExtra("title", title);
        //startActivity(intent);
    }

    //TODO: implement asyncTask

    //TODO: implement generic methods
}
