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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.moarub.db.ListItem;
import com.moarub.kipptapi.ClipCreatedListener;
import com.moarub.kipptapi.CreateClip;
import com.moarub.kipptapi.ListsGetter;
import com.moarub.kipptapi.ListsListener;
import com.moarub.util.UrlDeshortener;
import com.moarub.util.UrlDeshortenerListener;

public class ShareToKipptActivity extends Activity implements OnClickListener,
		ClipCreatedListener, UrlDeshortenerListener, ListsListener {
	private static final String SAVE_ALL_URLS = "Save all urls";
	protected String fUrlShared;
	protected ArrayList<String> fAllUrls = new ArrayList<String>();
	protected String fTitle;
	protected String fListUri;
	private TextView fTitleView;
	private TextView fNoteView;
	private ConnectivityManager fConnectivityManager;
	private String fGeneratedNoteText;
	protected HashMap<String, UrlDeshortener> fUrlDeshorteners = new HashMap<String, UrlDeshortener>();
	private ListsGetter fListGetter;
	private boolean fIgnoreShortening;
	private Spinner fListSpinner;
	private Spinner fUrlsSpinner;
	private boolean fStar;
	private boolean fReadLater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sahare_moar);
		fAllUrls.add(SAVE_ALL_URLS);

		handleIntentInit();
		getLists();
		initViews();
	}

	private void getLists() {
		fListGetter = new ListsGetter(this, this);
		if (isCachedLists()) {
			setupSpinner(fListGetter.getCachedListItems());
		}
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String apiTokStr = preferences.getString("kippt_token", "");
		String apiTokUser = preferences.getString("kippt_username", "");

		String[] params = { apiTokUser, apiTokStr };

		fListGetter.execute(params);
	}

	private boolean isCachedLists() {
		return fListGetter.isCachedListItems();
	}

	private void initViews() {
		fTitleView = (TextView) findViewById(R.id.titleTextEditor);
		fTitleView.setText(fTitle);

		fNoteView = (TextView) findViewById(R.id.editTextNotes);
		if (fGeneratedNoteText != null) {
			fNoteView.setText(fGeneratedNoteText);
		}
	}

	private List<ListItem> getListNames() {
		return fListGetter.getListitems();
	}

	protected void handleIntentInit() {
		if (isOnline()) {
			fetchAPITokens();
		} else {
			finishWithError(R.string.inet_not_available);
		}

		Intent i = getIntent();
		int urlCount = 0;

		if (i != null && i.getType() != null
				&& i.getType().equalsIgnoreCase("text/plain")) {
			Bundle extras = i.getExtras();
			urlCount = cleanAndLinkify(extras);
			fTitle = extras.getString("android.intent.extra.SUBJECT");
		}

		if (urlCount < 1) {
			finishWithError(R.string.no_url_found_in_the_shared_text);
		}

		for (String s : fAllUrls) {
			if (s.length() < 27 && !s.equals(SAVE_ALL_URLS)) { // Yes, it is a completely random cut-off
									// point
				UrlDeshortener us = new UrlDeshortener(this, 0);
				us.execute(s);
				fUrlDeshorteners.put(s, us);
			}
		}
	}

	public int cleanAndLinkify(Bundle extras) {
		String urlCandidate = extras.getString("android.intent.extra.TEXT");
		if (urlCandidate != null && urlCandidate.startsWith("http://")) {
			fAllUrls.add(urlCandidate);
		} else {
			Editable str = Editable.Factory.getInstance().newEditable(
					" " + urlCandidate);
			Linkify.addLinks(str, Linkify.WEB_URLS);
			URLSpan[] urls = str.getSpans(0, str.length(), URLSpan.class);
			if (urls == null || urls.length < 1) {
				return 0;
			}
			URLSpan uspan = urls[0];

			for (URLSpan us : urls) {
				fAllUrls.add(us.getURL());
			}

			setupUrlSpinner();

			fGeneratedNoteText = urlCandidate;
		}
		return fAllUrls.size();
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

	protected void createClips() {
		if (fUrlDeshorteners != null) {
			for (UrlDeshortener us : fUrlDeshorteners.values()) {
				if (us != null) {
					us.cancel(true);
				}
			}
			fIgnoreShortening = true;
		}
		fTitle = fTitleView.getText().toString();
		fListUri = fListSpinner.getSelectedItem() != null ? ((ListItem) fListSpinner
				.getSelectedItem()).getUri() : null;
		if (fUrlsSpinner.getSelectedItem() != null
				&& fUrlsSpinner.getSelectedItem().equals(SAVE_ALL_URLS)) {
			for (String u : fAllUrls) {
				if (!fUrlsSpinner.getSelectedItem().equals(SAVE_ALL_URLS)) {
					doCreateClip(u, fReadLater, fReadLater);
				}
			}
		} else if (fUrlsSpinner.getSelectedItem() != null) {
			doCreateClip((String) fUrlsSpinner.getSelectedItem(), fReadLater,
					fStar);
		}
	}

	public void doCreateClip(String url, boolean readLater, boolean star) {
		CreateClip cl = new CreateClip(url, this);

		cl.addTitle(fTitle);
		cl.addListUri(fListUri);
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
		createClips();
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
			fReadLater = item.isChecked();
			return true;
		}
		case R.id.menu_star: {
			item.setChecked(!item.isChecked());
			item.setIcon(getIcon(item));
			fStar = item.isChecked();
			return true;
		}
		case R.id.menu_send: {
			createClips();
		}
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	private Drawable getIcon(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_read_later: {
			return item.isChecked() ? getResources().getDrawable(
					R.drawable.ic_action_glasses_white) : getResources()
					.getDrawable(R.drawable.ic_action_glasses_gray);
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
	public void onURLDeshortened(String resolution, int responseCode,
			String original) {
		if (resolution != null && !fIgnoreShortening) {
			fAllUrls.set(fAllUrls.indexOf(original), resolution);
			setupUrlSpinner();
			if (responseCode > 399) {
				if (original != null && fUrlDeshorteners.containsKey(original)) {
					fUrlDeshorteners.get(original).cancel(true);
					fUrlDeshorteners.remove(original);
				}
			}

		}
	}

	@Override
	public void onTitleUpdate(String newTitle, String url) {
		if (newTitle != null && !fIgnoreShortening) {
			fTitleView.setText(newTitle);
			fTitleView.invalidate();
		}
	}

	@Override
	public void setListsReady() {
		setupSpinner(getListNames());
	}

	private void setupSpinner(List<ListItem> l) {
		fListSpinner = (Spinner) findViewById(R.id.ls_spinner);
		// ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
		// this, R.array.lists, R.layout.spinner_item_lists);
		ArrayAdapter<ListItem> adapter = new ArrayAdapter<ListItem>(this,
				R.layout.spinner_item_lists, l);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fListSpinner.setAdapter(adapter);
		fListSpinner.setSelection(0);
	}

	private void setupUrlSpinner() {
		fUrlsSpinner = (Spinner) findViewById(R.id.urlspinner);
		LinearLayout All = (LinearLayout) findViewById(R.id.sharemoresavetokippt);
		All.requestLayout();

		ArrayAdapter<String> urlsAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_item_lists, fAllUrls);
		urlsAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fUrlsSpinner.setAdapter(urlsAdapter);
		fUrlsSpinner.setSelection(0);
	}

}
