package com.jaeckel.amdroid.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

import java.util.Date;
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

  private static final String TAG = "Topic" ;

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

  public long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public boolean isBest() {
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
      dest.writeLong(id);

      if (best == null) {
        dest.writeInt(0);
      } else {
        dest.writeInt(best ? 0 : 1);
      }

      dest.writeString(description);
      dest.writeString(scope);
      dest.writeInt(objektsCount);
      dest.writeList(rankedStatements);



    }

    private void readFromParcel(Parcel in) {
      id = in.readLong();
      best = in.readInt() == 0;
      description = in.readString();
      scope = in.readString();
      objektsCount = in.readInt();
      rankedStatements = in.readArrayList(getClass().getClassLoader());
      
    }
}
