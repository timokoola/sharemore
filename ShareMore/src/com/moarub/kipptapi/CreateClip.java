package com.moarub.kipptapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class CreateClip extends AsyncTask<String, Void, HttpResponse> {

	private String fClipUrl;
	private String fApiToken;
	private String fToken;
	private String fNote;
	private String fTitle;
	private ClipCreatedListener fListener;
	private boolean fReadLater;

	public CreateClip(String clip, ClipCreatedListener listener) {
		fClipUrl = clip;
		fListener = listener;
	}

	@Override
	protected HttpResponse doInBackground(String... params) {
		String username = params[0];
		String token = params[1];
		String reqTokenUrl = "https://kippt.com/api/clips/";
		DefaultHttpClient client = new DefaultHttpClient();

		try {
			HttpPost request = new HttpPost(reqTokenUrl);
			request.addHeader("X-Kippt-Username", username);
			request.addHeader("X-Kippt-API-Token", token);
			request.addHeader("X-Kippt-Client", "ShareMoar for Android,sharemoar@moarub.com,http://moarub.com/sharemoar");
			
			JSONObject job = new JSONObject();
			job.put("url", fClipUrl);
			if (fNote != null) {
				job.put("notes", fNote);
			}
			if (fTitle != null) {
				job.put("title", fTitle);
			}
			if(fReadLater) {
				job.put("is_read_later", true);
			}

			StringEntity se = new StringEntity(job.toString(), "UTF-8");
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			
			Log.d("Sending clip JSON", job.toString());

			request.setEntity(se);

			// request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			return client.execute(request);
		} catch (ClientProtocolException e) {
			Log.d("ApiTokenFailure", "Can't fetch API Token");
			return null;
		} catch (IOException e) {
			Log.d("ApiTokenFailure", "Can't fetch API Token");
			return null;
		} catch (JSONException e) {
			Log.d("ApiTokenFailure", "Can't fetch API Token");
			return null;
		}
	}

	@Override
	protected void onPostExecute(HttpResponse result) {
		StatusLine sl = result.getStatusLine();

		Log.d("CreateClip", sl.getStatusCode() + " " + sl.getReasonPhrase());
		Log.d("CreateClip", getResponseString(result).toString());

		fListener.onClipCreated(sl.getStatusCode());

	}

	private StringBuilder getResponseString(HttpResponse result) {
		fToken = "";
		StringBuilder builder = new StringBuilder(fToken);

		HttpEntity res = result.getEntity();
		InputStream is = null;
		try {
			is = res.getContent();
		} catch (IllegalStateException e) {
			Log.d("ApiTokenFailure", "Can't fetch API Token");
			return builder;
		} catch (IOException e) {
			Log.d("ApiTokenFailure", "Can't fetch API Token");
			return builder;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				builder.append(line);
			}
		} catch (IOException e) {
			Log.d("ApiTokenFailure", "Can't fetch API Token");
			return builder;
		}
		return builder;
	}

	public void addNote(String fNotes) {
		fNote = fNotes;
	}

	public void addTitle(String title) {
		fTitle = title;
	}

	public void setReadLater(boolean checked) {
		fReadLater = checked;
	}

}
