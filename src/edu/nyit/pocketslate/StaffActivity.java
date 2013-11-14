/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslate;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

		mArticleAdapter = new ItemListAdapter(this, mPocketDbHelper, "search");

		mArticleHeader = getLayoutInflater().inflate(R.layout.item_list_header, null, true);
		mArticleList.addHeaderView(mArticleHeader, null, false);
		mArticleHeaderText = (TextView)findViewById(R.id.item_header);

		mArticleList.setAdapter(mArticleAdapter);

		// The dash in pranavs is missing a space and won't split properly
		mName = mStaffMember.title;
//		if(mName.contains("Pranav")) {
//			mName = "Pranav Krishnamurthy";
//		} else {
//			// Split name and position
//			int end= mName.indexOf(" Ð ");
//			mName = mName.substring(0, end);
//		}
		// Split name and position
		int end= mName.indexOf(" Ð ");
		mName = mName.substring(0, end);
		
		mArticleHeaderText.setText("Articles written by " + mName);
		
		// If there is an image link start task to download bitmap
		if(mStaffMember.imageUrl != null) {
			new DownloadBitmapTask().execute(mStaffMember.imageUrl);
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
	 * 
	 * @author jasonscott
	 *
	 */
	private class DownloadBitmapTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected void onPreExecute() {
			mImage.setImageResource(R.drawable.ic_action_refresh);
		}

		@Override
		protected Bitmap doInBackground(String... url) {

			try {
				return BitmapFactory.decodeStream(downloadUrl(url[0]));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if(result != null) {
				mImage.setImageBitmap(result);
			}
		}

		/**
		 * Makes a connection to a given URL and returns InputStream
		 * @param urlString
		 * @return
		 * @throws IOException
		 */
		private InputStream downloadUrl(String urlString) throws IOException {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(5000 /* milliseconds */);
			conn.setConnectTimeout(5000/*milliseconds*/);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			InputStream stream = conn.getInputStream();
			return stream;
		}

	}
	
	//TODO Write click listener for article list
	private class ArticleClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			
		}
		
	}
}
