/*******************************************************************************
 * Copyright (c) 2012 Moarub Oy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Moarub Oy - initial API and implementation
 ******************************************************************************/
package com.moarub.sharemoar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.moarub.kipptapi.ClipCreatedListener;
import com.moarub.kipptapi.CreateClip;

public class ShareToKipptActivity extends Activity implements OnClickListener,
		ClipCreatedListener {
	protected String fUrlShared;
	protected String fTitle;
	private TextView fTitleView;
	private ConnectivityManager fConnectivityManager;
	private CheckBox fReadLater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sahare_moar);

		handleIntentInit();
		initViews();
	}

	private void initViews() {
		TextView urlV = (TextView) findViewById(R.id.editText1);
		urlV.setText(fUrlShared);

		fTitleView = (TextView) findViewById(R.id.editText3);
		fTitleView.setText(fTitle);

		Button b = (Button) findViewById(R.id.button1);
		b.setOnClickListener(this);

		fReadLater = (CheckBox) findViewById(R.id.checkBox1);
	}

	protected void handleIntentInit() {
		Intent i = getIntent();

		if (i != null && i.getType() != null
				&& i.getType().equalsIgnoreCase("text/plain")) {
			Bundle extras = i.getExtras();
			Log.d("Received",
					extras != null
							&& extras.getString("android.intent.extra.TEXT") != null ? extras
							.getString("android.intent.extra.TEXT")
							: "No extra text");
			fUrlShared = extras.getString("android.intent.extra.TEXT");
			fTitle = extras.getString("android.intent.extra.SUBJECT");
		}

		if (isOnline()) {
			fetchAPITokens();
		} else {
			finishWithError(R.string.inet_not_available);
		}
	}

	private void finishWithError(int resId) {
		Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_LONG)
				.show();
		finish();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_sahare_moar, menu);
		return true;
	}

	protected void createClip() {
		fTitle = fTitleView.getText().toString();
		doCreateClip(fReadLater.isChecked(), false);
	}

	public void doCreateClip(boolean readLater, boolean star) {
		CreateClip cl = new CreateClip(fUrlShared, this);

		cl.addTitle(fTitle);
		cl.setReadLater(readLater);
		cl.setStar(star);

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String apiTokStr = preferences.getString("kippt_token", "");
		String apiTokUser = preferences.getString("kippt_username", "");

		String[] params = { apiTokUser, apiTokStr };
		cl.execute(params);
	}

	@Override
	public void onClick(View v) {
		createClip();
	}

	@Override
	public void onClipCreated(int code) {
		if (code == 201) {
			Toast.makeText(getApplicationContext(), R.string.clip_created,
					Toast.LENGTH_SHORT).show();
		} else if (code == 401) {
			Toast.makeText(getApplicationContext(), R.string.authentication_failed_password_changed,
					Toast.LENGTH_SHORT).show();
			callLoginActivity();
		} else {
			Toast.makeText(getApplicationContext(), R.string.error_creation + code,
					Toast.LENGTH_LONG).show();
		}
		finish();
	}

	public boolean isOnline() {
		fConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = fConnectivityManager.getActiveNetworkInfo();
		boolean result = ni != null && ni.isAvailable() && ni.isConnected();
		return result;
	}

}
