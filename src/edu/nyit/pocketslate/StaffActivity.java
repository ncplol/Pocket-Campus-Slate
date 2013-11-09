/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslate;

import edu.nyit.pocketslateUtils.ItemListAdapter;
import edu.nyit.pocketslateUtils.PocketSlateDbHelper;
import edu.nyit.pocketslateUtils.PocketSlateReaderContract.ItemEntry;
import static edu.nyit.pocketslate.Constants.*;
import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author jasonscott
 *
 */

//TODO Authors bio, image, and list of articles written.
public class StaffActivity extends Activity {
	
	// Instance Variables
	private ListView mArticleList;
	private TextView mTitle;
	private TextView mBio;
	private TextView mLink;
	private ImageView mImage;
	private PocketSlateDbHelper mPocketDbHelper;
	private Item mStaffMember;
	private String mDate;
	private ItemListAdapter mArticleAdapter;
	
	// Callback methods
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_staff);
		
		// Initialize from resources
		mArticleList = (ListView)findViewById(R.id.staff_articles_list);
		mTitle = (TextView)findViewById(R.id.staff_title);
		mBio = (TextView)findViewById(R.id.staff_bio);
		mLink = (TextView)findViewById(R.id.staff_link);
		mImage = (ImageView)findViewById(R.id.staff_image);
		
		// Get date from MainActivity's Intent to use as identifier for entry in database
		Bundle bundle = getIntent().getBundleExtra("staff");
		mDate = bundle.getString("pub_date");
		
		mPocketDbHelper =  new PocketSlateDbHelper(this);
		
		mStaffMember = mPocketDbHelper.getItem("staff", ItemEntry.COLUMN_NAMES[DATE], mDate);
		
		mTitle.setText(mStaffMember.title);

		Spanned spanned = Html.fromHtml(mStaffMember.content);
		mBio.setText(spanned);

		mLink.setText(mStaffMember.link);
		mLink.setMovementMethod(LinkMovementMethod.getInstance());
		
		mArticleAdapter = new ItemListAdapter(this, mPocketDbHelper, "search");
		mArticleList.setAdapter(mArticleAdapter);
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.staff, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		return super.onOptionsItemSelected(item);
	}
}
