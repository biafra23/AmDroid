package com.jaeckel.amdroid.api.model;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
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

  private transient final Logger log = LoggerFactory.getLogger("Amen");

  private Long      id;
  private Long      userId;
  private User      user;
  private Date      createdAt;
  private Integer   kindId; //normal, amen, dispute
  private Statement statement;


  //sometimes disputed Amen
  private Amen referringAmen;
  
  public Amen() {
    
  }

  public Amen(Statement statement) {
    this.statement = statement;
    this.kindId = 0;
  }

  public Amen(JSONObject object) {

//    log.warn("-----> Amen(): " + object);

    try {

      if (object.has("created_at")) {
        final String created_at = (String) object.get("created_at");
        Date date = parseIso8601DateJoda(created_at);
        this.setCreatedAt(date);
      }

      this.setId(object.getLong("id"));

//      final Object kind_id = object.get("kind_id");
//      if (object.has("kind_id") && object.getInt("kind_id") != 0) {
      if (object.has("kind_id")) {
        final int kind_id1 = object.getInt("kind_id");

        this.setKindId(kind_id1);
      }

      this.setUserId(object.getLong("user_id"));

      User user = new User(object.getJSONObject("user"));
      this.setUser(user);

      Statement statement = new Statement(object.getJSONObject("statement"));
      this.setStatement(statement);


      if (object.has("referring_amen")) {
        final JSONObject referring_amen = object.getJSONObject("referring_amen");
        Amen ref_amen = new Amen(referring_amen);

//        log.warn("-----> Amen() ref_amen: " + ref_amen);
//        log.warn("-----> Amen() ref_amen NOT: " + this);
//            log.warn();
        this.referringAmen = ref_amen;
      }

    } catch (JSONException e) {
//      log.error("-----> Amen(): " + object);
      throw new RuntimeException("Error parsing Amen", e);
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
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

  public void setKindId(Integer kindId) {
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

  public String json() {

    GsonBuilder builder = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    return builder.create().toJson(this);
  }

  private Date parseIso8601DateNoBind(String dateString) {
    try {
      return javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(dateString).toGregorianCalendar().getTime();
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException("Error converting timestamp", e);
    }
  }

  public static Date parseIso8601DateJoda(String dateString) {
    //2011-09-24T22:23:26Z
    DateTimeFormatter fmt = ISODateTimeFormat.dateTimeParser();
    DateTime dt = fmt.parseDateTime(dateString);
    return dt.toDate();
  }

  public boolean hasDispute() {

    if (referringAmen != null
        && referringAmen.getStatement() != null
        && referringAmen.getStatement().isAgreeable() != null
        && referringAmen.getStatement().isAgreeable()) {
      return true;
    }
    return false;
  }

  public Objekt disputingObjekt() {
    if (hasDispute()) {
      return referringAmen.getStatement().getObjekt();
    }
    return null;
  }

  public User disputingUser() {
    if (hasDispute()) {
      return referringAmen.getUser();
    }
    return null;
  }
}

