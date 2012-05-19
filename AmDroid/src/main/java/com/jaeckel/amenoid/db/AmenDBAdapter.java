package com.jaeckel.amenoid.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.jaeckel.amenoid.util.Log;
import com.jaeckel.amenoid.api.model.Amen;

/**
 * User: biafra
 * Date: 11/22/11
 * Time: 11:16 PM
 */
public class AmenDBAdapter {


  private AmenDao amenDao;


  private static final String DATABASE_NAME    = "amen.db";
  private static final String DATABASE_TABLE   = "amen";
  private static final int    DATABASE_VERSION = 1;
  // The index (key) column name for use in where clauses.
  public static final  String KEY_ID           = "_id";
  // The name and column index of each column in your database.
  public static final  String KEY_NAME         = "name";
  public static final  int    NAME_COLUMN      = 1;


  // Variable to hold the database instance
  private       SQLiteDatabase db;
  // Context of the application using the database.
  private final Context        context;
  // Database open/upgrade helper
  private       AmenDBHelper   dbHelper;
  private static final String TAG = "AmenDbAdapter";

  public AmenDBAdapter(Context _context) {
    Log.i(TAG, "AmenDBAdapter()");

    context = _context;
    dbHelper = new AmenDBHelper(context, DATABASE_NAME, null, DATABASE_VERSION);

    amenDao = new AmenDaoImpl(dbHelper);

  }

  public AmenDBAdapter open() throws SQLException {
    db = dbHelper.getWritableDatabase();
    return this;
  }


  public void close() {
    db.close();
  }

  public int insertEntry(Amen _myObject) {
    // TODO: Create a new ContentValues to represent my row
    // and insert it into the database.
//    return index;
    return 0;
  }

  public boolean removeEntry(long _rowIndex) {
    return db.delete(DATABASE_TABLE, KEY_ID + "=" + _rowIndex, null) > 0;
  }

  public Cursor getAllEntries() {
    return db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_NAME},
                    null, null, null, null, null);
  }

  public Amen getEntry(long _rowIndex) {
    // TODO: Return a cursor to a row from the database and
    // use the values to populate an instance of MyObject


//    return objectInstance;
    return null;
  }

  public boolean updateEntry(long _rowIndex, Amen _myObject) {
    // TODO: Create a new ContentValues based on the new object // and use it to update a row in the database.
    return true;
  }

  public AmenDao getAmenDao() {
    return amenDao;
  }

  public void setAmenDao(AmenDao amenDao) {
    this.amenDao = amenDao;
  }
}
