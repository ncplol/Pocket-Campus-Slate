/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslate;

import android.view.LayoutInflater;
import android.view.View;
/**
 * <p>Title: DrawerMenuItem.java</p>
 * <p>Description: Interface for MenuHeader, MenuOption, and MenuSection.</p>
 * @author jasonscott
 *
 */
public interface DrawerMenuItem {
	public int getViewType();
	public View getView(LayoutInflater inflater, View convertView);
	public String getTable();
}
