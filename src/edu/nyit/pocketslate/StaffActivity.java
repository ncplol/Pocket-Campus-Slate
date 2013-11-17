/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslate;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import edu.nyit.pocketslateUtils.BitmapWorkerTask;
import edu.nyit.pocketslateUtils.ItemListAdapter;
import edu.nyit.pocketslateUtils.PocketSlateDbHelper;
import edu.nyit.pocketslateUtils.PocketSlateReaderContract.ItemEntry;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author jasonscott
 *
 */

//TODO Get articles written by staff member and apply to list view
public class StaffActivity extends Activity {

	// Instance Variables
	private ListView mArticleList;
	private TextView mTitle;
	private TextView mBio;
	private ImageView mImage;
	private PocketSlateDbHelper mPocketDbHelper;
	private Item mStaffMember;
	private int mPosition;
	private ItemListAdapter mArticleAdapter;
	private View mArticleHeader;						
	private TextView mArticleHeaderText;				
	private String mName;
	private String mTable;

	// Callback methods
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_staff);

		// Initialize from resources
		mArticleList = (ListView)findViewById(R.id.staff_articles_list);
		mTitle = (TextView)findViewById(R.id.staff_title);
		mBio = (TextView)findViewById(R.id.staff_bio);
		mImage = (ImageView)findViewById(R.id.staff_image);

		// Get date from MainActivity's Intent to use as identifier for entry in database
		Bundle bundle = getIntent().getBundleExtra("staff");
		mPosition = bundle.getInt("position");

		mPocketDbHelper =  PocketSlateDbHelper.getInstance(this);

		mStaffMember = mPocketDbHelper.getItem("staff", ItemEntry._ID, "" + mPosition);

		mTitle.setText(mStaffMember.title);

		Spanned spanned = Html.fromHtml(mStaffMember.content);
		mBio.setText(spanned);

		mName = mStaffMember.title;

		// Split name and position
		int end= mName.indexOf(" Ð ");
		mName = mName.substring(0, end);
		mTable = mName.toLowerCase(Locale.US).replace(" ", "_");

		mArticleAdapter = new ItemListAdapter(this, mPocketDbHelper, mTable);

		mArticleHeader = getLayoutInflater().inflate(R.layout.item_list_header, null, true);
		mArticleList.addHeaderView(mArticleHeader, null, false);
		mArticleHeaderText = (TextView)findViewById(R.id.item_header);

		mArticleList.setOnItemClickListener(new ArticleClickListener());
		mArticleList.setAdapter(mArticleAdapter);

		mArticleHeaderText.setText("Articles written by " + mName);

		// If there is an image link start task to download bitmap
		if(mStaffMember.imageUrl != null) {
			BitmapWorkerTask task = new BitmapWorkerTask(mImage, 200, 200);
			task.execute(mStaffMember.imageUrl);
		} else {
			mImage.setImageResource(R.drawable.splash_horizontal);
		}
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
		switch(item.getItemId()) {
		case R.id.staff_action_open_link:
			Intent browser = new Intent(Intent.ACTION_VIEW);
			browser.setData(Uri.parse(mStaffMember.link));
			startActivity(browser);
			return super.onOptionsItemSelected(item);
		default:
			NavUtils.navigateUpFromSameTask(this);
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Gets article from database based on user selection.  Packages items fields into a bundle.
	 * Creates an Intent and puts the Bundle in it.  Starts ItemActivity
	 * @param position - position of user selection
	 */
	private void selectArticle(int position) {
		Item item = mPocketDbHelper.getItem(mTable, ItemEntry._ID, "" + position);

		Bundle bundle = new Bundle();
		bundle.putString("pub_date", item.pubDate);
		bundle.putString("table", mTable);

		Intent itemActivityIntent = new Intent(this, ArticleActivity.class);
		itemActivityIntent.putExtra("article", bundle);
		startActivity(itemActivityIntent);
	}

	/**
	 * 
	 * @author jasonscott
	 *
	 */
	private class ArticleClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View views, int position,
				long id) {
			selectArticle(position);
		}

	}
}
