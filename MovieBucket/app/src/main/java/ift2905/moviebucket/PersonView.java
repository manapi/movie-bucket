package ift2905.moviebucket;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.people.PersonCredit;
import info.movito.themoviedbapi.model.people.PersonCredits;

public class PersonView extends AppCompatActivity {

    final String API_KEY = "93928f442ab5ac81f8c03b874f78fb94";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_view);

        int id = (int) getIntent().getExtras().getLong(AbstractResultsAdapter.Type.person.name());
        String name = getIntent().getExtras().getString("name");

        setTitle(name);

        if (id > 0) {
            FetchCredits mf = new FetchCredits();
            mf.execute(id + "");
        }

        ExpandableListView creditsList = (ExpandableListView) findViewById(R.id.creditsList);
    }



    public class FetchCredits extends AsyncTask<String, Object,  PersonCredits> {

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
                List<PersonCredit> crew = new ArrayList<>();

                for (PersonCredit c : credits.getCrew()) {
                    if (c.getJob().equals("Director")) {
                        crew.add(c);
                    }
                }

                List<PersonCredit> cast = credits.getCast();

                //TODO

            }
        }
    }

    /*public class CreditsExpandableAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<String> _listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<String, List<String>> _listDataChild;

        public CreditsExpandableAdapter(Context context, List<String> listDataHeader,
                                     HashMap<String, List<String>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final String childText = (String) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.search_view, null);
            }

            TextView txtListChild = (TextView) convertView
                    .findViewById(R.id.title);

            txtListChild.setText(childText);
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
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
                LayoutInflater infalInflater = (LayoutInflater) this._context
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
    }*/
}
