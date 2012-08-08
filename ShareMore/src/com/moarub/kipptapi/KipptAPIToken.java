package com.moarub.kipptapi;

import java.io.IOException;

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

import android.os.AsyncTask;
import android.util.Log;

public class KipptAPIToken extends AsyncTask<String, Void, HttpResponse> {
	private String fToken;
	private ApiTokenListener fListener;
	private String fUserName;
	public static final String reqTokenUrl = "https://kippt.com/api/account/";
	
	public KipptAPIToken(ApiTokenListener listener) {
		super();
		fListener = listener;
	}
	
	@Override
	protected HttpResponse doInBackground(String... u) {
		String username = u[0];
		String password = u[1];
		DefaultHttpClient client = new DefaultHttpClient();
		Credentials creds = new UsernamePasswordCredentials(username,password);
		client.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), creds);
		
        try {
            HttpGet request = new HttpGet(reqTokenUrl);
            
			return client.execute(request);
        } catch (ClientProtocolException e) {
        	Log.d("ApiTokenFailure", "Can't fetch API Token " + e.getMessage());
			return null;
        } catch (IOException e) {
            Log.d("ApiTokenFailure", "Can't fetch API Token " + e.getMessage());
            return null;
        }        
	}

	@Override
	protected void onPostExecute(HttpResponse result) {
		StringBuilder builder = KipptAPIHelpers.getResponseString(result);
		
		try {
			JSONObject jobj = (JSONObject) new JSONTokener(builder.toString()).nextValue();
			fToken = jobj.getString("api_token");
			setUserName(jobj.getString("username"));
		} catch (JSONException e) {
			Log.d("ApiTokenFailure", "Can't fetch API Token (Broken JSON) " + e.getMessage());
			fListener.setAPIToken(false);
			return;
		}
		Log.d("Result", fToken);
		fListener.setAPIToken(true);
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
