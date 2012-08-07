package com.moarub.kipptapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.util.Log;

public class ListsGetter extends AsyncTask<ApiToken, Void, HttpResponse> {
	private ApiToken fApiToken;
	private String fToken;
	private JSONArray fLists;

	@Override
	protected HttpResponse doInBackground(ApiToken... params) {
		fApiToken = params[0];
		String reqTokenUrl = "https://kippt.com/api/lists/";
		DefaultHttpClient client = new DefaultHttpClient();

		try {
			HttpGet request = new HttpGet(reqTokenUrl);
			request.addHeader("X-Kippt-Username", fApiToken.getUserName());
			request.addHeader("X-Kippt-API-Token", fApiToken.getResult());

			return client.execute(request);
		} catch (ClientProtocolException e) {
			Log.d("ApiTokenFailure", "Can't fetch API Token");
			return null;
		} catch (IOException e) {
			Log.d("ApiTokenFailure", "Can't fetch API Token");
			return null;
		}
	}

	@Override
	protected void onPostExecute(HttpResponse result) {
		StringBuilder builder = getResponseString(result);

		try {
			JSONObject jobj = (JSONObject) new JSONTokener(builder.toString())
					.nextValue();
			
			setLists(jobj.getJSONArray("objects"));
			
			for(int i = 0; i < fLists.length(); i++) {
				JSONObject job = fLists.getJSONObject(i);
				Log.d("ListsGetter", job.getString("app_url"));
			}
			
			
		} catch (JSONException e) {
			Log.d("ApiTokenFailure", "Can't fetch API Token");
			return;
		}
		Log.d("Result", builder.toString());
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

	public JSONArray getLists() {
		return fLists;
	}

	public void setLists(JSONArray fLists) {
		this.fLists = fLists;
	}

}
