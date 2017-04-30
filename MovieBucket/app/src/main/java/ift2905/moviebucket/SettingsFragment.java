package ift2905.moviebucket;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;


public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_ADULT_PREF = "adult_filter";
    public static final String KEY_LOCALE = "locale";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String k) {
        if(k.equals(KEY_LOCALE)) {
            Toast.makeText(getActivity(), k, Toast.LENGTH_SHORT).show();
            Preference localePref = findPreference(k);
            //TODO: there has to be a better way...
            String verifier = (sharedPreferences.getString(k, ""));
            if (verifier.equals("en")) {
                localePref.setSummary("English");
            } else {
                localePref.setSummary("Français");
            }
        } else  if(k.equals(KEY_ADULT_PREF)) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SearchPagerFragment.adult = prefs.getBoolean(SettingsFragment.KEY_ADULT_PREF, false);
            Toast.makeText(getActivity(), SearchPagerFragment.adult.toString(), Toast.LENGTH_SHORT).show();
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