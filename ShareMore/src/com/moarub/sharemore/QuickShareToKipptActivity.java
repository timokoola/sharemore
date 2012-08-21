/*******************************************************************************
 * Copyright (c) 2012 Moarub Oy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Moarub Oy - initial API and implementation
 ******************************************************************************/
package com.moarub.sharemore;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.widget.Toast;

public class QuickShareToKipptActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Class.forName("android.os.AsyncTask");
		} catch (ClassNotFoundException e) {
		} // Hack, see
			// http://stackoverflow.com/questions/4280330/onpostexecute-not-being-called-in-asynctask-handler-runtime-exception
		fetchAPITokens();
		Toast.makeText(getApplicationContext(), R.string.saving,
				Toast.LENGTH_SHORT).show();
		Intent intent = getIntent();

		Intent service = new Intent(this, QuickSaveToKipptService.class);
		service.putExtras(intent);
		service.setType(intent.getType());
		startService(service);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	private void fetchAPITokens() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String apiTokStr = preferences.getString("kippt_token", "");

		if (apiTokStr.equalsIgnoreCase("apitoken")
				|| apiTokStr.equalsIgnoreCase("")) {
			callLoginActivity();
		}
	}

	private void callLoginActivity() {
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		startActivityForResult(intent, 700);
	}

}
