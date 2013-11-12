/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslateUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.nyit.pocketslate.Item;
import edu.nyit.pocketslate.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * <p>Title: ItemListAdapter.java</p>
 * <p>Description: Adapter for mItemList ListView.  Reference to PocketSlateDbHelper
 *  for retrieving items from database and applying to the ListView.</p>
 * @author jasonscott
 *
 */
public class ItemListAdapter extends BaseAdapter {

	private Activity mActivity;
	private static LayoutInflater sInflater = null;
	private PocketSlateDbHelper mPocketDbHelper;
	private List<Item> mItems;
	private String mTableName;
	private ImageView mImage;

	/**
	 * Constructs the adapter for list of items
	 * @param a - Activity, applications MainActivity to get layout inflater
	 * @param dbH - PocketSlateDatabaseHelper to access application database
	 * @param tableName - Table name in database for current list of items
	 */
	public ItemListAdapter(Activity a, PocketSlateDbHelper dbH, String tableName) {
		mActivity = a;
		mTableName = tableName;
		sInflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPocketDbHelper = dbH;
		mItems = mPocketDbHelper.getAllItems(mTableName);
	}

	/**
	 *  Update the ArrayList of Items by getting all items from the database
	 *  of a particular table.
	 * @param tableName -  String of table name in database currently selected
	 */
	public void update(String tableName) {
		mItems = mPocketDbHelper.getAllItems(tableName);
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		if(convertView == null) {
			v = sInflater.inflate(R.layout.item_list_row, null);
		}
		mImage = (ImageView)v.findViewById(R.id.item_image);
		TextView title = (TextView)v.findViewById(R.id.item_title);
		TextView date = (TextView)v.findViewById(R.id.item_date);
		title.setText(mItems.get(position).title);
		String dateText = mItems.get(position).pubDate;
		if(dateText != null && dateText.length() > 6) {
			date.setText(dateText.substring(0,(dateText.length()-6)));
		} else {
			date.setText("");
		}
		new DownloadBitmapTask(mImage).execute(mItems.get(position).imageUrl);
		return v;
	}

	private class DownloadBitmapTask extends AsyncTask<String, Void, Bitmap> {
		private ImageView mImageView;

		public DownloadBitmapTask(ImageView i) {
			mImageView = i;
		}
		
		@Override
		protected void onPreExecute() {
			mImageView.setImageResource(R.drawable.ic_action_refresh);
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
				mImageView.setImageBitmap(result);
			}
		}

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
}
