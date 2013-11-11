/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslate;


/**
 * <p>Title: Constants.java</p>
 * <p>Description: Inteface for constant values used throughout the application</p>
 * @author jasonscott
 *
 */
public interface Constants {
	
	// URLS for the campus slate
	public static final String URL_RSS = "http://www.campusslate.com/feed/";
	public static final String URL_ORGS = "http://www.campusslate.com/orgs.xml";
	public static final String URL_STAFF = "http://www.campusslate.com/bios.xml";
	
	// For ItemEntry.TABLE_NAMES
	public static final int TOP_STORIES = 0;
	public static final int FAVORITES = 1;
	public static final int NEWS = 2;
	public static final int FEATURES = 3;
	public static final int STAFF = 4;
	public static final int EVENTS = 5;
	public static final int SPORTS = 6;
	public static final int CLUBS_AND_ORGANIZATIONS = 7;
	public static final int EDITORIALS = 8;
	public static final int THATS_WHAT_SHE_SAID = 9;
	public static final int BEARS_TO_WATCH = 10;
	public static final int SEARCH = 11;

	
	// For ItemEntry.COLUMN_NAMES
	public static final int TITLE = 0;
	public static final int DESCRIPTION = 1;
	public static final int LINK = 2;
	public static final int IMAGE_URL = 3;
	public static final int CONTENT = 4;
	public static final int CATEGORY = 5;
	public static final int DATE = 6;
	public static final int AUTHOR = 7;
	public static final int SAVED = 8;
	
	// One hour in milliseconds 
	static final long ONE_HOUR = 1000 * 60 * 60;
}
