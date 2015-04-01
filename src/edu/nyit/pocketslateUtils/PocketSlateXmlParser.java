/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslateUtils;


import static edu.nyit.pocketslate.Constants.*;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;
import android.util.Xml;
import edu.nyit.pocketslate.Item;
import edu.nyit.pocketslateUtils.PocketSlateReaderContract.ItemEntry;
/**
 * <p>Title: PocketSlateXmlParser.java</p>
 * <p>Description: Parser for Campus Slate XML data.  Uses XmlPullParser.</p>
 * @author jasonscott
 *
 */
public class PocketSlateXmlParser {
	private PocketSlateDbHelper mDbHelper;
	private Item mItem;
	private String mText;
	private Date mLastBuild;
	private long timeStart;

	/**
	 * Constructs parser.
	 * @param dbH - PocketSlateDbHelper, for storing Campus Slate items.
	 */
	public PocketSlateXmlParser(PocketSlateDbHelper dbH) {
		mDbHelper = dbH;
	}

	/**
	 * Parses in from connection to Campus Slate RSS feed.
	 * @param in - InputStream, from MainActivity's DownloadContentTask's downloadUrl method.
	 */
	public void parse(InputStream in) {
		timeStart = System.currentTimeMillis();
		XmlPullParserFactory factory = null;
		XmlPullParser parser = null;
		String title = null;
		String link = null;
		String description = null;
		String content = null;
		String category = null;
		String imageUrl = null;
		String pubDate = null;
		String author = null;

		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			parser = factory.newPullParser();

			parser.setInput(in, null);

			int eventType = parser.getEventType();
			while(eventType != XmlPullParser.END_DOCUMENT) {
				String tagName = parser.getName();
				switch(eventType) {
				case XmlPullParser.TEXT:
					mText = parser.getText();
					break;
				case XmlPullParser.END_TAG:
					if(tagName.equalsIgnoreCase("item")) {

						// Check for image URL within content.
						if(content.contains("img src")) {
							int start = content.indexOf("img src=") + 9;
							int end = content.indexOf("\"", start);		// Index of " implying end of the link
							imageUrl = content.substring(start, end);
						}

						// Separate the div with image link from the content
						if(content.contains("<p>")) {
							int contentStart = content.indexOf("<p>");
							content = content.substring(contentStart);
						}

						mItem = new Item(null, title, link, description, content, category, imageUrl, pubDate, author, null);
						imageUrl = null;
						addItem(mItem);
					} else if(tagName.equalsIgnoreCase("title")) {
						title = mText;
					} else if(tagName.equalsIgnoreCase("link")) {
						link = mText;
					} else if(tagName.equalsIgnoreCase("pubDate")) {
						pubDate = mText;
					} else if(tagName.equalsIgnoreCase("category")) {
						category = mText;
					} else if(tagName.equalsIgnoreCase("description")) {
						description = mText;
					} else if(tagName.equalsIgnoreCase("encoded")) {
						content = mText;
					} else if(tagName.equalsIgnoreCase("creator")) {
						author = mText;
					} else if(tagName.equalsIgnoreCase("lastBuildDate")) {
						mLastBuild = buildStringToDate(mText);
					} 
					break;
				default:
					break;
				}
				eventType = parser.next();
			}
		} catch(XmlPullParserException e) {
			Log.d("XmlPullParserException: PocketXmlParser.parse()", e.getMessage());
		} catch(IOException e) {
			Log.d("IOException: PocketXmlParser.parse()", e.getMessage());
		}
	}

	/**
	 * Adds an entry to the database.
     * NOTE: The "Top Stories" tag no longer exists or is no longer used on the website.
     * I have no intention of updating this app with the website so this quick fix is all I can do.
     * Now it just dumps the "News" and "Features" tags into the "Top Stories" table so it will display something when the app starts.
	 * @param item - Item to be stored.
	 */
	public void addItem(Item item) {
		if(item.category.equals("News") || item.category.equals("Features")) {
			mDbHelper.addItem(item, ItemEntry.TABLE_NAMES[TOP_STORIES]);
		} else if(item.category.equals("News")) {
			mDbHelper.addItem(item, ItemEntry.TABLE_NAMES[NEWS]);
		} else if(item.category.equals("Features")) {
			mDbHelper.addItem(item, ItemEntry.TABLE_NAMES[FEATURES]);
		} else if(item.category.equals("Editorials")) {
			mDbHelper.addItem(item, ItemEntry.TABLE_NAMES[EDITORIALS]);
		} else if(item.category.equals("Sports")) {
			mDbHelper.addItem(item, ItemEntry.TABLE_NAMES[SPORTS]);
		} else if(item.category.equals("Bears to Watch")) {
			mDbHelper.addItem(item, ItemEntry.TABLE_NAMES[BEARS_TO_WATCH]);
		} else if(item.category.equals("That's What She Said")) {
			mDbHelper.addItem(item, ItemEntry.TABLE_NAMES[THATS_WHAT_SHE_SAID]);
		} else if(item.category.equals("Staff")) {
			mDbHelper.addItem(item, ItemEntry.TABLE_NAMES[STAFF]);
		}
		String author = item.author.toLowerCase(Locale.US);
		for(String staffMember : ItemEntry.STAFF_TABLE_NAMES) {
			String n = staffMember.replace("_", " ");
			if(author.contains(n)) {
				mDbHelper.addItem(item, staffMember);
			}
		}
		System.out.println("Elapsed time from start is " + new Date(System.currentTimeMillis() - timeStart).toString());
	}

	/**
	 * Parses in looking for the last build of the XML data.
	 * @param in, InputStream, from MainActivity's DownloadContentTask's downloadUrl method.  
	 * Pass a temporary duplicated stream instead of stream used for actually parsing all the data.
	 * @return Date of last build
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public Date parseLastBuildDate(InputStream in) throws XmlPullParserException, IOException {
		String last = null;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			parser.require(XmlPullParser.START_TAG, null, "rss");
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();
				if(name.equals("lastBuildDate")) {
					last = readLastBuild(parser);
					mLastBuild = buildStringToDate(last);
					return mLastBuild;
				}
				else
					skip(parser);
			}

		} finally {
			in.close();
		}
		return null;
	}

	/**
	 * Called from parseLastBuildDate method, calls readText method
	 * @param parser, XmlPullParser at the location of the build date namespace
	 * @return String representing the last build of the XML data
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private String readLastBuild(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, "lastBuildDate");
		String last = readText(parser);
		parser.require(XmlPullParser.END_TAG, null, "lastBuildDate");
		return last;
	}

	/**
	 * Gets text from parser current location
	 * @param parser, XmlPullParser at the location of the text in lastBuildDate namespace
	 * @return String representing text of the date of last build
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String result = "";
		if(parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	/**
	 * Skips unwanted position of parser.
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if(parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while(depth != 0) {
			if(parser.getName() != null && parser.getName().equals("channel")) {
				depth--;
			}
			switch(parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}

	/**
	 * Constructs a Date object representing the last build of the XML data
	 * @param last - String to convert into Date object.
	 * @return Date, of last build of XML data.
	 */
	private Date buildStringToDate(String l) {
		SimpleDateFormat f = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
		Date d = null;
		try {
			d = f.parse(l);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return d;
	}
}
