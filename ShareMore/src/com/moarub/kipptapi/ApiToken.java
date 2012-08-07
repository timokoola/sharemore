package com.moarub.kipptapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class ApiToken extends AsyncTask<String, Void, HttpResponse> {
	private String fToken;
	private ApiTokenListener fListener;
	private String fUserName;
	
	public ApiToken(ApiTokenListener listener) {
		super();
		fListener = listener;
	}
	
	@Override
	protected HttpResponse doInBackground(String... u) {
		String username = u[0];
		String password = u[1];
		String reqTokenUrl = "https://kippt.com/api/account/";
		DefaultHttpClient client = new DefaultHttpClient();
		Credentials creds = new UsernamePasswordCredentials(username,password);
		client.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), creds);
		
        try {
            HttpGet request = new HttpGet(reqTokenUrl);
            
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
			JSONObject jobj = (JSONObject) new JSONTokener(builder.toString()).nextValue();
			fToken = jobj.getString("api_token");
			setUserName(jobj.getString("username"));
		} catch (JSONException e) {
			Log.d("ApiTokenFailure", "Can't fetch API Token");
			fListener.setAPIToken(false);
			return;
		}
		Log.d("Result", fToken);
		fListener.setAPIToken(true);
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

	public String getResult() {
		return fToken;
	}

	public void setResult(String fResult) {
		this.fToken = fResult;
	}

	public String getUserName() {
		return fUserName;
	}

	public void setUserName(String fUserName) {
		this.fUserName = fUserName;
	}
	
}
