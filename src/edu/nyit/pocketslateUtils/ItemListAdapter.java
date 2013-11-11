/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslateUtils;

import java.util.List;

import edu.nyit.pocketslate.Item;
import edu.nyit.pocketslate.R;
import android.app.Activity;
import android.content.Context;
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
		Log.d("update done here","");
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
		ImageView image = (ImageView)v.findViewById(R.id.item_image);
		TextView title = (TextView)v.findViewById(R.id.item_title);
		TextView date = (TextView)v.findViewById(R.id.item_date);
		image.setImageResource(R.drawable.item_image_testing);
		title.setText(mItems.get(position).title);
		String dateText = mItems.get(position).pubDate;
		if(dateText != null && dateText.length() > 6) {
			date.setText(dateText.substring(0,(dateText.length()-6)));
		} else {
			date.setText("");
		}
		return v;
	}
	
	

}
