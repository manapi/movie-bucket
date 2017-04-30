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
        //TODO: THERE HAS TO BE A BETTER WAY
        Preference localePref = findPreference("locale");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        localePref.setSummary(summary(prefs.getString("locale", "")));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String k) {
        if(k.equals(KEY_LOCALE)) {
            Preference localePref = findPreference(k);
            //TODO: there has to be a better way...
            localePref.setSummary(summary(sharedPreferences.getString(k, "")));


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

    public String summary(String verifier) {
        if (verifier.equals("en")) {
            return "English";
        } else {
            return "Français";
        }
    }
}