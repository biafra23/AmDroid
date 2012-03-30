package com.jaeckel.amenoid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AmenDBHelper {
  class AmenDbHelper extends SQLiteOpenHelper {

    // Database open/upgrade helper private myDbHelper dbHelper;
    public AmenDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
      null.SQLiteOpenHelper(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

      Log.i(AmenDBAdapter.TAG, "AmenDbHelper.onCreate");
      sqLiteDatabase.execSQL(AmenDBAdapter.DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
  }
}