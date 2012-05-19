package com.jaeckel.amenoid.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.jaeckel.amenoid.util.Log;
import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.api.model.Objekt;
import com.jaeckel.amenoid.api.model.Statement;
import com.jaeckel.amenoid.api.model.Topic;
import com.jaeckel.amenoid.api.model.User;

import java.util.List;

/**
 * User: biafra
 * Date: 1/15/12
 * Time: 1:08 AM
 */
public class AmenDaoImpl implements AmenDao {

  public final static String TAG = "AmenDaoImpl";

  private AmenDBHelper dbHelper;

  AmenDaoImpl(AmenDBHelper dbHelper) {
    this.dbHelper = dbHelper;
  }

  public void insertOrUpdate(Amen amen) {

    SQLiteDatabase db = dbHelper.getWritableDatabase();

    Amen exists = findById(amen.getId());

    ContentValues cv = new ContentValues();
    cv.put(AmenDBHelper.KEY_ID, amen.getId());
    cv.put(AmenDBHelper.KEY_USER_ID, amen.getUserId());
    cv.put(AmenDBHelper.KEY_STATEMENT_ID, amen.getStatement().getId());
    cv.put(AmenDBHelper.KEY_CREATED_AT, amen.getCreatedAt().getTime());

    if (exists == null) {

      Log.d(TAG, "Inserting amen: " + amen);
      db.insert(AmenDBHelper.AMEN_TABLE, "name", cv);
    } else {
      Log.d(TAG, "Updating amen: " + amen);
      db.update(AmenDBHelper.AMEN_TABLE, cv, AmenDBHelper.KEY_ID + "=?", new String[]{amen.getId() + ""});
    }

    if (amen.getStatement() != null) {
      insertOrUpdate(amen.getStatement());
    }
    if (amen.getUser() != null) {
      insertOrUpdate(amen.getUser());
    }
  }

  public void insertOrUpdate(Statement statement) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    Statement exists = findStatementById(statement.getId());
    ContentValues cv = new ContentValues();
    cv.put(AmenDBHelper.KEY_ID, statement.getId());
    cv.put(AmenDBHelper.KEY_FIRST_AMEN_ID, statement.getFirstAmenId());
    cv.put(AmenDBHelper.KEY_TOTAL_AMEN_COUNT, statement.getTotalAmenCount());
    if (statement.getFirstPostedAt() != null) {
      cv.put(AmenDBHelper.KEY_FIRST_POSTED_AT, statement.getFirstPostedAt().getTime());
    }
    if (statement.getTopic() != null) {
      cv.put(AmenDBHelper.KEY_TOPIC_ID, statement.getTopic().getId());
    }
    if (statement.getObjekt() != null) {
      cv.put(AmenDBHelper.KEY_OBJEKT_ID, statement.getObjekt().getId());
    }
    if (statement.getFirstPoster() != null) {
      cv.put(AmenDBHelper.KEY_FIRST_POSTER_ID, statement.getFirstPoster().getId());
    }

    if (exists == null) {
      Log.d(TAG, "Inserting statement: " + statement);
      db.insert(AmenDBHelper.STATEMENTS_TABLE, "name", cv);

    } else {

      Log.d(TAG, "Updating statement: " + statement);
      db.update(AmenDBHelper.STATEMENTS_TABLE, cv, AmenDBHelper.KEY_ID + "=?", new String[]{statement.getId() + ""});
    }

    if (statement.getTopic() != null) {
      insertOrUpdate(statement.getTopic());
    }

