/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslateUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import edu.nyit.pocketslate.Item;
import edu.nyit.pocketslate.R;
import edu.nyit.pocketslateUtils.BitmapWorkerTask.AsyncDrawable;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
		mItems = mPocketDbHelper.getAllItems(mTableName = tableName);
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
		Item item = mItems.get(position);

		View v = convertView;
		if(convertView == null) {
			v = sInflater.inflate(R.layout.item_list_row, null);
		}

		mImage = (ImageView)v.findViewById(R.id.item_image);
		TextView title = (TextView)v.findViewById(R.id.item_title);
		TextView date = (TextView)v.findViewById(R.id.item_date);
		TextView author = (TextView)v.findViewById(R.id.item_author);

		if(mTableName.equals("staff")) {
			String[] strs = item.title.split("Ð");
			title.setText(strs[0]);
			author.setText("");
			if(strs.length > 1) { 
				date.setText(strs[1]); 
			}
		} else if(mTableName.equals("clubs_and_organizations")) {
			author.setText("");
			date.setText("");
			title.setText(item.title);
		} else {
			author.setText("by " + item.author);
			title.setText(item.title);
			String dateText = item.pubDate;
			if(dateText != null && dateText.length() > 6) {
				date.setText(dateText.substring(0,(dateText.length()-6)));
			} else {
				date.setText("");
			}
		}

		if(item.imageUrl != null) {
//			BitmapWorkerTask task = new BitmapWorkerTask(mImage, 100, 100);
//			task.execute(item.imageUrl);
			loadBitmap(mImage, item.imageUrl, 100, 100);
		} else {
			mImage.setImageResource(R.drawable.splash_horizontal);
		}
		return v;
	}
	
	/**
	 * 
	 * @param imageView
	 * @param url
	 * @param w
	 * @param h
	 */
	public void loadBitmap(ImageView imageView, String url, int w, int h) {
	    if (BitmapWorkerTask.cancelPotentialWork(url, imageView)) {
	        final BitmapWorkerTask task = new BitmapWorkerTask(imageView, url, h, w);
	        final AsyncDrawable asyncDrawable =
	                new AsyncDrawable(mActivity.getResources(), BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.ic_action_refresh), task);
	        imageView.setImageDrawable(asyncDrawable);
	        task.execute(url);
	    }
	}

//	/**
//	 * 
//	 * @param url
//	 * @param imageView
//	 * @return
//	 */
//	public static boolean cancelPotentialWork(String url, ImageView imageView) {
//	    final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
//
//	    if (bitmapWorkerTask != null) {
//	        final String bitmapUrl = bitmapWorkerTask.mUrl;
//	        if (!bitmapUrl.equals(url)) {
//	            // Cancel previous task
//	            bitmapWorkerTask.cancel(true);
//	        } else {
//	            // The same work is already in progress
//	            return false;
//	        }
//	    }
//	    // No task associated with the ImageView, or an existing task was cancelled
//	    return true;
//	}
//	
//	/**
//	 * 
//	 * @param imageView
//	 * @return
//	 */
//	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
//		   if (imageView != null) {
//		       final Drawable drawable = imageView.getDrawable();
//		       if (drawable instanceof AsyncDrawable) {
//		           final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
//		           return asyncDrawable.getBitmapWorkerTask();
//		       }
//		    }
//		    return null;
//		}
}
