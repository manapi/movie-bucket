package ift2905.moviebucket;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_ADULT_PREF = "adult_filter";
    public static final String KEY_LOCALE = "locale";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String k) {
        if(k.equals(KEY_LOCALE)){
            Preference connectionPref = findPreference(k);
            connectionPref.setSummary(sharedPreferences.getString(k, ""));
        }
    }
}

