package ift2905.moviebucket;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;



public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    static final String KEY_ADULT_PREF = "adult_filter";
    static final String KEY_LOCALE = "locale";
    DBHandler dbh;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dbh = new DBHandler(getActivity());
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String k) {
        if(k.equals(KEY_LOCALE)) {
            dbh.rebuild();
            //TODO: refresh the Discover fragment here, end my misery Amé!

        } else  if(k.equals(KEY_ADULT_PREF)) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SearchPagerFragment.adult = prefs.getBoolean(SettingsFragment.KEY_ADULT_PREF, false);
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}