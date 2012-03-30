package com.jaeckel.amenoid.db;

import java.util.List;

import android.database.Cursor;
import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.api.model.Objekt;
import com.jaeckel.amenoid.api.model.Statement;
import com.jaeckel.amenoid.api.model.Topic;

/**
 * User: biafra
 * Date: 1/15/12
 * Time: 1:05 AM
 */
public interface AmenDao {


  public void insertOrUpdate(Amen amen);
  public void insertOrUpdate(Statement amen);
  public void insertOrUpdate(Topic topic);
  public void insertOrUpdate(Objekt objekt);
  public void insertOrUpdate(List<Amen> amen);
  public void insertOrUpdateStatements(List<Statement> statements);

  public Amen findById(Long id);
  public Statement findStatementById(Long id);

  public void delete(Long id);
  public Cursor currentAmen();

}
