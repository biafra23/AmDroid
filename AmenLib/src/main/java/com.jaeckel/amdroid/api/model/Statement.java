package com.jaeckel.amdroid.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.List;

/**
 * User: biafra
 * Date: 9/24/11
 * Time: 2:09 AM
 */
public class Statement implements Parcelable {

  private Long       id;
  private Long       totalAmenCount;
  private Boolean    agreeable;
  private List<User> agreeingNetwork;
  private Topic      topic;
  private Objekt     objekt;
  private User       firstPoster;
  private Date       firstPostedAt;
  private static final String TAG = "Statement";

  public Statement() {

  }

  public Statement(Objekt o, Topic t) {
    this.topic = t;
    this.objekt = o;
  }

  public String toDisplayString() {

    return objekt.getName() + " is " + (topic.isBest() ? "the Best " : "the Worst ") + (objekt.getKindId() == 1 ? " Place for " : "") + topic.getDescription() + " " + topic.getScope();

  }

  @Override
  public String toString() {
    return "Statement{" +
           "id=" + id +
           ", totalAmenCount=" + totalAmenCount +
           ", agreeable=" + agreeable +
           ", agreeingNetwork=" + agreeingNetwork +
           ", topic=" + topic +
           ", objekt=" + objekt +
           ", firstPoster=" + firstPoster +
           ", firstPostedAt=" + firstPostedAt +
           '}';
  }

  public String json() {

    GsonBuilder builder = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    return builder.create().toJson(this);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getTotalAmenCount() {
    return totalAmenCount;
  }

  public void setTotalAmenCount(Long totalAmenCount) {
    this.totalAmenCount = totalAmenCount;
  }

  public Boolean isAgreeable() {
    return agreeable;
  }

  public void setAgreeable(Boolean agreeable) {
    this.agreeable = agreeable;
  }

  public List<User> getAgreeingNetwork() {
    return agreeingNetwork;
  }

  public void setAgreeingNetwork(List<User> agreeingNetwork) {
    this.agreeingNetwork = agreeingNetwork;
  }

  public Topic getTopic() {
    return topic;
  }

  public void setTopic(Topic topic) {
    this.topic = topic;
  }

  public Objekt getObjekt() {
    return objekt;
  }

  public void setObjekt(Objekt objekt) {
    this.objekt = objekt;
  }


  public User getFirstPoster() {
    return firstPoster;
  }

  public Date getFirstPostedAt() {
    return firstPostedAt;
  }

  public void setFirstPostedAt(Date firstPostedAt) {
    this.firstPostedAt = firstPostedAt;
  }

/*
  *
  *   PARCEL STUFF
  *
  */

  public static final Parcelable.Creator<Statement> CREATOR = new Parcelable.Creator<Statement>() {

    public Statement[] newArray(int size) {
      return new Statement[size];
    }

    public Statement createFromParcel(Parcel source) {
      Log.d(TAG, "createFromParcel: " + source);
      return new Statement(source);
    }
  };

  private Statement(Parcel in) {
    Log.d(TAG, "Statement: parcel: " + in);

    readFromParcel(in);
  }

  @Override
  public int describeContents() {
    return 0;
  }


  @Override
  public void writeToParcel(Parcel dest, int flags) {
    Log.d(TAG, "writeToParcel");
    dest.writeLong(id);
    
    if (totalAmenCount == null) {
      dest.writeLong(0);
    } else {
      dest.writeLong(totalAmenCount);
    }
    if (agreeable == null) {
      dest.writeInt(0);
    } else {
      dest.writeInt(agreeable ? 0 : 1);
    }

//    dest.writeList(agreeingNetwork);
    dest.writeParcelable(topic, flags);
    dest.writeParcelable(objekt, flags);
    dest.writeParcelable(firstPoster, flags);

    if (firstPostedAt != null) {
      dest.writeLong(firstPostedAt.getTime());
    } else {
      dest.writeLong(-1L);
    }
    Log.d(TAG, "writeToParcel. done.");
  }

  private void readFromParcel(Parcel in) {
    Log.d(TAG, "readFromParcel");
    id = in.readLong();
    totalAmenCount = in.readLong();
    agreeable = in.readInt() == 0;

//    agreeingNetwork = in.readArrayList(getClass().getClassLoader());
    topic = in.readParcelable(getClass().getClassLoader());
    objekt = in.readParcelable(getClass().getClassLoader());
    firstPoster = in.readParcelable(getClass().getClassLoader());

    Long firstPostedAtLong = in.readLong();
    if (firstPostedAtLong == -1) {
      firstPostedAt = null;
    } else {
      firstPostedAt = new Date(firstPostedAtLong);
    }

    Log.d(TAG, "readFromParcel. done.");

  }
}
