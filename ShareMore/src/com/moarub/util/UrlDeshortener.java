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

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class UrlDeshortener extends AsyncTask<String, Void, DeshorteningResult> {
	private UrlDeshortenerListener fListener;
	private String fUrlTo;

	public UrlDeshortener(UrlDeshortenerListener listener) {
		fListener = listener;
	}

	@Override
	protected DeshorteningResult doInBackground(String... urls) {
		Log.d("Deshortening", "URL " + urls[0]);
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
		if(result != null && result.getResValue() != null) {
			
			fListener.onURLDeshortened(result.getResValue(),result.getStatusCode());
			Log.d("Deshortening", result.getResValue() + " (" + result.getStatusCode() + ")");
			
			if(result.getStatusCode() >= 300 && result.getStatusCode()  < 400) {
				UrlDeshortener followOn = new UrlDeshortener(fListener);
				followOn.execute(result.getResValue());
			}
			return;
		} else if(result != null) {
			int statusCode = result.getStatusCode();
			if(statusCode >= 200 && statusCode < 300 && fUrlTo != null) {
				extractTitle(fUrlTo);
			}
			return;
		}
		fListener.onURLDeshortened(null,700);
	}

	private void extractTitle(String url) {
		PageTitleGetter pgt = new PageTitleGetter(fListener);
		pgt.execute(url);
	}

}