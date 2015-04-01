/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslate;

import edu.nyit.pocketslateUtils.MenuListAdapter.RowType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
/**
 * <p>Title: MenuHeader.java</p>
 * <p>Description: Header/footer for sliding menu.  
 * Must override methods from DrawerMenuItem</p>
 * @author jasonscott
 *
 */
public class MenuHeader implements DrawerMenuItem {
	private String mTitle;
	
	/**
	 * Constructs header/footer for sliding menu.
	 * @param title - "Sections" for header and empty string for footer.
	 */
	public MenuHeader(String title) {
		mTitle = title;
	}
	
	@Override
	public int getViewType() {
		return RowType.MENU_HEADER.ordinal();
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		View view = convertView;
		if(convertView == null) {
			view = inflater.inflate(R.layout.menu_list_section_header, null);
		}

		TextView title = (TextView)view.findViewById(R.id.menu_header);
		title.setText(mTitle);
		return view;
	}

	@Override
	public String getTable() {
		return null;
	}

}
