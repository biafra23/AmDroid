package com.jaeckel.amenoid.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * User: biafra
 * Date: 11/29/11
 * Time: 10:39 PM
 */
public class StatementsProvider extends ContentProvider {

  AmenDBAdapter dbAdapter;

  @Override
  public boolean onCreate() {

    dbAdapter = new AmenDBAdapter(getContext());

    return false;
  }

  @Override
  public int delete(Uri uri, String where, String[] whereArgs) {
    return 0;
  }

  @Override
  public String getType(Uri uri) {
    return null;
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    return null;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection,
                      String[] selectionArgs, String sortOrder) {


    //TODO: where do we fill the database?
    return dbAdapter.getAllEntries();
  }

  @Override
  public int update(Uri uri, ContentValues values, String where,
                    String[] whereArgs) {
    return 0;
  }
}
