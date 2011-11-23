package com.jaeckel.amenoid.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.jaeckel.amenoid.api.model.Amen;

/**
 * User: biafra
 * Date: 11/22/11
 * Time: 11:16 PM
 */
public class AmenDBAdapter {

  private static final String DATABASE_NAME    = "amen.db";
  private static final String DATABASE_TABLE   = "amen";
  private static final int    DATABASE_VERSION = 1;
  // The index (key) column name for use in where clauses.
  public static final  String KEY_ID           = "_id";
  // The name and column index of each column in your database.
  public static final  String KEY_NAME         = "name";
  public static final  int    NAME_COLUMN      = 1;

  //RESTful fields
  // server http response result 200, 500 etc
  public static final String KEY_RESULT = "result";
  // the transitional state: pending, complete
  public static final String KEY_STATUS = "status";

  // TODO: Create public field for each column in your table.
  public static final String KEY_USER_ID                          = "user_id";
  public static final String KEY_USER_NAME                        = "user_name";
  public static final String KEY_CREATED_AT                       = "created_at";
  public static final String KEY_KIND_ID                          = "kind_id";
  public static final String KEY_REFERRING_AMEN_ID                = "refering_amen_id";
  public static final String KEY_STATEMENT_ID                     = "statement_id";
  public static final String KEY_STATEMENT_AGREEABLE              = "statement_agreeable";
  public static final String KEY_STATEMENT_FIRST_POSTER_NAME      = "statement_first_poster_name";
  public static final String KEY_STATEMENT_FIRST_POSTER_ID        = "statement_first_poster_id";
  public static final String KEY_STATEMENT_FIRST_POSTED_AT        = "statement_first_posted_at";
  public static final String KEY_STATEMENT_FIRST_AMEN_ID          = "statement_first_amen_id";
  public static final String KEY_STATEMENT_FIRST_TOPIC_ID         = "statement_topic_id";
  public static final String KEY_STATEMENT_TOPIC_BEST             = "statement_topic_best";
  public static final String KEY_STATEMENT_TOPIC_DESCRIPTION      = "statement_topic_description";
  public static final String KEY_STATEMENT_TOPIC_SCOPE            = "statement_topic_scope";
  public static final String KEY_STATEMENT_TOPIC_OBJEKTS_COUNT    = "statement_topic_objekts_count";
  public static final String KEY_STATEMENT_TOPIC_AMEN_AS_SENTENCE = "statement_topic_as_sentence";
  public static final String KEY_STATEMENT_OBJEKT_ID              = "statement_objekt_id";
  public static final String KEY_STATEMENT_OBJEKT_KIND_ID         = "statement_objekt_kind_id";
  public static final String KEY_STATEMENT_OBJEKT_NAME            = "statement_objekt_name";
  public static final String KEY_STATEMENT_OBJEKT_CATEGORY        = "statement_objekt_category";

  // SQL Statement to create a new database.
  private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE
                                                + " (" + KEY_ID + " integer primary key autoincrement"
                                                + ", " + KEY_NAME + " text"
                                                + ", " + KEY_RESULT + " text"
                                                + ", " + KEY_STATUS + " text"
                                                + ", " + KEY_USER_ID + " text"
                                                + ", " + KEY_USER_NAME + " text"
                                                + ", " + KEY_CREATED_AT + " text"
                                                + ", " + KEY_KIND_ID + " text"
                                                + ", " + KEY_REFERRING_AMEN_ID + " text"
                                                + ", " + KEY_STATEMENT_ID + " text"
                                                + ", " + KEY_STATEMENT_FIRST_POSTER_NAME + " text"
                                                + ", " + KEY_STATEMENT_FIRST_POSTER_ID + " text"
                                                + ", " + KEY_STATEMENT_AGREEABLE + " text"
                                                + ", " + KEY_STATEMENT_FIRST_POSTED_AT + " text"
                                                + ", " + KEY_STATEMENT_FIRST_AMEN_ID + " text"
                                                + ", " + KEY_STATEMENT_FIRST_TOPIC_ID + " text"
                                                + ", " + KEY_STATEMENT_TOPIC_BEST + " text"
                                                + ", " + KEY_STATEMENT_TOPIC_DESCRIPTION + " text"
                                                + ", " + KEY_STATEMENT_TOPIC_SCOPE + " text"
                                                + ", " + KEY_STATEMENT_OBJEKT_NAME + " text"
                                                + ");";

  // Variable to hold the database instance
  private       SQLiteDatabase db;
  // Context of the application using the database.
  private final Context        context;
  // Database open/upgrade helper
  private       AmenDbHelper   dbHelper;
  private static final String TAG = "AmenDbAdapter";

  public AmenDBAdapter(Context _context) {
    Log.i(TAG, "AmenDBAdapter()");

    context = _context;
    dbHelper = new AmenDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);

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


  private class AmenDbHelper extends SQLiteOpenHelper {

    // Database open/upgrade helper private myDbHelper dbHelper;
    public AmenDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
      super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
      
      Log.i(TAG, "AmenDbHelper.onCreate");
      sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
  }
}
