/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslate;

import java.util.List;
import java.util.Locale;

import edu.nyit.pocketslateUtils.PocketSlateDbHelper;
import edu.nyit.pocketslateUtils.PocketSlateReaderContract.ItemEntry;
import static edu.nyit.pocketslate.Constants.*;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <p>Title: ItemActivity.java</p>
 * <p>Description: </p>
 * @author jasonscott
 *
 */
public class ItemActivity extends Activity {
	//Instance Variables
	private ImageView mImage;
	private TextView mTitle;
	private TextView mCategory;
	private TextView mPubDate;
	private TextView mContent;
	private TextView mLink;
	private String mTable;
	private String mDate;
	private String mTableName;
	private PocketSlateDbHelper mPocketDbHelper;
	private Item mItem;
	private MenuItem mSaveMenuItem;
	private boolean mIsSaved;
	private boolean mFromSearch;

	//Callback Methods
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item);

		getActionBar().setDisplayHomeAsUpEnabled(true); 
		getActionBar().setHomeButtonEnabled(true);

		// Initialize Layouts
		mImage = (ImageView)findViewById(R.id.item_image);
		mTitle = (TextView)findViewById(R.id.item_title);
		mCategory = (TextView)findViewById(R.id.item_category);
		mPubDate = (TextView)findViewById(R.id.item_pubdate);
		mContent = (TextView)findViewById(R.id.item_content);
		mLink = (TextView)findViewById(R.id.item_link);
		
		// Get bundle from MainActivity, need item's date and table name for searching
		Bundle bundle = getIntent().getBundleExtra("item");
		mDate = bundle.getString("pub_date");
		mTable = bundle.getString("table");

		// If the calling activities open table is search
		mFromSearch = mTable.equals("search") ? true : false;
		
		// Get item based on date from database
		mPocketDbHelper = PocketSlateDbHelper.getInstance(this);
		mItem = mPocketDbHelper.getItem(mTable, ItemEntry.COLUMN_NAMES[DATE], mDate);
		
		// Convert category to lower case replacing " " with "_" to match corresponding table name
		mTableName = mItem.category.toLowerCase(Locale.US).replace(" ", "_");
		
		// Is the item saved
		mIsSaved = mItem.saved != null ? true : false;

		// Set Layout values
		mImage.setImageResource(R.drawable.item_image_testing);
		mTitle.setText(mItem.title);
		mCategory.setText(mItem.category);
		if(mDate != null && mDate.length() > 6) {
			mPubDate.setText(mDate.substring(0,mDate.length()-6));
		}
		//mAuthor.setText(mItem.title);

		Spanned spanned = Html.fromHtml(mItem.content);
		mContent.setText(spanned);

		mLink.setText(mItem.link);
		mLink.setMovementMethod(LinkMovementMethod.getInstance());
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
		getMenuInflater().inflate(R.menu.item, menu);
		mSaveMenuItem = menu.findItem(R.id.item_action_save);
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
		case R.id.item_action_search:
			//TODO Search this article
			return super.onOptionsItemSelected(item);
		case R.id.item_action_save:
			return mIsSaved ? removeFavorite(item) : addFavorite(item);
		case R.id.item_action_share:
			//TODO Share article link through Facebook, Twitter, E-Mail, Text, etc.
			return super.onOptionsItemSelected(item);
		default:
			NavUtils.navigateUpFromSameTask(this);
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Sets the save story menu item icon and title based on whether article is saved or not.
	 */
	public void setMenuItem() {
		int resId = mIsSaved ? R.drawable.ic_action_remove : R.drawable.ic_favorite;
		String title = mIsSaved ? "Remove" : " Save ";
		mSaveMenuItem.setIcon(resId);
		mSaveMenuItem.setTitle(title);
	}

	/**
	 * 
	 * @param item
	 * @return
	 */
	public boolean addFavorite(MenuItem item) {
		mPocketDbHelper.updateItem(mTableName, ItemEntry.COLUMN_NAMES[SAVED], "saved", mItem.pubDate);
		if(mFromSearch) {
			mPocketDbHelper.updateItem(mTable, ItemEntry.COLUMN_NAMES[SAVED], "saved", mItem.pubDate);
		}
		mPocketDbHelper.addItem(mPocketDbHelper.getItem(mTableName, ItemEntry.COLUMN_NAMES[DATE], mItem.pubDate), "saved_stories");
		mIsSaved = true;
		setMenuItem();
		Toast.makeText(this, "Added to Saved Stories.", Toast.LENGTH_SHORT).show();
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Removes an article from user's favorite table of database.
	 * @param item
	 * @return
	 */
	public boolean removeFavorite(MenuItem item) {
		mPocketDbHelper.updateItem(mTableName, ItemEntry.COLUMN_NAMES[SAVED], null, mItem.pubDate);
		List<Item> items = mPocketDbHelper.getAllItems("saved_stories");
		int index = -1;
		for(int i=0;i<items.size();i++) {
			if(items.get(i).pubDate.equals(mItem.pubDate)) {
				index = i;
			}
		}
		//int index = items.indexOf(mItem);
		if(mPocketDbHelper.deleteItem("saved_stories", ItemEntry.COLUMN_NAMES[DATE], mDate)) {
			items.remove(index);
			mPocketDbHelper.deleteTable("saved_stories");
			for(int i=0; i<items.size();i++) {
				mPocketDbHelper.addItem(items.get(i), "saved_stories");
			}
			mIsSaved = false;
			setMenuItem();
			Toast.makeText(this, "Removed from Saved Stories", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Failed to remove!", Toast.LENGTH_SHORT).show();
		}
		return super.onOptionsItemSelected(item);
	}
}

