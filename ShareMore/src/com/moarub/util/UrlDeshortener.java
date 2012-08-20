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
package com.moarub.util;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.params.HttpClientParams;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class UrlDeshortener extends AsyncTask<String, Void, DeshorteningResult> {
	private UrlDeshortenerListener fListener;
	private String fUrlTo;
	private int fRedirects;
	private boolean fAsync;

	public UrlDeshortener(UrlDeshortenerListener listener, int count) {
		fListener = listener;
		fRedirects = count;
	}

	@Override
	protected DeshorteningResult doInBackground(String... urls) {
		return doShortening(urls);
	}

	private DeshorteningResult doShortening(String... urls) {
		fAsync = true;
		Log.d("Deshortening", "URL " + urls[0] + "(" + fRedirects + ")");
		fUrlTo = urls[0];
		AndroidHttpClient httpClient = AndroidHttpClient
				.newInstance("Android ShareMore");

		HttpHead headReq = new HttpHead(fUrlTo);

		try {
			HttpResponse resp = httpClient.execute(headReq);
			String resValue = resp.getLastHeader("Location") != null ? resp
					.getLastHeader("Location").getValue() : null;
			int statusCode = resp.getStatusLine().getStatusCode();
			return new DeshorteningResult(resValue, statusCode);
		} catch (IOException e) {
			Log.d("Deshortening", e.getMessage());
			return null;
		} finally {
			httpClient.close();
		}
	}

	@Override
	protected void onPostExecute(DeshorteningResult result) {
		handleResponse(result);
	}

	private void handleResponse(DeshorteningResult result) {
		if (result != null && result.getResValue() != null && fRedirects < 20) {

			fListener.onURLDeshortened(result.getResValue(),
					result.getStatusCode());
			Log.d("Deshortening",
					result.getResValue() + " (" + result.getStatusCode() + ")");

			if (result.getStatusCode() >= 300 && result.getStatusCode() < 400) {
				UrlDeshortener followOn = new UrlDeshortener(fListener,
						fRedirects + 1);
				followOn.execute(result.getResValue());
			}
			return;
		} else if (result != null) {
			if (fUrlTo != null && fAsync) {
				extractTitle(fUrlTo);
			}
			return;
		}
		fListener.onURLDeshortened(null, 700);
	}

	private void extractTitle(String url) {
		PageTitleGetter pgt = new PageTitleGetter(fListener);
		String[] params = {url,url};
		pgt.execute(params);
	}

}
