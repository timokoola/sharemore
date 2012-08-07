package com.moarub.sharemoar;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class ShareMoarPreferenceFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);



	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
		
		Preference pref = findPreference("kippt_username");
		SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
		pref.setSummary(sharedPreferences.getString("kippt_username", ""));
		Preference pw = findPreference("kippt_token");
		pw.setSummary(sharedPreferences.getString("kippt_token", ""));
		
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key == null) {
			return;
		}

		if (key.equalsIgnoreCase("kippt_username")) {
			Preference pref = findPreference(key);
			pref.setSummary(sharedPreferences.getString(key, ""));
		} else if (key.equalsIgnoreCase("kippt_token")) {
			Preference pw = findPreference(key);
			pw.setSummary(sharedPreferences.getString(key, ""));
		}
	}

}
