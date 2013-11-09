/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslate;

import edu.nyit.pocketslateUtils.MenuListAdapter.RowType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
/**
 * <p>Title: MenuSection.java</p>
 * <p>Description: Sections of the Campus Slate.
 * Must override methods from DrawerMenuItem.</p>
 * @author jasonscott
 *
 */
public class MenuSection implements DrawerMenuItem{
	private String mTitle;
	private String mTable;

	/**
	 * Constructs section of Campus Slate for sliding menu
	 * @param title - String of section
	 * @param table - String of table name in database for particular section
	 */
	public MenuSection(String title, String table) {
		mTitle = title;
		mTable = table;
	}
	
	@Override
	public int getViewType() {
		return RowType.MENU_SECTION.ordinal();
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		View view = convertView;
		if(convertView == null) {
			view = inflater.inflate(R.layout.menu_list_section, null);
		}

		TextView title = (TextView)view.findViewById(R.id.menu_option_title);
		title.setText(mTitle);
		
		return view;
	}

	@Override
	public String getTable() {
		return mTable;
	}

}
