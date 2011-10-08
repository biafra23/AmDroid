package com.jaeckel.amdroid.api.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.jaeckel.amdroid.api.AmenService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.Date;

/**
 * User: biafra
 * Date: 9/23/11
 * Time: 8:36 PM
 */
public class Amen implements Parcelable {

  private transient final Logger log = LoggerFactory.getLogger("Amen");


  private Long      id;
  private Long      userId;
  private User      user;
  private Date      createdAt;
  private Integer   kindId; //normal, amen, dispute ??
  private Statement statement;
  //sometimes disputed Amen
  private Amen      referringAmen;
  private Long      referringAmenId;

  private static final String TAG = "Amen";

  public Amen() {

  }

  public Amen(Amen amen, Objekt objekt) {

    this.kindId = AmenService.AMEN_KIND_DISPUTE;
    this.referringAmenId = amen.getId();

    this.statement = amen.getStatement();
    this.statement.setAgreeingNetwork(null);
    this.statement.setObjekt(objekt);
//    this.statement.getObjekt().setKey(null);
    this.statement.setTotalAmenCount(null);


    this.statement.getTopic().setId(null);
    this.statement.getTopic().setObjektsCount(null);
  }

  public Amen(Statement statement, Objekt objekt, Long referringAmenId) {

    this.kindId = AmenService.AMEN_KIND_DISPUTE;
    this.referringAmenId = referringAmenId;

    this.statement = statement;
    this.statement.setAgreeingNetwork(null);
    this.statement.setObjekt(objekt);
    //    this.statement.getObjekt().setKey(null);
    this.statement.setTotalAmenCount(null);
    this.statement.getTopic().setId(null);
    this.statement.getTopic().setObjektsCount(null);
  }

  public Amen(Statement statement) {

    this.statement = statement;
    this.kindId = 0;
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

  public boolean isDispute() {
    return getKindId() == AmenService.AMEN_KIND_AMEN;
  }

  public boolean isAmen() {
    return getKindId() == AmenService.AMEN_KIND_AMEN;
  }

  public Objekt disputingObjekt() {
    if (isDispute()) {
      return referringAmen.getStatement().getObjekt();
    }
    return null;
  }

  public User disputingUser() {
    if (isDispute()) {
      return referringAmen.getUser();
    }
    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Amen amen = (Amen) o;

    if (createdAt != null ? !createdAt.equals(amen.createdAt) : amen.createdAt != null) return false;
    if (id != null ? !id.equals(amen.id) : amen.id != null) return false;
    if (kindId != null ? !kindId.equals(amen.kindId) : amen.kindId != null) return false;
    if (log != null ? !log.equals(amen.log) : amen.log != null) return false;
    if (referringAmen != null ? !referringAmen.equals(amen.referringAmen) : amen.referringAmen != null) return false;
    if (statement != null ? !statement.equals(amen.statement) : amen.statement != null) return false;
    if (user != null ? !user.equals(amen.user) : amen.user != null) return false;
    if (userId != null ? !userId.equals(amen.userId) : amen.userId != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = log != null ? log.hashCode() : 0;
    result = 31 * result + (id != null ? id.hashCode() : 0);
    result = 31 * result + (userId != null ? userId.hashCode() : 0);
    result = 31 * result + (user != null ? user.hashCode() : 0);
    result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
    result = 31 * result + (kindId != null ? kindId.hashCode() : 0);
    result = 31 * result + (statement != null ? statement.hashCode() : 0);
    result = 31 * result + (referringAmen != null ? referringAmen.hashCode() : 0);
    return result;
  }


  /*
  *
  *   PARCEL STUFF
  *
  */

  public static final Parcelable.Creator<Amen> CREATOR = new Creator<Amen>() {

    public Amen[] newArray(int size) {
      return new Amen[size];
    }

    public Amen createFromParcel(Parcel source) {
      return new Amen(source);
    }
  };

  private Amen(Parcel in) {
    readFromParcel(in);
  }

  @Override
  public int describeContents() {
    return 0;
  }


  @Override
  public void writeToParcel(Parcel dest, int flags) {
    Log.d(TAG, "writeToParcel");
    if (dest == null) {
      throw new RuntimeException("Parcel must not be null");

    }
    dest.writeValue(id);
    dest.writeValue(userId);
    dest.writeParcelable(user, flags);
    dest.writeValue(kindId);
    dest.writeValue(createdAt);
    dest.writeParcelable(statement, flags);
    dest.writeParcelable(referringAmen, flags);
    dest.writeValue(referringAmenId);

    Log.d(TAG, "writeToParcel. done.‚");

  }

  private void readFromParcel(Parcel in) {
    final ClassLoader cl = getClass().getClassLoader();

    id = (Long) in.readValue(cl);
    userId = (Long) in.readValue(cl);
    user = in.readParcelable(cl);
    kindId = (Integer) in.readValue(cl);
    createdAt = (Date) in.readValue(cl);
    statement = in.readParcelable(cl);
    referringAmen = in.readParcelable(cl);
    referringAmenId = (Long) in.readValue(cl);

  }

  public Long getReferringAmenId() {
    return referringAmenId;
  }

  public void setReferringAmenId(Long referringAmenId) {
    this.referringAmenId = referringAmenId;
  }
}

