package com.moarub.util;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class PageTitleGetter extends AsyncTask<String, Void, StringBuilder> {
	private UrlDeshortenerListener fListener;

	public PageTitleGetter(UrlDeshortenerListener listener) {
		fListener = listener;
	}

	@Override
	protected StringBuilder doInBackground(String... urls) {
		Log.d("Resolving title", "URL " + urls[0]);
		String urlTo = urls[0];
		AndroidHttpClient httpClient = AndroidHttpClient
				.newInstance("Android ShareMore");

		HttpGet headReq = new HttpGet(urlTo);

		try {
			HttpResponse result = httpClient.execute(headReq);
			return ShareMoreUtils.getResponseString(result);
		} catch (IOException e) {
			Log.d("Resolving title Exception", e.getMessage());
			return null;
		} finally {
			httpClient.close();
		}
	}

	@Override
	protected void onPostExecute(StringBuilder result) {
		Log.d("Resolving title", "Ready");
		if (result != null) {
				String newTitle = result.toString();
				newTitle.replaceAll("\\s+", " ");
				Pattern titleP = Pattern.compile("<title>(.*?)</title>");
				Matcher m = titleP.matcher(newTitle);
				while(m.find()) {
					fListener.onTitleUpdate(m.group(1).trim());
				}
		}
	}

	
}
