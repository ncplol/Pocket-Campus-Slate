/**
 * Copyright (C) 2013 Jason Scott
 */
package edu.nyit.pocketslateUtils;

import java.util.ArrayList;
import java.util.List;

import edu.nyit.pocketslate.Item;
import edu.nyit.pocketslateUtils.PocketSlateReaderContract.ItemEntry;
import static edu.nyit.pocketslate.Constants.*;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * <p>Title: PocketSlateDbHelper.java</p>
 * <p>Description: </p>
 * @author jasonscott
 *
 */ 
public class PocketSlateDbHelper extends SQLiteOpenHelper {
	private static PocketSlateDbHelper sInstance = null;
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "PocketSlate.db";
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ";
	private static final String SQL_CREATE_ENTRIES = 
			" (" + 
					ItemEntry._ID + " INTEGER PRIMARY KEY," +
					ItemEntry.COLUMN_NAMES[TITLE] + TEXT_TYPE + COMMA_SEP +
					ItemEntry.COLUMN_NAMES[LINK] + TEXT_TYPE + COMMA_SEP +
					ItemEntry.COLUMN_NAMES[DESCRIPTION] + TEXT_TYPE + COMMA_SEP +
					ItemEntry.COLUMN_NAMES[CONTENT] + TEXT_TYPE + COMMA_SEP +
					ItemEntry.COLUMN_NAMES[CATEGORY] + TEXT_TYPE + COMMA_SEP +
					ItemEntry.COLUMN_NAMES[IMAGE_URL] + TEXT_TYPE + COMMA_SEP +
					ItemEntry.COLUMN_NAMES[DATE] + TEXT_TYPE + COMMA_SEP +
					ItemEntry.COLUMN_NAMES[AUTHOR] + TEXT_TYPE + COMMA_SEP +
					ItemEntry.COLUMN_NAMES[SAVED] + TEXT_TYPE + " )";

	/**
	 * 
	 * @param c
	 * @return
	 */
	public static PocketSlateDbHelper getInstance(Context c) {
		if(sInstance == null) {
			sInstance = new PocketSlateDbHelper(c.getApplicationContext());
		}
		return sInstance;
	}
	
	/**
	 *  Constructs SQLiteDatabase Helper for managing applications database.
	 * @param context - Applications Context
	 */
	private PocketSlateDbHelper(Context c) {
		super(c, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for(String tableName : ItemEntry.TABLE_NAMES) {
			db.execSQL(SQL_DELETE_ENTRIES + tableName);
			db.execSQL("CREATE TABLE " + tableName + SQL_CREATE_ENTRIES);
		}	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//TODO onUpgrade; How to handle database upgrades, currently just delete and download again.
	}


	/**
	 * Adds new item into database.
	 * @param item - Item, new item to add
	 * @param table - String, table name where to add item
	 */
	public void addItem(Item item, String table) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(ItemEntry.COLUMN_NAMES[TITLE], item.title);	
		values.put(ItemEntry.COLUMN_NAMES[LINK], item.link);
		values.put(ItemEntry.COLUMN_NAMES[DESCRIPTION], item.description);
		values.put(ItemEntry.COLUMN_NAMES[CONTENT], item.content);
		values.put(ItemEntry.COLUMN_NAMES[CATEGORY], item.category);
		values.put(ItemEntry.COLUMN_NAMES[IMAGE_URL], item.imageUrl);
		values.put(ItemEntry.COLUMN_NAMES[DATE], item.pubDate);
		values.put(ItemEntry.COLUMN_NAMES[AUTHOR], item.author);
		values.put(ItemEntry.COLUMN_NAMES[SAVED], item.saved);

		db.insert(table, null, values);
		db.close();
	}

