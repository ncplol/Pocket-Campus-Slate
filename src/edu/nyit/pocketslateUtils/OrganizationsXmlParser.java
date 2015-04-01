/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslateUtils;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;
import edu.nyit.pocketslate.Item;
import edu.nyit.pocketslateUtils.PocketSlateReaderContract.ItemEntry;
import static edu.nyit.pocketslate.Constants.*;

/**
 * <p>Title: OrganizationsXmlParser</p>
 * <p>Description: Parser for clubs and organizations.  Uses XmlPullParser.</p>
 * @author jasonscott
 *
 */
public class OrganizationsXmlParser {
	private PocketSlateDbHelper mDbHelper;
	private Item mOrg;
	private String mText;

	/**
	 * Constructs the parser.
	 * @param dbH - PocketSlateDbHelper for storing the clubs and organizations.
	 */
	public OrganizationsXmlParser(PocketSlateDbHelper dbH) {
		mDbHelper = dbH;
	}

	/**
	 * 	Parses in from connection to url of the clubs and organizations xml
	 * @param in - InputStream, from MainActivity's DownloadContentTask's downloadUrl method.
	 */
	public void parse(InputStream in) {
		XmlPullParserFactory factory = null;
		XmlPullParser parser = null;
		String title = null;
		String imageUrl = null;
		String description = null;

		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			parser = factory.newPullParser();

			parser.setInput(in, null);

			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tagName = parser.getName();
				switch (eventType) {
				case XmlPullParser.TEXT:
					mText = parser.getText();
					break;
				case XmlPullParser.END_TAG:
					if(tagName.equalsIgnoreCase("org")) {
						mOrg = new Item(null, title, null, null, description, "Clubs and Organizations", imageUrl, null, null, null);
						imageUrl = null;
						mDbHelper.addItem(mOrg, ItemEntry.TABLE_NAMES[CLUBS_AND_ORGANIZATIONS]);
					} else if(tagName.equalsIgnoreCase("lname")) {
						title = mText;
					} else if(tagName.equalsIgnoreCase("logo")) {
						imageUrl = mText;
					} else if(tagName.equalsIgnoreCase("description")) {
						description = mText;
					}
					break;
				default:
					break;
				}
				eventType = parser.next();
			}

		} catch(XmlPullParserException e) {
			Log.d("XmlPullParserException: OrganizationsXmlParser.parse()", e.getMessage());
		} catch(IOException e) {
			Log.d("IOException: OrganizationsXmlParser.parse()", e.getMessage());
		}

	}

}
