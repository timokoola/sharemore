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
package com.moarub.kipptapi;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.moarub.kipptapi.ListsGetter.ListItem;
import com.moarub.util.ShareMoreUtils;

import android.os.AsyncTask;
import android.util.Log;

public class ListsGetter extends AsyncTask<String, Void, StringBuilder> {
	private JSONArray fLists;
	private ListsListener fListener;

	public ListsGetter(ListsListener listener) {
		fListener = listener;
	}
	
	@Override
	protected StringBuilder doInBackground(String... params) {
		String username = params[0];
		String password = params[1];
		String reqTokenUrl = "https://kippt.com/api/lists/";
		DefaultHttpClient client = new DefaultHttpClient();

		try {
			HttpGet request = new HttpGet(reqTokenUrl);
			request.addHeader("X-Kippt-Username", username);
			request.addHeader("X-Kippt-API-Token", password);

			HttpResponse lists = client.execute(request);
			return ShareMoreUtils.getResponseString(lists);
		} catch (ClientProtocolException e) {
			Log.d("ApiTokenFailure", "Can't fetch API Token");
			return null;
		} catch (IOException e) {
			Log.d("ApiTokenFailure", "Can't fetch API Token");
			return null;
		}
	}

	@Override
	protected void onPostExecute(StringBuilder result) {
		try {
			JSONObject jobj = (JSONObject) new JSONTokener(result.toString())
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
		Log.d("Result", result.toString());
		fListener.setListsReady();
	}

	public JSONArray getLists() {
		return fLists;
	}

	public void setLists(JSONArray fLists) {
		this.fLists = fLists;
	}

	public class ListItem {
		private String fName;
		private String fResourceUri;
		private int fId;
		
		public ListItem(String name, int id) {
			fName = name;
			fId = id;
		}
		
		public void setResourceUri(String rUri) {
			fResourceUri = rUri;
		}
		
		public int getId() {
			return fId;
		}
		
		@Override
		public String toString() {
			return fName;
		}

		public String getUri() {
			return fResourceUri;
		}
		
	}

	public ArrayList<ListItem> getListitems() {
			ArrayList<ListItem> result = new ArrayList<ListItem>();
			for(int i = 0; i < fLists.length(); i++) {
				ListItem item;
				try {
					item = new ListItem(fLists.getJSONObject(i).getString("title"), fLists.getJSONObject(i).getInt("id"));
					item.setResourceUri(fLists.getJSONObject(i).getString("resource_uri"));
				} catch (JSONException e) {
					item = new ListItem("error",0);
				}
				result.add(item);
			}
			return result;
	}
	
}
