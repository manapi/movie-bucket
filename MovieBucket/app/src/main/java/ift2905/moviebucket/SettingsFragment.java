package ift2905.moviebucket;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
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
        //TODO: THERE HAS TO BE A BETTER WAY
        Preference localePref = findPreference(KEY_LOCALE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        localePref.setSummary(summary(prefs.getString(KEY_LOCALE, "")));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String k) {
        if(k.equals(KEY_LOCALE)) {
            //Updates the summary
            //TODO: probably has to go.
            Preference localePref = findPreference(KEY_LOCALE);
            localePref.setSummary(summary(sharedPreferences.getString(KEY_LOCALE, "")));
            dbh.rebuild();
            // Notify change upon return to main activity
            getActivity().setResult(Activity.RESULT_OK);

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

    public String summary(String verifier) {
        if (verifier.equals("en")) {
            return getString(R.string.locale_en);
        } else {
            return getString(R.string.locale_fr);
        }
    }
}