package ift2905.moviebucket;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;

public class Explore extends AppCompatActivity {

    private String[] titles = new String[5];

    ListView liste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        //Testing api to display content **to be replaced with proper behavior**
        FetchMovie fetcher = new FetchMovie();
        fetcher.execute();
        liste = (ListView) findViewById(R.id.list);

        // TODO: Read suggestions from local data base
        // TODO: Display suggestions on startup

        // TODO: Read text input from user, upon click search and display results

        // TODO: On click on list item, create MovieView activity from item

        // TODO: Add navigation drawer
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        // TODO: Save suggestions to local data base
    }

    public class SimpleListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 15;
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

            if(convertView == null) {
                convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
                Toast.makeText(Explore.this, "Inflate" + position, Toast.LENGTH_SHORT).show();
            }

            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);

            tv.setText(titles[position % 5]);

            return convertView;
        }
    }

    public class FetchMovie extends AsyncTask<String, Object, MovieDb[]> {

        @Override
        protected MovieDb[] doInBackground(String... params) {

            // Fetcher un film

            MovieDb[] movies = new MovieDb[5];

            final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";
            TmdbApi api = new TmdbApi(API_KEY);


            try {
                TmdbMovies dbmovies = api.getMovies();
                for(int i=0; i<5; i++)
                    movies[i] = dbmovies.getMovie(5353, "en");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return movies;
        }

        @Override
        protected void onPostExecute(MovieDb[] movies) {

            for(int i=0; i<5; i++)
                titles[i] = movies[i].getTitle();

            liste.setAdapter(new SimpleListAdapter());
        }
    }
}


