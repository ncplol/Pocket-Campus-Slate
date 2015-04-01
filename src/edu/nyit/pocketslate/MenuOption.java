/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslate;

import edu.nyit.pocketslateUtils.MenuListAdapter.RowType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * <p>Title: MenuOption.java</p>
 * <p>Description: Sliding menu options that aren't sections of the Campus Slate.
 * Must override methods of DrawerMenuItem.</p>
 * @author jasonscott
 *
 */
public class MenuOption implements DrawerMenuItem{
	private String mTitle;
	private String mTable;
	private int mResId;
	
	/**
	 * Constructs menu option(Home, Saved Stories, Settings, and About)
	 * @param title - String for option title
	 * @param table - String for table name of database if Home or Saved Stories otherwise empty
	 * @param resId - Drawable resource ID
	 */
	public MenuOption(String title, String table, int resId) {
		mTitle = title;
		mTable = table;
		mResId = resId;
	}

	@Override
	public int getViewType() {
		return RowType.MENU_OPTION.ordinal();
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		View view = convertView;
		if(convertView == null) {
			view = inflater.inflate(R.layout.menu_list_row, null);
		}

		TextView title = (TextView)view.findViewById(R.id.menu_option_title);
		ImageView thumbnail = (ImageView)view.findViewById(R.id.menu_option_image);
		title.setText(mTitle);
		thumbnail.setImageResource(mResId);
		return view;
	}

	@Override
	public String getTable() {
		return mTable;
	}

}
