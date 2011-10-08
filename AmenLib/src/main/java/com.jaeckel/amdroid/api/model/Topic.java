package com.jaeckel.amdroid.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * User: biafra
 * Date: 9/24/11
 * Time: 2:17 AM
 */
public class Topic implements Parcelable {

  private Long                   id;
  private Boolean                best;
  private String                 description;
  private String                 scope;
  private Integer                objektsCount;
  private List<RankedStatements> rankedStatements;

  private static final String TAG = "Topic";

  public Topic() {
  }

  public Topic(String description, Boolean best, String scope) {
    this.description = description;
    this.best = best;
    this.scope = scope;
  }


  @Override
  public String toString() {
    return "Topic{" +
           "id=" + id +
           ", best=" + best +
           ", description='" + description + '\'' +
           ", scope='" + scope + '\'' +
           ", objektsCount=" + objektsCount +
           ", rankedStatements=" + rankedStatements +
           '}';
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Boolean isBest() {
    return best;
  }

  public void setBest(Boolean best) {
    this.best = best;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public int getObjektsCount() {
    return objektsCount;
  }

  public void setObjektsCount(Integer objektsCount) {
    this.objektsCount = objektsCount;
  }


  public List<RankedStatements> getRankedStatements() {
    return rankedStatements;
  }

  public void setRankedStatements(List<RankedStatements> rankedStatements) {
    this.rankedStatements = rankedStatements;
  }

  public String json() {

    GsonBuilder builder = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    return builder.create().toJson(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Topic topic = (Topic) o;

    if (best != null ? !best.equals(topic.best) : topic.best != null) return false;
    if (description != null ? !description.equals(topic.description) : topic.description != null) return false;
    if (id != null ? !id.equals(topic.id) : topic.id != null) return false;
    if (objektsCount != null ? !objektsCount.equals(topic.objektsCount) : topic.objektsCount != null) return false;
    if (rankedStatements != null ? !rankedStatements.equals(topic.rankedStatements) : topic.rankedStatements != null)
      return false;
    if (scope != null ? !scope.equals(topic.scope) : topic.scope != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (best != null ? best.hashCode() : 0);
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (scope != null ? scope.hashCode() : 0);
    result = 31 * result + (objektsCount != null ? objektsCount.hashCode() : 0);
    result = 31 * result + (rankedStatements != null ? rankedStatements.hashCode() : 0);
    return result;
  }

/*
    *
    *   PARCEL STUFF
    *
    */

  public static final Parcelable.Creator<Topic> CREATOR = new Parcelable.Creator<Topic>() {

    public Topic[] newArray(int size) {
      return new Topic[size];
    }

    public Topic createFromParcel(Parcel source) {
      return new Topic(source);
    }
  };

  private Topic(Parcel in) {
    readFromParcel(in);
  }

  @Override
  public int describeContents() {
    return 0;
  }


  @Override
  public void writeToParcel(Parcel dest, int flags) {

    dest.writeValue(id);
    dest.writeValue(best);
    dest.writeString(description);
    dest.writeString(scope);
    dest.writeValue(objektsCount);
    dest.writeList(rankedStatements);
  }

  private void readFromParcel(Parcel in) {
    final ClassLoader cl = getClass().getClassLoader();

    id = (Long) in.readValue(cl);
    best = (Boolean) in.readValue(cl);
    description = in.readString();
    scope = in.readString();
    objektsCount = (Integer) in.readValue(cl);

    rankedStatements = in.readArrayList(cl);

  }
}
