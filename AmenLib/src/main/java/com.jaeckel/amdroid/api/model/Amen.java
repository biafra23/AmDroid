package com.jaeckel.amdroid.api.model;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.Date;

/**
 * User: biafra
 * Date: 9/23/11
 * Time: 8:36 PM
 */
public class Amen {

  final Logger log = LoggerFactory.getLogger("Amen");

  private long      id;
  private long      userId;
  private User      user;
  private Date      createdAt;
  private int       kindId; //normal, amen, dispute
  private Statement statement;

  private Amen referringAmen;


  public Amen(JSONObject object) {

    try {

      if (object.has("created_at")) {
        final String created_at = (String) object.get("created_at");
        Date date = parseIso8601DateJoda(created_at);
        this.setCreatedAt(date);
      }

      this.setId(object.getInt("id"));
      this.setKindId(object.getInt("kind_id"));
      this.setUserId(object.getInt("user_id"));

      User user = new User(object.getJSONObject("user"));
      this.setUser(user);

      Statement statement = new Statement(object.getJSONObject("statement"));
      this.setStatement(statement);


      if (object.has("referring_amen")) {
        final JSONObject referring_amen = object.getJSONObject("referring_amen");
        this.referringAmen = new Amen(referring_amen);
      }

    } catch (JSONException e) {
      throw new RuntimeException("Error parsing Amen", e);
    }
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public int getKindId() {
    return kindId;
  }

  public void setKindId(int kindId) {
    this.kindId = kindId;
  }

  public Statement getStatement() {
    return statement;
  }

  public void setStatement(Statement statement) {
    this.statement = statement;
  }

  public Amen getReferringAmen() {
    return referringAmen;
  }

  public void setReferringAmen(Amen referringAmen) {
    this.referringAmen = referringAmen;
  }

  @Override
  public String toString() {
    return "Amen{" +
           "id=" + id +
           ", userId=" + userId +
           ", user=" + user +
           ", createdAt=" + createdAt +
           ", kindId=" + kindId +
           ", statement=" + statement +
           ", referringAmen=" + referringAmen +
           '}';
  }

  private Date parseIso8601DateNoBind(String dateString) {
    try {
      return javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(dateString).toGregorianCalendar().getTime();
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException("Error converting timestamp", e);
    }
  }

  private Date parseIso8601DateJoda(String dateString) {
    //2011-09-24T22:23:26Z
    DateTimeFormatter fmt = ISODateTimeFormat.dateTimeParser();
    DateTime dt = fmt.parseDateTime(dateString);
    return dt.toDate();
  }
}

