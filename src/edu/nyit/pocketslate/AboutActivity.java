/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslate;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
/**
 * 
 * @author jasonscott
 *
 */
public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		default:
			NavUtils.navigateUpFromSameTask(this);
			return super.onOptionsItemSelected(item);
		}

	}
}
