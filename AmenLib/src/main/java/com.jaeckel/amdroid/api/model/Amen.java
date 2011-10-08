package com.jaeckel.amdroid.api.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
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
  private Integer   kindId; //normal, amen, dispute
  private Statement statement;
  //sometimes disputed Amen
  private Amen      referringAmen;

  private static final String TAG = "Amen";

  public Amen() {

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
    return getKindId() == 2;
  }

  public boolean isAmen() {
    return getKindId() == 1;
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
    if (id == null) {
      dest.writeLong(0L);
    } else {
      dest.writeLong(id);
    }
    if (userId == null) {
      dest.writeLong(0);
    } else {
      dest.writeLong(userId);
    }

    dest.writeParcelable(user, flags);
    if (kindId == null) {
      dest.writeInt(0);
    } else {
      dest.writeInt(kindId);
    }

    if (createdAt != null) {
      final Long createdAtLong = createdAt.getTime();
      Log.d(TAG, "readFromParcel. createdAtLong:" + createdAtLong / 1000);

      dest.writeLong(createdAtLong / 1000);
    } else {
      dest.writeLong(0L);
    }
    dest.writeParcelable(statement, flags);
//    dest.writeParcelable(referringAmen, flags);
    Log.d(TAG, "writeToParcel. done.‚");

  }

  private void readFromParcel(Parcel in) {
    Log.d(TAG, "readFromParcel");
    id = in.readLong();
    Log.d(TAG, "readFromParcel. id:" + id);
    userId = in.readLong();
    Log.d(TAG, "readFromParcel. userId:" + userId);
    user = in.readParcelable(getClass().getClassLoader());
    Log.d(TAG, "readFromParcel. user:" + user);
    kindId = in.readInt();
    Log.d(TAG, "readFromParcel. kindId:" + kindId);
    Long createdAtLong = in.readLong();

    Log.d(TAG, "readFromParcel. createdAtLong:" + createdAtLong);

    if (createdAtLong == -1) {
      createdAt = null;
    } else {
      createdAt = new Date(createdAtLong);
    }
    Log.d(TAG, "readFromParcel. before statement.");
    statement = in.readParcelable(getClass().getClassLoader());
//    referringAmen = in.readParcelable(getClass().getClassLoader());

    Log.d(TAG, "readFromParcel. done.");
  }
}

