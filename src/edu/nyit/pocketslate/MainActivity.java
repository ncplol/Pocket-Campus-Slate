/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslate;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParserException;

import edu.nyit.pocketslateExceptions.BuildDateException;
import edu.nyit.pocketslateUtils.ItemListAdapter;
import edu.nyit.pocketslateUtils.MenuListAdapter;
import edu.nyit.pocketslateUtils.OrganizationsXmlParser;
import edu.nyit.pocketslateUtils.PocketSlateDbHelper;
import edu.nyit.pocketslateUtils.PocketSlateReaderContract.ItemEntry;
import edu.nyit.pocketslateUtils.PocketSlateXmlParser;
import static edu.nyit.pocketslate.Constants.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * <p>Title: MainActivity.java</p>
 * <p>Description: Applications main Activity.  Manages entire application.</p>
 * @author jasonscott
 *
 */
public class MainActivity extends Activity {
	private DrawerLayout mDrawerLayout;				// Layout for sliding menu
	private ListView mDrawerList;					// ListView containing menu items in sliding menu
	private MenuListAdapter mDrawerListAdapter;		// Adapter for mDrawerList ListView
	private ActionBarDrawerToggle mDrawerToggle;	

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	private boolean mRefresh;						// Flag to refresh the content or not
	private String mOpenTable = "top_stories";		// Table name for the database

	private ProgressDialog mProgressDialog;			// To show the user content is loading/refreshing

	private PocketSlateDbHelper mPocketDbHelper;	// For storing and retrieving all items
	private PocketSlateXmlParser mPocketXmlParser;	// Parser for campus slate RSS feed

	private ListView mItemList;						// ListView of articles/items
	private View mItemHeader;						// Header View for mItemList ListView
	private TextView mItemHeaderText;				// TextView of header View
	private ItemListAdapter mItemListAdapter;		// Adapter for mItemList

	private Date mLastBuild ;						// Date and time of last build of the RSS feed
	private long mLastBuildLong;					// Time in milliseconds of last build since January 1st, 1970

	private long mLastRefreshLong;					// Time in milliseconds of last user refresh since January 1st, 1970

	private OrganizationsXmlParser mOrgsXmlParser;  // Parsing RSS feed from assets

	private SharedPreferences mPrefs;				// User preferences used to save application information

	private SearchView mSearchView;					// SearchView for searching applications database

	//TODO How to populate listview in StaffActivity for articles written by staff member, new Adapter

	//TODO Layout for items with no image

	//TODO Downloading and storing bitmaps, loading symbol in place of image until downloaded

	//TODO Memory and Disk Cache for bitmaps

	//TODO SettingsActivity

	// Callback Methods
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		mDrawerList = (ListView)findViewById(R.id.drawer_list);

