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
package com.moarub.sharemore;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.moarub.kipptapi.ApiTokenListener;
import com.moarub.kipptapi.KipptAPIToken;
import com.moarub.sharemore.R;


public class LoginActivity extends Activity implements ApiTokenListener, OnClickListener {

	private KipptAPIToken fAPIToken;
	private TextView fUsername;
	private TextView fPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		
		Button ok = (Button) findViewById(R.id.loginOkButton);
		ok.setOnClickListener(this);
		
		fUsername = (TextView) findViewById(R.id.loginUsername);
		fPassword = (TextView) findViewById(R.id.loginPassword);
	}
		
	
	private void fetchAPIToken(String un, String pw) {
		fAPIToken = new KipptAPIToken(this);
		String[] params = {un,pw};
		fAPIToken.execute(params);
	}

	@Override
	public void setAPIToken(boolean ready) {
		if(ready) {
			setPreferences();
			Toast.makeText(getApplicationContext(), "Authenticated" , Toast.LENGTH_SHORT).show();
			setResult(700);
			finish();
		} else {
			Toast.makeText(getApplicationContext(), "Authentication failed" , Toast.LENGTH_SHORT).show();
		}
	}

	private void setPreferences() {
		SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor shEdit = shPref.edit();
		shEdit.putString("kippt_username", fAPIToken.getUserName()).apply();
		shEdit.putString("kippt_token", fAPIToken.getResult()).apply();
	}

	@Override
	public void onClick(View v) {
		fetchAPIToken(fUsername.getText().toString(), fPassword.getText().toString());
	}



}
