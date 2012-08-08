/*******************************************************************************
 * Copyright (c) 2012 Moarub Oy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Moarub Oy - initial API and implementation
 ******************************************************************************/
package com.moarub.kipptapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import android.util.Log;

public class KipptAPIHelpers {

	public static StringBuilder getResponseString(HttpResponse result) {
		StringBuilder builder = new StringBuilder(""); 
		
		HttpEntity res = result.getEntity();
		InputStream is = null;
		try {
			is = res.getContent();
		} catch (IllegalStateException e) {
			Log.d("ResponseString", "Response invalid (IllegalStateException) " + e.getMessage());
			return builder;
		} catch (IOException e) {
			Log.d("ResponseString", "Response invalid (IoException)  " + e.getMessage());
			return builder;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				builder.append(line);
			}
		} catch (IOException e) {
			Log.d("ResponseString", "IOException " + e.getMessage());
			return builder;
		}
		return builder;
	}

}
