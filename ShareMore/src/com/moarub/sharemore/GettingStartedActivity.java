package com.moarub.sharemore;

import android.app.Activity;
import android.os.Bundle;

public class GettingStartedActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GettingStartedView v = new GettingStartedView(this);
		setContentView(v);
	}
}
