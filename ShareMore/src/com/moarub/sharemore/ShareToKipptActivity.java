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

import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.text.Editable;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.moarub.kipptapi.ClipCreatedListener;
import com.moarub.kipptapi.CreateClip;
import com.moarub.kipptapi.ListsGetter;
import com.moarub.kipptapi.ListsGetter.ListItem;
import com.moarub.kipptapi.ListsListener;
import com.moarub.util.UrlDeshortener;
import com.moarub.util.UrlDeshortenerListener;

public class ShareToKipptActivity extends Activity implements OnClickListener,
		ClipCreatedListener, UrlDeshortenerListener, ListsListener {
	protected String fUrlShared;
	protected String fTitle;
	private TextView fTitleView;
	private TextView fNoteView;
	private ConnectivityManager fConnectivityManager;
	private CheckBox fReadLater;
	private String fGeneratedNoteText;
	private UrlDeshortener fUrlDeshortener;
	private ListsGetter fListGetter;
	private boolean fIgnoreShortening;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sahare_moar);

		handleIntentInit();
		getLists();
		initViews();
	}

	private void getLists() {
		if(isCachedLists()) {
			fetctCachedLists();
		}
		fListGetter = new ListsGetter(this);
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String apiTokStr = preferences.getString("kippt_token", "");
		String apiTokUser = preferences.getString("kippt_username", "");

		String[] params = { apiTokUser, apiTokStr };

		fListGetter.execute(params);
	}

	private void fetctCachedLists() {
		// TODO later
	}

	private boolean isCachedLists() {
		// TODO later
		return false;
	}

	private void initViews() {
		TextView urlV = (TextView) findViewById(R.id.urlTextEditor);
		urlV.setText(fUrlShared);

		fTitleView = (TextView) findViewById(R.id.titleTextEditor);
		fTitleView.setText(fTitle);

		

		fNoteView = (TextView) findViewById(R.id.editTextNotes);
		if (fGeneratedNoteText != null) {
			fNoteView.setText(fGeneratedNoteText);
		}
	}


	private List<ListsGetter.ListItem> getListNames() {
		return fListGetter.getListitems();
	}

	protected void handleIntentInit() {
		Intent i = getIntent();

		if (i != null && i.getType() != null
				&& i.getType().equalsIgnoreCase("text/plain")) {
			Bundle extras = i.getExtras();
			fUrlShared = cleanAndLinkify(extras);
			fTitle = extras.getString("android.intent.extra.SUBJECT");
		}

		if (fUrlShared == null) {
			finishWithError(R.string.no_url_found_in_the_shared_text);
		} else if(fUrlShared.length() < 27) {
			fUrlDeshortener = new UrlDeshortener(this);
			fUrlDeshortener.execute(fUrlShared);
		}

		if (isOnline()) {
			fetchAPITokens();
		} else {
			finishWithError(R.string.inet_not_available);
		}
	}

	private String cleanAndLinkify(Bundle extras) {
		String urlCandidate = extras.getString("android.intent.extra.TEXT");
		if (urlCandidate != null && urlCandidate.startsWith("http://")) {
			return urlCandidate;
		} else {
			Editable str = Editable.Factory.getInstance().newEditable(
					urlCandidate);
			Linkify.addLinks(str, Linkify.WEB_URLS);
			URLSpan[] urls = str.getSpans(0, str.length(), URLSpan.class);
			if (urls == null || urls.length < 1) {
				return null;
			}
			URLSpan uspan = urls[0];
			fGeneratedNoteText = urlCandidate;
			
			return uspan.getURL();
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
		if(fUrlDeshortener != null) {
			fUrlDeshortener.cancel(true);
			fIgnoreShortening = true;
		}
		fTitle = fTitleView.getText().toString();
		doCreateClip(false, false);
	}

	public void doCreateClip(boolean readLater, boolean star) {
		CreateClip cl = new CreateClip(fUrlShared, this);

		cl.addTitle(fTitle);
		if (fGeneratedNoteText != null) {
			cl.addNote(fGeneratedNoteText);
		}
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
			Toast.makeText(getApplicationContext(),
					R.string.authentication_failed_password_changed,
					Toast.LENGTH_SHORT).show();
			callLoginActivity();
		} else {
			Toast.makeText(getApplicationContext(),
					R.string.error_creation + code, Toast.LENGTH_LONG).show();
		}
		finish();
	}

	public boolean isOnline() {
		fConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = fConnectivityManager.getActiveNetworkInfo();
		boolean result = ni != null && ni.isAvailable() && ni.isConnected();
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_read_later: {
			item.setChecked(!item.isChecked());
			item.setIcon(getIcon(item));
			return true;
		}
		case R.id.menu_star: {
			item.setChecked(!item.isChecked());
			item.setIcon(getIcon(item));
			return true;
		}
		case R.id.menu_send: {
			createClip();
		}
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	private Drawable getIcon(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_read_later: {
			return item.isChecked() ? getResources().getDrawable(
					R.drawable.ic_action_glasses) : getResources().getDrawable(
					R.drawable.ic_action_glasses_gray);
		}
		case R.id.menu_star: {
			return item.isChecked() ? getResources().getDrawable(
					R.drawable.ic_action_star_10) : getResources().getDrawable(
					R.drawable.ic_action_star_0);
		}
		default:
			return null;
		}
	}

	@Override
	public void onURLDeshortened(String resolution, int responseCode) {
		if(resolution != null && !fIgnoreShortening) {
			fUrlShared = resolution;
			TextView urlView = (TextView) findViewById(R.id.urlTextEditor);
			urlView.setText(fUrlShared);
			if(responseCode > 399) {
				fUrlDeshortener = null;
			}
			urlView.invalidate();
 		}
	}

	@Override
	public void onTitleUpdate(String newTitle) {
		if(newTitle != null && !fIgnoreShortening) {
			fTitleView.setText(newTitle);
			fTitleView.invalidate();
		}
	}

	@Override
	public void setListsReady() {
		Spinner ps = (Spinner) findViewById(R.id.ls_spinner);
		//ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
		//		this, R.array.lists, R.layout.spinner_item_lists);
		ArrayAdapter<ListsGetter.ListItem> adapter = new ArrayAdapter<ListsGetter.ListItem>(this, R.layout.spinner_item_lists, getListNames());
 		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ps.setAdapter(adapter);
	}

}
