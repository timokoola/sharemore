package com.moarub.sharemore;

import com.moarub.sharemore.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class QuickShareToKipptActivity extends ShareToKipptActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.empty_layout);
		handleIntentInit();
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		
		doCreateClip(preferences.getBoolean("quick_read_later", false), preferences.getBoolean("quick_star", false));
	}
}
