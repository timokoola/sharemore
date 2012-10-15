package com.moarub.sharemore;

import java.util.Stack;

import com.moarub.kipptapi.ClipCreatedListener;
import com.moarub.kipptapi.CreateClip;
import com.moarub.util.UrlDeshortener;
import com.moarub.util.UrlDeshortenerListener;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.Toast;

public class SaveToKipptService extends IntentService implements
		UrlDeshortenerListener, ClipCreatedListener {
	private URLSpan[] fUrls;
	private String fTitle;
	private UrlDeshortener fUrlDeshortener;
	private String fGeneratedNoteText;
	private ConnectivityManager fConnectivityManager;
	
	
	public static class UrlItem {
		public String fUrl;
		public String fTitle;
		public String fNoteText;
	}
	
	private Stack<UrlItem> fUrlItems = new Stack<SaveToKipptService.UrlItem>();
	

	public SaveToKipptService() {
		super("SaveToKipptService");
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		Intent i = arg0;
		String urlCandidate = null;
		if (i != null && i.getType() != null
				&& i.getType().equalsIgnoreCase("text/plain")) {
			Bundle extras = i.getExtras();
			urlCandidate = extras.getString("android.intent.extra.TEXT");
			fUrls = cleanAndLinkify(" " + urlCandidate);
			if (fUrls == null && urlCandidate == null) {
				finishWithError(R.string.no_url_found_in_the_shared_text);
			} 
			if(fUrls != null) {
				fGeneratedNoteText = urlCandidate;
			}
			
			fTitle = extras.getString("android.intent.extra.SUBJECT");
		}

		if (fUrls != null) {

			for (int i1 = 0; i1 < fUrls.length; i1++) {
				UrlItem ui = new UrlItem();
				ui.fNoteText = fGeneratedNoteText;
				ui.fUrl = fUrls[i1].getURL();
				ui.fTitle = fTitle;
				fUrlItems.push(ui);
			}
			
			handleOneItem();
			
		} else {
			createClip(fTitle, urlCandidate);
		}

	}

	private void handleOneItem() {
		if(fUrlItems.size() == 0) {
			return;
		}
		UrlItem ui = fUrlItems.pop();
		UrlDeshortener ushort = new UrlDeshortener(this, 0);
		ushort.execute(ui.fUrl);
	}

	private void finishWithError(int resId) {
		Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_LONG)
				.show();
		stopSelf();
	}

	private URLSpan[] cleanAndLinkify(String urlCandidate) {
		if (urlCandidate != null && urlCandidate.startsWith("http://")) {
			return null;
		} else {
			Editable str = Editable.Factory.getInstance().newEditable(
					urlCandidate);
			Linkify.addLinks(str, Linkify.WEB_URLS);
			URLSpan[] urls = str.getSpans(0, str.length(), URLSpan.class);
			if (urls == null || urls.length < 1) {
				return null;
			}

			return urls;
		}
	}

	public boolean isOnline() {
		fConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = fConnectivityManager.getActiveNetworkInfo();
		boolean result = ni != null && ni.isAvailable() && ni.isConnected();
		return result;
	}

	@Override
	public void onURLDeshortened(String resolution, int responseCode, String original) {
		if (responseCode > 399 && fUrlDeshortener != null) {
			fUrlDeshortener.cancel(true);
			fUrlDeshortener = null;
		}

	}

	@Override
	public void onTitleUpdate(String newTitle, String url) {
		createClip(newTitle, url);
	}

	private void createClip(String newTitle, String url) {
		CreateClip cl = new CreateClip(url, this);

		cl.addTitle(newTitle);
		if (fGeneratedNoteText != null) {
			cl.addNote(fGeneratedNoteText);
		}
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		cl.setReadLater(preferences.getBoolean("quick_read_later", false));
		cl.setStar(preferences.getBoolean("quick_star", false));

		String apiTokStr = preferences.getString("kippt_token", "");
		String apiTokUser = preferences.getString("kippt_username", "");

		String[] params = { apiTokUser, apiTokStr };
		cl.execute(params);
	}

	@Override
	public void onClipCreated(int code) {
		if (code == 201) {
			Toast.makeText(getApplicationContext(), R.string.clip_created,
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(),
					R.string.error_creation + code, Toast.LENGTH_LONG).show();
		}
		handleOneItem();
	}

}
