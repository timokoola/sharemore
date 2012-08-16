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

import com.moarub.sharemore.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;

public class QuickShareToKipptActivity extends ShareToKipptActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.empty_layout);
		handleIntentInit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	
	@Override
	public void setListsReady() {
		// Do nothing, on purpose
	}
	
	@Override
	public void onTitleUpdate(String newTitle) {
		if(fUrlDeshortener != null) {
			fUrlDeshortener.cancel(true);
			fUrlDeshortener = null;
		}
		fTitle = newTitle;
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		doCreateClip(preferences.getBoolean("quick_read_later", false), preferences.getBoolean("quick_star", false));

	}
	
	
}
