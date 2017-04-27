package ift2905.moviebucket;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.people.PersonCredit;
import info.movito.themoviedbapi.model.people.PersonCredits;

public class PersonView extends AppCompatActivity {

    final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";
    protected ExpandableListView creditsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        int id = (int) getIntent().getExtras().getLong(AbstractResultsAdapter.Type.person.name());
        String name = getIntent().getExtras().getString("name");

        if(name != null) {
            setTitle(name);
        }

        creditsList = (ExpandableListView) findViewById(R.id.credisList);

        if (id > 0) {
            FetchCredits mf = new FetchCredits();
            mf.execute(id + "");
        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private class FetchCredits extends AsyncTask<String, Object, PersonCredits> {

        @Override
        protected PersonCredits doInBackground(String... params) {

            if (params[0] != null) {
                int id = new Integer(params[0]);
                TmdbApi api = new TmdbApi(API_KEY);
                return api.getPeople().getPersonCredits(id);
            }
            return null;
        }

        @Override
        protected void onPostExecute(PersonCredits credits) {
            if (credits != null) {
                List<String> jobs = new ArrayList<>();
                HashMap<String, List<PersonCredit>> creds = new HashMap<>();

                List<PersonCredit> crew = credits.getCrew();

                if(!crew.isEmpty()) {
                    for (PersonCredit c : crew) {
                        if (!jobs.contains(c.getJob())) {
                            jobs.add(c.getJob());
                            creds.put(c.getJob(), new ArrayList<PersonCredit>());
                        }
                        creds.get(c.getJob()).add(c);
                    }
                }

                List<PersonCredit> cast = credits.getCast();

                if(!cast.isEmpty()) {
                    jobs.add("Actor");
                    creds.put("Actor", cast);
                }

                CreditsExpandableAdapter adapter = new CreditsExpandableAdapter(getApplication(), jobs, creds);
                creditsList.setAdapter(adapter);

                for(int i=0; i < adapter.getGroupCount(); i++)
                    creditsList.expandGroup(i);
            }
        }
    }

    private class CreditsExpandableAdapter extends BaseExpandableListAdapter {

        final String BASE_URL = "http://image.tmdb.org/t/p/";
        final String SIZE_SMALL = "w154";

        private Context context;
        private List<String> listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<String, List<PersonCredit>> listDataChild;

        public CreditsExpandableAdapter(Context context, List<String> listDataHeader,
                                        HashMap<String, List<PersonCredit>> listChildData) {
            this.context = context;
            this.listDataHeader = listDataHeader;
            this.listDataChild = listChildData;

        }

        @Override
        public PersonCredit getChild(int groupPosition, int childPosititon) {
            return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosition).getMovieId();
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final PersonCredit child = (PersonCredit) getChild(groupPosition, childPosition);

            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null)
                convertView = li.inflate(R.layout.search_view, parent, false);

            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView release = (TextView) convertView.findViewById(R.id.release);
            TextView type = (TextView) convertView.findViewById(R.id.type);
            ImageView image = (ImageView) convertView.findViewById(R.id.image);

            type.setText("Movie");
            type.setTextColor(Color.parseColor("#919191"));
            type.setTypeface(null, Typeface.BOLD);

            String year = child.getReleaseDate();
            if(year != null && year.length() >= 4) {
                year = year.substring(0, 4);
            }

            title.setText(child.getMovieTitle());
            release.setText(year);

            String url = child.getPosterPath();
            if(url != null) {
                url = BASE_URL + SIZE_SMALL + url;
            }

            Picasso.with(context)
                    .load(url)
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(image);

            convertView.setOnClickListener(new View.OnClickListener() {

                // Create a new activity (detailed view of the selected movie)
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MovieView.class);
                    intent.putExtra(AbstractResultsAdapter.Type.movie.name(), getChildId(groupPosition, childPosition));
                    context.startActivity(intent);
                }
            });
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                    .size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this.listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_group, null);
            }

            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.listHeader);
            lblListHeader.setText(headerTitle);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

}