	/**
	 * 
	 * @param table
	 * @param column
	 * @param value
	 * @param date
	 */
	public void updateItem(String table, String column, String value, String date) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(column, value);	
		String selection = ItemEntry.COLUMN_NAMES[DATE] + " LIKE ?";
		String[] selectionArgs = { String.valueOf(date) };
		db.update(table, values, selection, selectionArgs);
		db.close();
	}

	/**
	 *  Retrieves item from database.
	 * @param table - String of table name of database to retrieve from
	 * @param selection
	 * @param selectionArg
	 * @return Item from database entry
	 */
	public Item getItem(String table, String selection, String selectionArg) {

		String[] selectionArgs = { String.valueOf(selectionArg) };

		Item item = null;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(table, null, selection + " LIKE ?", selectionArgs, null, null, null);
		if(cursor.moveToFirst()) { 
			do {
				String[] strs = new String[]{
						cursor.getString(0),	// ID
						cursor.getString(1),	// Title
						cursor.getString(2), 	// Link
						cursor.getString(3), 	// Description
						cursor.getString(4), 	// Content
						cursor.getString(5), 	// Category
						cursor.getString(6), 	// ImageUrl
						cursor.getString(7), 	// Publication date
						cursor.getString(8),
						cursor.getString(9) };	// Author
				item = new Item(strs);
			} while(cursor.moveToNext());
			cursor.close();
		}
		db.close();
		return item;
	}

	/**
	 * Creates an ArrayList of Item objects,queries the database,
	 *  for all entries for that table, packs text from database
	 *  into a cursor, creates an Item for each entry and adds 
	 *  each Item to the List.
	 * @param table - String, table name for entries to be retrieved
	 * @return ArrayList<Item> items, all entries for the given table
	 */
	public List<Item> getAllItems(String table) {
		List<Item> items = new ArrayList<Item>();
		// Select all query
		String selectQuery = "SELECT  * FROM " + table;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// Loop through all rows
		if(cursor.moveToFirst()) { 
			do {
				String[] strs = new String[]{
						cursor.getString(0),	// ID
						cursor.getString(1),	// Title
						cursor.getString(2), 	// Link
						cursor.getString(3), 	// Description
						cursor.getString(4), 	// Content
						cursor.getString(5), 	// Category
						cursor.getString(6), 	// ImageUrl
						cursor.getString(7), 	// Publication date
						cursor.getString(8),	// Author
						cursor.getString(9)};	// Saved
				Item item = new Item(strs);
				items.add(item);
			} while(cursor.moveToNext());
			cursor.close();
		}
		db.close();
		return items;
	}

	/**
	 * Calls SQLiteDatabase delete method to remove an item from the table.
	 * @param tableName - String of table name of database for entry to be deleted
	 * @param column - String of column to look in
	 * @param date - String of items date for best case match.  ItemEntry._ID causes problems when deleting a saved story.
	 * @return true if successfully deleted, false if not
	 */
	public boolean deleteItem(String tableName, String column, String date) {
		//TODO algorithm for updating "_ID" for all other entries in database
		
		SQLiteDatabase db = this.getWritableDatabase();
		String selection = column + " LIKE ?";
		String[] selectionArgs = { String.valueOf(date) };
		int result = db.delete(tableName, selection, selectionArgs);
		db.close();
		if(result > 0) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Deletes an entire table.
	 * @param table - String of table to be deleted from database.
	 */
	public void deleteTable(String table) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(table, null, null);
		db.close();
	}

	/**
	 *  Used for searching database.  Calls query method below.
	 * @param table - String of table to be searched.
	 * @param query - String of user search query
	 * @param columnName - String of column to search in.
	 * @return Cursor from query method.
	 */
	public Cursor getItemMatches(String table, String query, String columnName) {
		String selection = columnName + " LIKE ?";
		String[] selectionArgs = new String[] {"%"+query+"%"};

		return query(table, selection, selectionArgs);
	}

	/**
	 * Called by getItemMatches method for searching the database.
	 * @param table - String of table being searched
	 * @param selection - SQL where clause
	 * @param selectionArgs - Value of query
	 * @return Cursor containing entries if any found.
	 */
	private Cursor query(String table, String selection, String[] selectionArgs) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(table, null, selection, selectionArgs, null, null, null);

		if(cursor == null) {
			return null;
		} else if(!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		db.close();
		return cursor;
	}


}
