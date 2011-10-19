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
  private Long       firstAmenId;

  private static final String TAG = "Statement";

  public Statement() {
  }

  public Statement(Objekt o, Topic t) {
    this.topic = t;
    this.objekt = o;
  }

  public String toDisplayString() {
    return objekt.getName() + " is " + (topic.isBest() ? "the Best " : "the Worst ")
           + (objekt.getKindId() == 1 ? " Place for " : "") + topic.getDescription() + " " + topic.getScope();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Statement statement = (Statement) o;

    if (agreeable != null ? !agreeable.equals(statement.agreeable) : statement.agreeable != null) return false;
    if (agreeingNetwork != null ? !agreeingNetwork.equals(statement.agreeingNetwork) : statement.agreeingNetwork != null)
      return false;
    if (firstAmenId != null ? !firstAmenId.equals(statement.firstAmenId) : statement.firstAmenId != null) return false;
    if (firstPostedAt != null ? !firstPostedAt.equals(statement.firstPostedAt) : statement.firstPostedAt != null)
      return false;
    if (firstPoster != null ? !firstPoster.equals(statement.firstPoster) : statement.firstPoster != null) return false;
    if (id != null ? !id.equals(statement.id) : statement.id != null) return false;
    if (objekt != null ? !objekt.equals(statement.objekt) : statement.objekt != null) return false;
    if (topic != null ? !topic.equals(statement.topic) : statement.topic != null) return false;
    if (totalAmenCount != null ? !totalAmenCount.equals(statement.totalAmenCount) : statement.totalAmenCount != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (totalAmenCount != null ? totalAmenCount.hashCode() : 0);
    result = 31 * result + (agreeable != null ? agreeable.hashCode() : 0);
    result = 31 * result + (agreeingNetwork != null ? agreeingNetwork.hashCode() : 0);
    result = 31 * result + (topic != null ? topic.hashCode() : 0);
    result = 31 * result + (objekt != null ? objekt.hashCode() : 0);
    result = 31 * result + (firstPoster != null ? firstPoster.hashCode() : 0);
    result = 31 * result + (firstPostedAt != null ? firstPostedAt.hashCode() : 0);
    result = 31 * result + (firstAmenId != null ? firstAmenId.hashCode() : 0);
    return result;
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
           ", firstAmenId=" + firstAmenId +
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
    Log.d(TAG, "Statement() parcel: " + in);

    readFromParcel(in);
  }

  @Override
  public int describeContents() {
    return 0;
  }


  @Override
  public void writeToParcel(Parcel dest, int flags) {
    Log.d(TAG, "writeToParcel");
    dest.writeValue(id);
    dest.writeValue(totalAmenCount);
    dest.writeValue(agreeable);
    dest.writeList(agreeingNetwork);
    dest.writeParcelable(topic, flags);
    dest.writeParcelable(objekt, flags);
    dest.writeParcelable(firstPoster, flags);
    dest.writeValue(firstPostedAt);
    dest.writeValue(firstAmenId);
    Log.d(TAG, "writeToParcel. done.");
  }

  private void readFromParcel(Parcel in) {
    final ClassLoader cl = getClass().getClassLoader();
    Log.d(TAG, "readFromParcel");
    id = (Long) in.readValue(cl);
    totalAmenCount = (Long) in.readValue(cl);
    agreeable = (Boolean) in.readValue(cl);
    agreeingNetwork = in.readArrayList(cl);
    topic = in.readParcelable(cl);
    objekt = in.readParcelable(cl);
    firstPoster = in.readParcelable(cl);
    firstPostedAt = (Date) in.readValue(cl);
    firstAmenId = (Long) in.readValue(cl);
    Log.d(TAG, "readFromParcel. done.");

  }

  public Long getFirstAmenId() {
    return firstAmenId;
  }

  public void setFirstAmenId(Long firstAmenId) {
    this.firstAmenId = firstAmenId;
  }


}
