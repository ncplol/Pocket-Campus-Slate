package edu.nyit.pocketcampusslate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Nick Passaro on 10/9/13.
 */
/*
    To create a database with this helper, we simply need to call:
    MyOpenHelper helper = new MyOpenHelper(getApplicationContext());
    SQLiteDatabase db = helper.getWritableDatabase();

    This SQLite database object has methods that allow the app to add, delete, and update rows, among other things.
    Here is an example of adding a row to the database, to get you started:
    // ContentValues objects are used by most of SQLiteDatabase's methods.
    ContentValues values = new ContentValues();
    values.put("first_name", "Elvis");
    values.put("last_name", "Presley");

    try
    {db.insert("people", null, values);}
    catch(Exception ex)
    {
        toast("Database error: Elvis has left the building.\n\n"
                + ex.getLocalizedMessage());
    }*/

class MyOpenHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db; // a reference to the database manager class.
    private static final String DB_NAME = "RSSFeed"; // the name of our database
    private static final int DB_VERSION = 1; // the version of the database

    // the names for our database columns
    private final String TABLE_NAME = "Articles";
    private final String TABLE_ROW_ID = "id";
    private final String TABLE_ROW_ONE = "article_title";
    private final String TABLE_ROW_TWO = "article_author";
    private final String TABLE_ROW_THREE = "article_date";
    private final String TABLE_ROW_FOUR = "article_category";
    private final String TABLE_ROW_FIVE = "article_content";
    private final String TABLE_ROW_SIX = "article_image";//Article images will be stored in db as byte arrays

    //To convert a bitmap to a byte array and add it to the db:
    /*public void insertImg(int id , Bitmap img ) {


        byte[] data = getBitmapAsByteArray(img); // this is a function

        insertStatement_logo.bindLong(1, id);
        insertStatement_logo.bindBlob(2, data);

        insertStatement_logo.executeInsert();
        insertStatement_logo.clearBindings() ;

    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }*/

    //To convert a byte array back to a bitmap and retrieve from db:
    /*public Bitmap getImage(int i){

        String qu = "select img  from table where feedid=" + i ;
        Cursor cur = db.rawQuery(qu, null);

        if (cur.moveToFirst()){
            byte[] imgByte = cur.getBlob(0);
            cur.close();
            return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
        }
        if (cur != null && !cur.isClosed()) {
            cur.close();
        }

        return null ;
    }*/

    public MyOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // the SQLite query string that will create our 3 column database table.
        String newTableQueryString =
                "create table " +
                        TABLE_NAME +
                        " (" +
                        TABLE_ROW_ID + " integer primary key autoincrement not null," +
                        TABLE_ROW_ONE + " text," +
                        TABLE_ROW_TWO + " text" +
                        TABLE_ROW_THREE + " text" +
                        TABLE_ROW_FOUR + " text" +
                        TABLE_ROW_FIVE + " text" +
                        TABLE_ROW_SIX + " blob" +//BLOB is what SQLite uses to store byte arrays
                        ");";

        // execute the query string to the database.
        db.execSQL(newTableQueryString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This method is mandatory, but never used unless you're upgrading a
        // database from an older SQLite version.
    }

    public void addRow(String rowStringOne, String rowStringTwo, String rowStringThree, String rowStringFour, String rowStringFive, byte[] rowByteSix) {
        // this is a key value pair holder used by android's SQLite functions
        ContentValues values = new ContentValues();

        // this is how you add a value to a ContentValues object
        // we are passing in a key string and a value string each time
        values.put(TABLE_ROW_ONE, rowStringOne);
        values.put(TABLE_ROW_TWO, rowStringTwo);
        values.put(TABLE_ROW_THREE, rowStringThree);
        values.put(TABLE_ROW_FOUR, rowStringFour);
        values.put(TABLE_ROW_FIVE, rowStringFive);
        values.put(TABLE_ROW_SIX, rowByteSix);

        // ask the database object to insert the new data
        try {
            db.insert(TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString()); // prints the error message to the log
            e.printStackTrace(); // prints the stack trace to the log
        }
    }

    public void deleteRow(long rowID) {
        // ask the database manager to delete the row of given id
        try {
            db.delete(TABLE_NAME, TABLE_ROW_ID + "=" + rowID, null);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }

    public void updateRow(long rowID, String rowStringOne, String rowStringTwo, String rowStringThree, String rowStringFour, String rowStringFive, byte[] rowByteSix) {
        // this is a key value pair holder used by android's SQLite functions
        ContentValues values = new ContentValues();
        values.put(TABLE_ROW_ONE, rowStringOne);
        values.put(TABLE_ROW_TWO, rowStringTwo);
        values.put(TABLE_ROW_THREE, rowStringThree);
        values.put(TABLE_ROW_FOUR, rowStringFour);
        values.put(TABLE_ROW_FIVE, rowStringFive);
        values.put(TABLE_ROW_SIX, rowByteSix);

        // ask the database object to update the database row of given rowID
        try {
            db.update(TABLE_NAME, values, TABLE_ROW_ID + "=" + rowID, null);
        } catch (Exception e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
    }

    /**
     * *******************************************************************
     * RETRIEVING ALL ROWS FROM THE DATABASE TABLE
     * <p/>
     * This is an example of how to retrieve all data from a database
     * table using this class.  You should edit this method to suit your
     * needs.
     * <p/>
     * the key is automatically assigned by the database
     */
    public ArrayList<ArrayList<Object>> getAllRowsAsArrays() {
        // create an ArrayList that will hold all of the data collected from
        // the database.
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();

        // this is a database call that creates a "cursor" object.
        // the cursor object store the information collected from the
        // database and is used to iterate through the data.
        Cursor cursor;

        try {
            // ask the database object to create the cursor.
            cursor = db.query(
                    TABLE_NAME,
                    new String[]{TABLE_ROW_ID, TABLE_ROW_ONE, TABLE_ROW_TWO, TABLE_ROW_THREE, TABLE_ROW_FOUR, TABLE_ROW_FIVE, TABLE_ROW_SIX},
                    null, null, null, null, null
            );

            // move the cursor's pointer to position zero.
            cursor.moveToFirst();

            // if there is data after the current cursor position, add it
            // to the ArrayList.
            if (!cursor.isAfterLast()) {
                do {
                    ArrayList<Object> dataList = new ArrayList<Object>();

                    dataList.add(cursor.getLong(0));
                    dataList.add(cursor.getString(1));
                    dataList.add(cursor.getString(2));
                    dataList.add(cursor.getString(3));
                    dataList.add(cursor.getString(4));
                    dataList.add(cursor.getString(5));
                    dataList.add(cursor.getBlob(6));
                    dataArrays.add(dataList);
                }
                // move the cursor's pointer up one position.
                while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }

        // return the ArrayList that holds the data collected from
        // the database.
        return dataArrays;
    }


    /**
     * *******************************************************************
     * RETRIEVING A ROW FROM THE DATABASE TABLE
     * <p/>
     * This is an example of how to retrieve a row from a database table
     * using this class.  You should edit this method to suit your needs.
     *
     * @param rowID the id of the row to retrieve
     * @return an array containing the data from the row
     */
    public ArrayList<Object> getRowAsArray(long rowID) {
        // create an array list to store data from the database row.
        // I would recommend creating a JavaBean compliant object
        // to store this data instead.  That way you can ensure
        // data types are correct.
        ArrayList<Object> rowArray = new ArrayList<Object>();
        Cursor cursor;

        try {
            // this is a database call that creates a "cursor" object.
            // the cursor object store the information collected from the
            // database and is used to iterate through the data.
            cursor = db.query(
                    TABLE_NAME,
                    new String[]{TABLE_ROW_ID, TABLE_ROW_ONE, TABLE_ROW_TWO, TABLE_ROW_THREE, TABLE_ROW_FOUR, TABLE_ROW_FIVE, TABLE_ROW_SIX},
                    TABLE_ROW_ID + "=" + rowID,
                    null, null, null, null, null
            );

            // move the pointer to position zero in the cursor.
            cursor.moveToFirst();

            // if there is data available after the cursor's pointer, add
            // it to the ArrayList that will be returned by the method.
            if (!cursor.isAfterLast()) {
                do {
                    rowArray.add(cursor.getLong(0));
                    rowArray.add(cursor.getString(1));
                    rowArray.add(cursor.getString(2));
                    rowArray.add(cursor.getString(3));
                    rowArray.add(cursor.getString(4));
                    rowArray.add(cursor.getString(5));
                    rowArray.add(cursor.getBlob(6));
                }
                while (cursor.moveToNext());
            }

            // let java know that you are through with the cursor.
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }

        // return the ArrayList containing the given row from the database.
        return rowArray;
    }


}