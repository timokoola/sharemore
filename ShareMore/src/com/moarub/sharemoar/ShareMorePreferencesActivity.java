package com.moarub.sharemoar;

import android.app.Activity;
import android.os.Bundle;

public class ShareMorePreferencesActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new ShareMoarPreferenceFragment())
        .commit();
	}
	
	
}
