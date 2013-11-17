/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslate;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import edu.nyit.pocketslateUtils.BitmapWorkerTask;
import edu.nyit.pocketslateUtils.PocketSlateDbHelper;
import edu.nyit.pocketslateUtils.BitmapWorkerTask.AsyncDrawable;
import edu.nyit.pocketslateUtils.PocketSlateReaderContract.ItemEntry;
import static edu.nyit.pocketslate.Constants.*;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author jasonscott
 *
 */
public class ArticleActivity extends Activity {
	//Instance Variables
	private ImageView mImage;
	private TextView mTitle;
	private TextView mCategory;
	private TextView mPubDate;
	private TextView mAuthor;
	private TextView mContent;
	private String mTable;
	private String mDate;
	private String mTableName;
	private PocketSlateDbHelper mPocketDbHelper;
	private Item mArticle;
	private MenuItem mSaveMenuItem;
	private boolean mIsSaved;
	private boolean mFromSearch;

	// Callback methods
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// Initialize Layouts
		mImage = (ImageView)findViewById(R.id.article_image);
		mTitle = (TextView)findViewById(R.id.article_title);
		mCategory = (TextView)findViewById(R.id.article_category);
		mPubDate = (TextView)findViewById(R.id.article_date);
		mAuthor = (TextView)findViewById(R.id.article_author);
		mContent = (TextView)findViewById(R.id.article_content);

		// Get bundle from MainActivity, need item's date and table name to query database
		Bundle bundle = getIntent().getBundleExtra("article");
		mDate = bundle.getString("pub_date");
		mTable = bundle.getString("table");

		// Is the calling activities open table search
		mFromSearch = mTable.equals("search") ? true : false;

		// Get article based on date from database
		mPocketDbHelper = PocketSlateDbHelper.getInstance(this);
		mArticle = mPocketDbHelper.getItem(mTable, ItemEntry.COLUMN_NAMES[DATE], mDate);

		// Convert article's category to match table names in database
		mTableName = mArticle.category.toLowerCase(Locale.US).replace(" ", "_");

		// Is the article saved
		mIsSaved = mArticle.saved != null ? true : false;

		// Set Layout values
		mTitle.setText(mArticle.title);
		mCategory.setText(mArticle.category);
		mPubDate.setText(mArticle.pubDate.substring(0, mArticle.pubDate.length() - 6));

		mAuthor.setText("by " + mArticle.author);

		Spanned spanned = Html.fromHtml(mArticle.content);
		mContent.setText(spanned);

		if(mArticle.imageUrl != null) {
			//			BitmapWorkerTask task = new BitmapWorkerTask(mImage, mArticle.imageUrl, 250, 250);
			//			task.execute(mArticle.imageUrl);
			loadBitmap(mImage, mArticle.imageUrl, 250, 250);
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
		getMenuInflater().inflate(R.menu.article, menu);
		mSaveMenuItem = menu.findItem(R.id.article_action_save);
		setMenuItem();
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.article_action_save:
			return mIsSaved ? removeSaved(item) : addSaved(item);
		case R.id.article_action_open_link:
			Intent browser = new Intent(Intent.ACTION_VIEW);
			browser.setData(Uri.parse(mArticle.link));
			startActivity(browser);
			return super.onOptionsItemSelected(item);
		default:
			NavUtils.navigateUpFromSameTask(this);
			return super.onOptionsItemSelected(item);
		}

	}

	/**
	 * Sets the save story menu item icon and title if the article is saved or not.
	 */
	private void setMenuItem() {
		int resId = mIsSaved ? R.drawable.ic_action_remove : R.drawable.ic_favorite;
		String title = mIsSaved ? "Remove" : " Save ";
		mSaveMenuItem.setIcon(resId);
		mSaveMenuItem.setTitle(title);
	}

	/**
	 * Adds an article to saved stories table of database.  Updates the articles saved field.
	 * @param item
	 * @return
	 */
	private boolean addSaved(MenuItem item) {
		mPocketDbHelper.updateItem(mTableName, ItemEntry.COLUMN_NAMES[SAVED], "saved", mArticle.pubDate);
		if(mFromSearch) {
			mPocketDbHelper.updateItem(mTable, ItemEntry.COLUMN_NAMES[SAVED], "saved", mArticle.pubDate);
		}
		mPocketDbHelper.addItem(mPocketDbHelper.getItem(mTableName, ItemEntry.COLUMN_NAMES[DATE], mArticle.pubDate), "saved_stories");
		mIsSaved = true;
		setMenuItem();
		Toast.makeText(this, "Added to Saved Stories", Toast.LENGTH_SHORT).show();
		return super.onOptionsItemSelected(item);
	}

	/**
	 *  Removes article from user's saved stories table of database.  Updates articles saved field.
	 * @param item
	 * @return
	 */
	private boolean removeSaved(MenuItem item) {
		mPocketDbHelper.updateItem(mTableName, ItemEntry.COLUMN_NAMES[SAVED], null, mArticle.pubDate);
		List<Item> savedArticles = mPocketDbHelper.getAllItems("saved_stories");
		int index = -1;
		for(int i=0; i<savedArticles.size();i++) {
			if(savedArticles.get(i).pubDate.equals(mArticle.pubDate)) {
				index = i;
			}
		}
		if(mPocketDbHelper.deleteItem("saved_stories", ItemEntry.COLUMN_NAMES[DATE], mDate)) {
			savedArticles.remove(index);
			mPocketDbHelper.deleteTable("saved_stories");
			for(int i=0; i<savedArticles.size(); i++) {
				mPocketDbHelper.addItem(savedArticles.get(i), "saved_stories");
			}
			mIsSaved = false;
			setMenuItem();
			Toast.makeText(this, "Removed from Saved Stories", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Failed to remove!", Toast.LENGTH_SHORT).show();
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 
	 * @param imageView
	 * @param url
	 * @param w
	 * @param h
	 */
	public void loadBitmap(ImageView imageView, String url, int w, int h) {

		final Bitmap bitmap = BitmapWorkerTask.getBitmapFromMemCache(url);
		if (bitmap != null) {
			mImage.setImageBitmap(bitmap);
		} else {
			//	        mImage.setImageResource(R.drawable.ic_action_refresh);
			//	        BitmapWorkerTask task = new BitmapWorkerTask(mImage, url, h, w);
			//	        task.execute(url);

			if (BitmapWorkerTask.cancelPotentialWork(url, imageView)) {
				final BitmapWorkerTask task = new BitmapWorkerTask(imageView, url, h, w);
				final AsyncDrawable asyncDrawable =
						new AsyncDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_refresh), task);
				imageView.setImageDrawable(asyncDrawable);
				task.execute(url);
			}
		}
	}

	//	/**
	//	 * 
	//	 * @param imageView
	//	 * @param url
	//	 * @param w
	//	 * @param h
	//	 */
	//	public void loadBitmap(ImageView imageView, String url, int w, int h) {
	//		if (BitmapWorkerTask.cancelPotentialWork(url, imageView)) {
	//			final BitmapWorkerTask task = new BitmapWorkerTask(imageView, url, h, w);
	//			final AsyncDrawable asyncDrawable =
	//					new AsyncDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_refresh), task);
	//			imageView.setImageDrawable(asyncDrawable);
	//			task.execute(url);
	//		}
	//	}
}
