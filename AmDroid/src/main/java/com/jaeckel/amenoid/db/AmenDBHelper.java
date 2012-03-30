package com.jaeckel.amenoid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AmenDBHelper extends SQLiteOpenHelper {


  public final static String TAG = "AmenDBHelper";

  public final static String AMEN_TABLE             = "amen";
  public final static String STATEMENTS_TABLE       = "statements";
  public final static String TOPICS_TABLE           = "topics";
  public final static String OBJEKTS_TABLE          = "objekts";
  public final static String USERS_TABLE            = "users";
  public final static String USERS_STATEMENTS_TABLE = "user_statements";

  public final static String KEY_ID      = "id";
  public final static String KEY_KIND_ID = "kind_id";

  //REST_RESULT (success or failure)
  public final static String KEY_RESULT = "rest_result";
  //REST_STATUS (pending or done)
  public final static String KEY_STATUS = "rest_status";

  // AMEN //
  public final static String KEY_NAME              = "name";
  public final static String KEY_USER_ID           = "user_id";
  public final static String KEY_CREATED_AT        = "created_at";
  public final static String KEY_REFERRING_AMEN_ID = "referring_amen_id";
  public final static String KEY_STATEMENT_ID      = "statement_id";

  // USERS //
  public final static String KEY_PICTURE                  = "picture";
  public final static String KEY_PHOTO                    = "photo";
  public final static String KEY_CREATED_STATEMENTS_COUNT = "created_statements_count";
  public final static String KEY_GIVEN_AMEN_COUNT         = "created_amen_count";
  public final static String KEY_RECEIVED_AMEN_COUNT      = "received_amen_count";
  public final static String KEY_FOLLOWERS_COUNT          = "followers_count";
  public final static String KEY_FOLLOWING_COUNT          = "following_count";
  public final static String KEY_FOLLOWING                = "following";
  public final static String KEY_AUTH_TOKEN               = "auth_token";
  public final static String KEY_BIO                      = "bio";
  public final static String KEY_USER_NAME                = "user_name";

  // STATEMENTS //
  public final static String KEY_TOTAL_AMEN_COUNT = "total_amen_count";
  public final static String KEY_AGREEABLE        = "agreeable";
  public final static String KEY_TOPIC_ID         = "topic_id";
  public final static String KEY_OBJEKT_ID        = "objekt_id";
  public final static String KEY_FIRST_POSTER_ID  = "first_poster_id";
  public final static String KEY_FIRST_POSTED_AT  = "first_posted_at";
  public final static String KEY_FIRST_AMEN_ID    = "first_amen_id";


  // OBJEKTS //
  public final static String KEY_CATEGORY              = "category";
  public final static String KEY_DEFAULT_DESCRIPTION   = "default_description";
  public final static String KEY_DEFAULT_SCOPE         = "default_scope";
  public final static String KEY_POSSIBLE_DESCRIPTIONS = "possible_descriptions";
  public final static String KEY_POSSIBLE_SCOPES       = "possible_scopes";


  // TOPICS //
  public final static String KEY_DESCRIPTION   = "description";
  public final static String KEY_BEST          = "best";
  public static final String KEY_SCOPE         = "scope";
  public static final String KEY_OBJEKTS_COUNT = "objekts_count";
  public static final String KEY_AS_SENTENCE   = "as_sentence";


  // SQL Statement to create a new database.
  private static final String AMEN_CREATE = "create table " + AMEN_TABLE
                                            + " (" + KEY_ID + " integer primary key "
                                            + ", " + KEY_RESULT + " text"
                                            + ", " + KEY_STATUS + " text"
                                            + ", " + KEY_USER_ID + " integer"
                                            + ", " + KEY_CREATED_AT + " integer"
                                            + ", " + KEY_KIND_ID + " integer"
                                            + ", " + KEY_REFERRING_AMEN_ID + " integer"
                                            + ", " + KEY_STATEMENT_ID + " integer"
                                            + ");";

  private static final String USERS_CREATE = "create table " + USERS_TABLE
                                             + " (" + KEY_ID + " integer primary key "
                                             + ", " + KEY_NAME + " text"
                                             + ", " + KEY_RESULT + " text"
                                             + ", " + KEY_STATUS + " text"
                                             + ", " + KEY_PICTURE + " text"
                                             + ", " + KEY_PHOTO + " text"
                                             + ", " + KEY_CREATED_AT + " integer"
                                             + ", " + KEY_CREATED_STATEMENTS_COUNT + " integer"
                                             + ", " + KEY_GIVEN_AMEN_COUNT + " integer"
                                             + ", " + KEY_RECEIVED_AMEN_COUNT + " integer"
                                             + ", " + KEY_FOLLOWERS_COUNT + " integer"
                                             + ", " + KEY_FOLLOWING_COUNT + " integer"
                                             + ", " + KEY_FOLLOWING + " integer" //boolean
                                             + ", " + KEY_AUTH_TOKEN + " text" //boolean
                                             + ", " + KEY_BIO + " text"
                                             + ", " + KEY_USER_NAME + " text"
                                             + ");";

  private static final String STATEMENTS_CREATE = "create table " + STATEMENTS_TABLE
                                                  + " (" + KEY_ID + " integer primary key "
                                                  + ", " + KEY_RESULT + " text"
                                                  + ", " + KEY_STATUS + " text"

                                                  + ", " + KEY_TOTAL_AMEN_COUNT + " integer"
                                                  + ", " + KEY_AGREEABLE + " text"

                                                  + ", " + KEY_TOPIC_ID + " integer"
                                                  + ", " + KEY_OBJEKT_ID + " integer"

                                                  + ", " + KEY_FIRST_POSTER_ID + " integer"
                                                  + ", " + KEY_FIRST_POSTED_AT + " integer"
                                                  + ", " + KEY_FIRST_AMEN_ID + " integer"


                                                  + ");";


  private static final String OBJEKTS_CREATE = "create table " + OBJEKTS_TABLE
                                               + " (" + KEY_ID + " integer primary key "
                                               + ", " + KEY_NAME + " text"
                                               + ", " + KEY_RESULT + " text"
                                               + ", " + KEY_STATUS + " text"

                                               + ", " + KEY_KIND_ID + " integer"
                                               + ", " + KEY_CATEGORY + " text"
                                               + ", " + KEY_DEFAULT_DESCRIPTION + " text"
                                               + ", " + KEY_DEFAULT_SCOPE + " text"
                                               + ", " + KEY_POSSIBLE_DESCRIPTIONS + " text"
                                               + ", " + KEY_POSSIBLE_SCOPES + " text"

                                               + ");";

  private static final String TOPICS_CREATE = "create table " + TOPICS_TABLE
                                              + " (" + KEY_ID + " integer primary key "
                                              + ", " + KEY_RESULT + " text"
                                              + ", " + KEY_STATUS + " text"
                                              + ", " + KEY_BEST + " integer"
                                              + ", " + KEY_DESCRIPTION + " text"
                                              + ", " + KEY_SCOPE + " text"
                                              + ", " + KEY_OBJEKTS_COUNT + " integer"
                                              + ", " + KEY_AS_SENTENCE + " text"
                                              + ");";


  private static final String USERS_STATEMENTS_CREATE = "create table " + USERS_STATEMENTS_TABLE
                                                        + " (" + KEY_ID + " integer primary key autoincrement"
                                                        + ", " + KEY_USER_ID + " integer"
                                                        + ", " + KEY_STATEMENT_ID + " integer"
                                                        + ");";


  public AmenDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
    super(context, name, factory, version);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {

    Log.i(TAG, "AmenDbHelper.onCreate()");
    sqLiteDatabase.execSQL(AMEN_CREATE);
    sqLiteDatabase.execSQL(USERS_CREATE);
    sqLiteDatabase.execSQL(STATEMENTS_CREATE);
    sqLiteDatabase.execSQL(OBJEKTS_CREATE);
    sqLiteDatabase.execSQL(TOPICS_CREATE);
    sqLiteDatabase.execSQL(USERS_STATEMENTS_CREATE);


  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int j) {
    Log.i(TAG, "AmenDbHelper.onUpgrade(" + i + " -> " + j);

  }

}