    if (statement.getObjekt() != null) {
      insertOrUpdate(statement.getObjekt());
    }


  }

  public void insertOrUpdate(Topic topic) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    Topic exists = findTopicById(topic.getId());
    ContentValues cv = new ContentValues();
    cv.put(AmenDBHelper.KEY_ID, topic.getId());
    cv.put(AmenDBHelper.KEY_DESCRIPTION, topic.getDescription());
    cv.put(AmenDBHelper.KEY_AS_SENTENCE, topic.getAsSentence());
    cv.put(AmenDBHelper.KEY_OBJEKTS_COUNT, topic.getObjektsCount());
    cv.put(AmenDBHelper.KEY_SCOPE, topic.getScope());
    cv.put(AmenDBHelper.KEY_BEST, topic.isBest());

    if (exists == null) {
      Log.d(TAG, "Inserting topic: " + topic);
      db.insert(AmenDBHelper.TOPICS_TABLE, "name", cv);

    } else {

      Log.d(TAG, "Updating topic: " + topic);
      db.update(AmenDBHelper.TOPICS_TABLE, cv, AmenDBHelper.KEY_ID + "=?", new String[]{topic.getId() + ""});
    }
  }

  public void insertOrUpdate(Objekt objekt) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    Objekt exists = findObjektById(objekt.getId());
    ContentValues cv = new ContentValues();
    cv.put(AmenDBHelper.KEY_ID, objekt.getId());
    cv.put(AmenDBHelper.KEY_NAME, objekt.getName());
    cv.put(AmenDBHelper.KEY_CATEGORY, objekt.getCategory());
    cv.put(AmenDBHelper.KEY_KIND_ID, objekt.getKindId());

    if (exists == null) {
      Log.d(TAG, "Inserting objekt: " + objekt);
      db.insert(AmenDBHelper.OBJEKTS_TABLE, "name", cv);

    } else {

      Log.d(TAG, "Updating objekt: " + objekt);
      db.update(AmenDBHelper.OBJEKTS_TABLE, cv, AmenDBHelper.KEY_ID + "=?", new String[]{objekt.getId() + ""});
    }
  }

  public void insertOrUpdate(User user) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    User exists = findUserById(user.getId());
    ContentValues cv = new ContentValues();
    cv.put(AmenDBHelper.KEY_ID, user.getId());
    cv.put(AmenDBHelper.KEY_NAME, user.getName());

    if (exists == null) {
      Log.d(TAG, "Inserting user: " + user);
      db.insert(AmenDBHelper.USERS_TABLE, "name", cv);

    } else {

      Log.d(TAG, "Updating user: " + user);
      db.update(AmenDBHelper.USERS_TABLE, cv, AmenDBHelper.KEY_ID + "=?", new String[]{user.getId() + ""});
    }
  }


  public void insertOrUpdate(List<Amen> amen) {
    for (Amen a : amen) {
      insertOrUpdate(a);
    }
  }

  public void insertOrUpdateStatements(List<Statement> statements) {
    for (Statement a : statements) {
      insertOrUpdate(a);
    }
  }

  private User findUserById(long id) {
    SQLiteDatabase db = dbHelper.getReadableDatabase();
    Cursor cursor = db.query(AmenDBHelper.USERS_TABLE, null, AmenDBHelper.KEY_ID + "=?", new String[]{"" + id}, null, null, null);

    if (cursor.getCount() == 0) {
      return null;
    }
    cursor.moveToFirst();
    User result = new User();
    result.setId(cursor.getLong(cursor.getColumnIndex(AmenDBHelper.KEY_ID)));
//TODO: add more fields
    return result;
  }

  public Statement findStatementById(Long id) {

    SQLiteDatabase db = dbHelper.getReadableDatabase();
    Cursor cursor = db.query(AmenDBHelper.STATEMENTS_TABLE, null, AmenDBHelper.KEY_ID + "=?", new String[]{"" + id}, null, null, null);

    if (cursor.getCount() == 0) {
      return null;
    }
    cursor.moveToFirst();
    Statement result = new Statement();
    result.setId(cursor.getLong(cursor.getColumnIndex(AmenDBHelper.KEY_ID)));
//TODO: add more fields
    return result;
  }

  public Amen findById(Long id) {

    SQLiteDatabase db = dbHelper.getReadableDatabase();
    Cursor cursor = db.query(AmenDBHelper.AMEN_TABLE, null, AmenDBHelper.KEY_ID + "=?", new String[]{"" + id}, null, null, null);

    if (cursor.getCount() == 0) {
      return null;
    }
    cursor.moveToFirst();
    Amen result = new Amen();
    result.setId(cursor.getLong(cursor.getColumnIndex(AmenDBHelper.KEY_ID)));
    result.setUserId(cursor.getLong(cursor.getColumnIndex(AmenDBHelper.KEY_USER_ID)));
//TODO: add more fields

    return result;
  }

  public Topic findTopicById(Long id) {

    SQLiteDatabase db = dbHelper.getReadableDatabase();
    Cursor cursor = db.query(AmenDBHelper.TOPICS_TABLE, null, AmenDBHelper.KEY_ID + "=?", new String[]{"" + id}, null, null, null);

    if (cursor.getCount() == 0) {
      return null;
    }
    cursor.moveToFirst();
    Topic result = new Topic();
    result.setId(cursor.getLong(cursor.getColumnIndex(AmenDBHelper.KEY_ID)));
//TODO: add more fields

    return result;
  }

  public Objekt findObjektById(Long id) {

    SQLiteDatabase db = dbHelper.getReadableDatabase();
    Cursor cursor = db.query(AmenDBHelper.OBJEKTS_TABLE, null, AmenDBHelper.KEY_ID + "=?", new String[]{"" + id}, null, null, null);

    if (cursor.getCount() == 0) {
      return null;
    }
    cursor.moveToFirst();
    Objekt result = new Objekt();
    result.setId(cursor.getLong(cursor.getColumnIndex(AmenDBHelper.KEY_ID)));
//TODO: add more fields

    return result;
  }

  public void delete(Long id) {

  }

  public Cursor currentAmen() {
//SELECT t1.*, t2.*, t3.*
//FROM table1 t1 INNER JOIN table2 t2
//    ON t1.id1 = t2.id1
//INNER JOIN table3 t3
//    ON t2.id2 = t3.id2

    SQLiteDatabase db = dbHelper.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT a.created_at, s.total_amen_count, u.name as user_name, t.scope, t.best, o.kind_id, t.description, o.name as objekt_name "
                                + "FROM " + AmenDBHelper.AMEN_TABLE + " a "
                                + " INNER JOIN " + AmenDBHelper.STATEMENTS_TABLE + " s"
                                + " ON a." + AmenDBHelper.KEY_STATEMENT_ID + " = s." + AmenDBHelper.KEY_ID
                                + " INNER JOIN " + AmenDBHelper.TOPICS_TABLE + " t"
                                + " ON s." + AmenDBHelper.KEY_TOPIC_ID + " = t." + AmenDBHelper.KEY_ID
                                + " INNER JOIN " + AmenDBHelper.OBJEKTS_TABLE + " o"
                                + " ON s." + AmenDBHelper.KEY_OBJEKT_ID + " = o." + AmenDBHelper.KEY_ID
                                + " INNER JOIN " + AmenDBHelper.USERS_TABLE + " u"
                                + " ON a." + AmenDBHelper.KEY_USER_ID + " = u." + AmenDBHelper.KEY_ID
      , new String[]{});


    return cursor;
  }
}
