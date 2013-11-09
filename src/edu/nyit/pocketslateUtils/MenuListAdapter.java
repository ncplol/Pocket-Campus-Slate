/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslateUtils;

import java.util.List;

import edu.nyit.pocketslate.DrawerMenuItem;
import edu.nyit.pocketslate.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import static edu.nyit.pocketslate.Constants.*;

/**
 * <p>Title: MenuListAdapter.java</p>
 * <p>Description: Adapter for sliding menu ListView </p>
 * @author jasonscott
 *
 */
public class MenuListAdapter extends BaseAdapter {

	private Activity mActivity;
	private List<DrawerMenuItem> mItems;
	private static LayoutInflater sInflater = null;

	// Three options for items in the menu
	public enum RowType {
		MENU_OPTION, MENU_SECTION, MENU_HEADER
	}

	/**
	 * Constructs adapter for sliding menu.
	 * @param a - Activity, applications MainActivity needed for getting LayoutInflater
	 * @param items
	 */
	public MenuListAdapter(Activity a, List<DrawerMenuItem> items) {
		mActivity = a;
		mItems = items;
		sInflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public DrawerMenuItem getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).getViewType();
	}

	@Override
	public int getViewTypeCount() {
		return RowType.values().length;
	}

	@Override
	public boolean isEnabled(int position) {
		if(getItem(position).getViewType() == RowType.MENU_HEADER.ordinal()) {
			return false;			// Sliding menu header/footer, not selectable
		} else {
			return true;			// Sliding menu option or section to be selected
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		return getItem(position).getView(sInflater, convertView);
	}

}