		createDrawerMenu();

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View view) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};

		// Initialize application UI and utilities
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mItemList = (ListView)findViewById(R.id.item_list);
		mItemList.setOnItemClickListener(new ItemListClickListener());

		mPocketDbHelper = PocketSlateDbHelper.getInstance(this);
		mPocketXmlParser = new PocketSlateXmlParser(mPocketDbHelper);

		mOrgsXmlParser = new OrganizationsXmlParser(mPocketDbHelper);

		mItemListAdapter = new ItemListAdapter(this, mPocketDbHelper, mOpenTable);

		mItemHeader = getLayoutInflater().inflate(R.layout.item_list_header, null, true);
		mItemList.addHeaderView(mItemHeader, null, false);

		mItemHeaderText = (TextView)findViewById(R.id.item_header);

		mItemList.setAdapter(mItemListAdapter);

		handleIntent(getIntent());
	}

	@Override
	protected void onRestart() {
		super.onRestart();

	}

	@Override
	protected void onStart() {
		super.onStart();

		ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		String str = "";
		if(networkInfo == null) {
			str = "WiFi and/or Celluar Network not on or available!";
			new NetworkErrorPage(this, str);
			mRefresh = false;
		}

		mPrefs = getSharedPreferences("user_prefs", 0);
		mOpenTable = mPrefs.getString("open_to", "top_stories");
		mLastBuildLong = mPrefs.getLong("last_build", 0);
		mLastBuild = new Date(mLastBuildLong);
		mLastRefreshLong = mPrefs.getLong("last_refresh", 0);

		long now = new Date().getTime();			// Current System Time
		if((now - mLastRefreshLong) < ONE_HOUR) {	// Current time minus last content refresh
			mRefresh = false;						// User refreshed within an hour	
		} else {
			mRefresh = true;						
		}

		if(mRefresh) {
			loadPage();								// Content to be refreshed
		} else {
			selectMenuItem(mOpenTable);

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d("onPause()", "");

		SharedPreferences.Editor ed = mPrefs.edit();
		ed.putString("open_to", mOpenTable);
		ed.putLong("last_build", mLastBuildLong);
		ed.putLong("last_refresh", mLastRefreshLong);
		ed.commit();
		if(mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		mSearchView = (SearchView) menu.findItem(R.id.main_action_search).getActionView();
		mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_refresh).setVisible(!drawerOpen);
		menu.findItem(R.id.main_action_search).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return super.onOptionsItemSelected(item);
		}
		switch(item.getItemId()) {
		case R.id.action_refresh:
			loadPage();
			break;
		case R.id.main_action_search:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggle
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 *  Initialize the sliding drawer menu.  DrawerMenuItems for Options, Sections, and Header/Footer.
	 */
	private void createDrawerMenu() {
		List<DrawerMenuItem> menuItems = new ArrayList<DrawerMenuItem>();
		menuItems.add(new MenuOption("Home", "top_stories", R.drawable.ic_top_stories));
		menuItems.add(new MenuOption("Saved Stories", "saved_stories", R.drawable.ic_favorite));
		menuItems.add(new MenuHeader("Sections"));
		menuItems.add(new MenuSection("News", "news"));
		menuItems.add(new MenuSection("Features", "features"));
		menuItems.add(new MenuSection("Staff", "staff"));
		menuItems.add(new MenuSection("Sports", "sports"));
		menuItems.add(new MenuSection("Clubs and Organizations", "clubs_and_organizations"));
		menuItems.add(new MenuSection("Editorials", "editorials"));
		menuItems.add(new MenuSection("That's What She Said", "thats_what_she_said"));
		menuItems.add(new MenuSection("Bears to Watch", "bears_to_watch"));
		menuItems.add(new MenuHeader(""));
		menuItems.add(new MenuOption("Settings", "", R.drawable.ic_action_settings));
		menuItems.add(new MenuOption("About", "", R.drawable.ic_action_about));

		// Instantiate MenuListAdapter and set sliding menu to DrawerListAdapter.
		mDrawerListAdapter = new MenuListAdapter(this, menuItems);
		mDrawerList.setAdapter(mDrawerListAdapter);
	}


	// ClickListener for DrawerList
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			DrawerMenuItem drawerItem = mDrawerListAdapter.getItem(position);
			if(position < 12) {
				mItemList.setSelectionAfterHeaderView();
				selectMenuItem(drawerItem.getTable());
			}
		}
	}

	// ClickListener for ItemList
	private class ItemListClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(mOpenTable.equals("staff")) {
				selectStaff(position);
			} else if(mOpenTable.equals("clubs_and_organizations")) {
				selectOrg(position);
			} else {
				selectArticle(position);
			}

		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntent(intent);
	}

	/**
	 * Handles searches from the SearchView.  Instantiates an AsyncTask for 
	 * searching the database and calls execute.
	 * @param intent
	 */
	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			new SearchDbTask(query, ItemEntry.COLUMN_NAMES, "Searching for \"" + query + "\"...").execute(query, ItemEntry.COLUMN_NAMES[TITLE]);
		}
	}


	/**
	 * Deletes duplicated items from search table of database.  Unsure why
	 * the query duplicates all items.  Called from SearchDbTask.
	 * @param count
	 */
	private void handleDuplicates(int count) {
		for(int i=(count/2)+1; i<=count; i++) {
			mPocketDbHelper.deleteItem("search", ItemEntry._ID, i+"");
		}
	}

	/**
	 * Handles selection of DrawerList.  Changes mOpenTable to user selection.
	 * Updates mItemListAdapter and notifies.
	 * @param position - position of user selection
	 */
	private void selectMenuItem(String table) {
		mOpenTable = table;
		mItemHeaderText.setText("Latest update: " + mLastBuild.toString().substring(0, mLastBuild.toString().length() - 9));
		// Converts lower case table names to upper case with spaces
		String title = mOpenTable;
		String[] split = title.split("_");
		String finalTitle = new String();
		for(int i=0; i<split.length;i++) {
			split[i] = split[i].substring(0, 1).toUpperCase(Locale.US) + split[i].substring(1);
			finalTitle += " " + split[i];
		}

		setTitle(finalTitle);
		mItemListAdapter.update(mOpenTable);
		mItemListAdapter.notifyDataSetChanged();
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	/**
	 * Gets article from database based on user selection.  Packages items fields into a bundle.
	 * Creates an Intent and puts the Bundle in it.  Starts ItemActivity
	 * @param position - position of user selection
	 */
	private void selectArticle(int position) {
		Item item = mPocketDbHelper.getItem(mOpenTable, ItemEntry._ID, "" + position);

		Bundle bundle = new Bundle();
		bundle.putString("pub_date", item.pubDate);
		bundle.putString("table", mOpenTable);

		Intent itemActivityIntent = new Intent(this, ArticleActivity.class);
		itemActivityIntent.putExtra("article", bundle);
		startActivity(itemActivityIntent);
	}

	/**
	 * Gets staff member from database based on user selection.  Creates a search task to find all
	 * articles written by selected staff member.  Packages staff item's fields into bundle. Starts
	 *  a StaffActivity.
	 * @param position - position of user selection in list view.
	 */
	private void selectStaff(int position) {
		//TODO Create new search task to find all articles from all tables written by selected staff member. Cannot use current SearchDbTask
		
//		Item item = mPocketDbHelper.getItem("staff", ItemEntry._ID, "" + position);
//		String title = item.title;
//		new StaffArticlesTask(title, position).execute();
		
		Bundle bundle = new Bundle();
		bundle.putInt("position", position);

		Intent staffActivityIntent = new Intent(this, StaffActivity.class);
		staffActivityIntent.putExtra("staff", bundle);
		startActivity(staffActivityIntent);
	}

	//TODO selectOrg method then org activity
	private void selectOrg(int position) {
		Bundle bundle = new Bundle();
		bundle.putInt("position", position);

		Intent orgActivityIntent = new Intent(this, OrganizationActivity.class);
		orgActivityIntent.putExtra("organization", bundle);
		startActivity(orgActivityIntent);
	}
	
	private void startStaffActivity(String table, int position) {
		Bundle bundle = new Bundle();
		bundle.putInt("position", position);
		bundle.putString("table", table);

		Intent staffActivityIntent = new Intent(this, StaffActivity.class);
		staffActivityIntent.putExtra("staff", bundle);
		startActivity(staffActivityIntent);
	}

	/**
	 * Instantiates the AsyncTask and calls execute.
	 */
	private void loadPage() {
		new DownloadContentTask(this).execute(new String[]{URL_RSS, URL_ORGS });
	}

	/**
	 *  Instantiates a mProgressDialog to show user the application is performing a task.
	 * @param text - String of message to user to be displayed in the dialog
	 */
	public void createProgressDialog(String text) {
		//Create a new progress dialog  
		mProgressDialog = new ProgressDialog(MainActivity.this, ProgressDialog.THEME_HOLO_DARK);  
		//Set the progress dialog to display a spinner progress bar  
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		//Set the dialog title to 'Loading...'  
		//mProgressDialog.setTitle("Loading...");
		//Set the dialog message to 'Loading application View, please wait...'  
		mProgressDialog.setMessage(text);  
		//This dialog can't be canceled by pressing the back key  
		mProgressDialog.setCancelable(false);  
		//This dialog isn't indeterminate  
		mProgressDialog.setIndeterminate(false);  
		//Display the progress dialog  
		mProgressDialog.show();
	}

	/**
	 * Checks that there are entries in table of the database.
	 * @return
	 */
	private boolean tableExists(String table) {
		Cursor cursor = mPocketDbHelper.getReadableDatabase()
				.rawQuery("SELECT * FROM " + table,
						null);

		if(cursor != null) {
			if(cursor.getCount() > 0) {
				cursor.close();
				return true;
			} else {
				cursor.close();
			}
		}
		return false;
	}

	/**
	 * <p>Description: Created in the event of an error connecting to the network. </p>
	 * @author jasonscott
	 *
	 */
	private class NetworkErrorPage {

		/**
		 * Constructed when failing to connect to the network.
		 * String is passed explaining the error.
		 * @param a - Calling Activity
		 * @param err - String representation of the type of error
		 */
		public NetworkErrorPage(Activity a, String err) {
			AlertDialog.Builder builder = new AlertDialog.Builder(a, AlertDialog.THEME_HOLO_DARK);
			builder.setMessage(err);
			AlertDialog alert = builder.create();

			alert.setButton(DialogInterface.BUTTON_POSITIVE, "Retry", new DialogInterface.OnClickListener() {
				@Override 
				public void onClick(DialogInterface dialog, int which) {
					loadPage();
				}
			});
			alert.setButton(DialogInterface.BUTTON_NEGATIVE, "Dismiss", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			alert.show();
		}
	}

	/**
	 * AsyncTask to handle the network connection for parsing the RSS feed.
	 * @author jasonscott
	 *
	 */
	private class DownloadContentTask extends AsyncTask<String, Void, Void> {

		private Activity mActivity;
		private Date currentBuild;
		private boolean error = false;

		/**
		 *  Constructs new AsyncTask for downloading applications content.
		 * @param a - Activity constructing AsyncTask, needed to for 
		 * NetworkErrorPage construction back on UI Thread.
		 */
		public DownloadContentTask(Activity a) {
			mActivity = a;
		}

		@Override
		protected void onPreExecute() {

			// Get device orientation and prevent a configuration change
			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
			} else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			}

			createProgressDialog("Downloading latest content, this may take a couple of minutes please wait...");
		}

		@Override
		protected Void doInBackground(String... urls) {

			try {
				loadXmlFromNetwork(urls[0]);
				loadXmlOrgsFromNetwork(urls[1]);
			} catch(BuildDateException e) {
				//e.printStackTrace();
				if(mProgressDialog != null) {
					mProgressDialog.dismiss();
				}
				this.cancel(true);
			} catch(XmlPullParserException e) {
				e.printStackTrace();
				error = true;
				this.cancel(true);
			} catch(IOException e) {
				e.printStackTrace();
				error = true;
				this.cancel(true);
			} 
			return null;
		}

		// Called upon successful download.
		@Override
		protected void onPostExecute(Void result) {
			// Dismiss ProgressDialog
			if(mProgressDialog != null) {
				mProgressDialog.dismiss();
			}

			// Allow for configuration change
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

			// On successful download, save the times of the last build and current time the content refreshed.
			if(currentBuild != null) {
				mLastBuild = currentBuild;
				mLastBuildLong = mLastBuild.getTime();
				mLastRefreshLong = new Date().getTime();
			}

			// Populate ListView from database table
			selectMenuItem(mOpenTable);
		}

		// Called instead of onPostExecute if AsyncTask has been cancelled.
		@Override
		protected void onCancelled(Void result) {
			if(mProgressDialog != null)
				mProgressDialog.dismiss();

			// Allow for configuration change
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

			// IOException or XmlPullParserException caught.  Network error.
			if(error) {
				mActivity.runOnUiThread(new Runnable() {
					public void run() {
						new NetworkErrorPage(mActivity, "Failed on connection to server!");	
					}
				});
			} else {
				// BuildDateException, alert user that all content is up to date.
				Toast toast = Toast.makeText(getApplicationContext(), "All content is up to date", Toast.LENGTH_SHORT);
				toast.show();

				// Update last user refresh
				mLastRefreshLong = new Date().getTime();
			}

			// Check that there is a database to allow for offline viewing.
			if(tableExists(mOpenTable)) {
				selectMenuItem(mOpenTable);
			}
		}

		/**
		 *  First checks the date of the RSS and compares with mLastBuild.  
		 *  Then gets input stream from URL and passes to XmlParser calling parse.
		 * @param url - URL of RSS feed
		 * @throws XmlPullParserException - Thrown upon error parsing RSS.
		 * @throws IOException - Thrown upon error making network connection.
		 * @throws BuildDateException - Thrown when the build date of the 
		 * latest RSS matches the last RSS parsed by the application
		 */
		private void loadXmlFromNetwork(String url) throws XmlPullParserException, IOException, BuildDateException  {

			InputStream stream = null;
			InputStream tempStream = null;
			try {

				// Temporary stream for checking build date
				tempStream = downloadUrl(url);
				currentBuild = mPocketXmlParser.parseLastBuildDate(tempStream);

				if(mLastBuild != null && currentBuild.compareTo(mLastBuild) == 0) {
					throw new BuildDateException("Build Dates Match");
				}

				// If content needs to be updated, delete all tables for updated tables
				if(tableExists(mOpenTable)) {
					for(String table : ItemEntry.TABLE_NAMES) {
						mPocketDbHelper.deleteTable(table);
					}
				}

				stream = downloadUrl(url);
				mPocketXmlParser.parse(stream);

			} finally {

				// Close streams
				if(stream != null) {
					stream.close();
				}

				if(tempStream != null) {
					tempStream.close();
				}
			}
		}


		/**
		 * Calls OrganizationXmlParser's parse method passing InputStream from connection.
		 * file and passing it.
		 * @throws IOException
		 */
		private void loadXmlOrgsFromNetwork(String url) throws IOException {
			mOrgsXmlParser.parse(downloadUrl(url));
		}

		/**
		 * 
		 * Establishes HttpUrlConnection with RSS feed.
		 * @param urlString
		 * @return InputStream from RSS feed.
		 * @throws IOException
		 */
		private InputStream downloadUrl(String urlString) throws IOException {

			//			try {
			//				URL url = new URL(urlString);
			//				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			//				conn.setReadTimeout(5000 /* milliseconds */);
			//				conn.setConnectTimeout(5000/*milliseconds*/);
			//				conn.setRequestMethod("GET");
			//				conn.setDoInput(true);
			//				conn.connect();
			//				InputStream stream = conn.getInputStream();
			//				return stream;
			//			} catch(IOException e) {
			//				return getApplicationContext().getAssets().open("rss.xml");	
			//			}

			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(15000 /* milliseconds */);
			conn.setConnectTimeout(5000/*milliseconds*/);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			InputStream stream = conn.getInputStream();
			return stream;
		}
	}

	/**
	 * AsyncTask for handling search of the database.
	 * @author jasonscott
	 *
	 */
	private class SearchDbTask extends AsyncTask<String, Void, Boolean> {

		private String mQuery;
		private String[] mColumn;
		private String mMessage;

		public SearchDbTask(String q, String[] c, String m) {
			mQuery = q;
			mColumn = c;
			mMessage = m;
		}

		@Override
		protected void onPreExecute() {
			createProgressDialog(mMessage);
		}

		@Override
		protected Boolean doInBackground(String... params) {

			mPocketDbHelper.deleteTable("search");
			int count = 0;
			for(String tablename : ItemEntry.TABLE_NAMES) {
				if(tablename.equals("saved_stories")) {
					continue;
				}
				for(int i=0; i< mColumn.length;i++) {
					Cursor cursor = mPocketDbHelper.getItemMatches(tablename, mQuery, mColumn[i]);
					// Loop through all rows
					if(cursor != null) {
						if(cursor.moveToFirst()) { 
							do {
								String[] strs = new String[]{
										cursor.getString(0),	// ID
										cursor.getString(1),	// Title
										cursor.getString(2), 	// Link
										cursor.getString(3), 	// Description
										cursor.getString(4), 	// Content
										cursor.getString(5), 	// Category
										cursor.getString(6), 	// ImageUrl
										cursor.getString(7), 	// Publication date
										cursor.getString(8),
										cursor.getString(9)};	// Author
								Item item = new Item(strs);
								mPocketDbHelper.addItem(item, "search");
								count++;
								i = mColumn.length;
							} while(cursor.moveToNext());
							cursor.close();
						}
					}
				}
			}

			if(count == 0) {
				return false;
			} else {
				handleDuplicates(count);
				return true;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if(mProgressDialog != null) {
				mProgressDialog.dismiss();
			}
			if(result) {
				mOpenTable = "search";
				mSearchView.clearFocus();
			} else {
				Toast.makeText(getBaseContext(), "No Results Found for \"" + mQuery + "\"!", Toast.LENGTH_LONG).show();			
			}
			selectMenuItem(mOpenTable);
		}
	}
}
