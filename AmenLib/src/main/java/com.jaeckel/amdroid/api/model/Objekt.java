package com.jaeckel.amdroid.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

import java.util.List;

/**
 * User: biafra
 * Date: 9/24/11
 * Time: 2:17 AM
 */
public class Objekt implements Parcelable {

  public final static Integer PERSON = 0;
  public final static Integer PLACE  = 1;
  public final static Integer THING  = 2;

  private Integer      kindId;
  private String       name;
  private List<String> key;
  private String       category;
  private String       defaultDescription;
  private List<String> possibleDescriptions;
  private String       defaultScope;

  public Objekt() {

  }

  public Objekt(String name, Integer kindId) {
    this.name = name;
    this.kindId = kindId;

  }

  @Override
  public String toString() {
    return "Objekt{" +
           "kindId=" + kindId +
           ", name='" + name + '\'' +
           ", key=" + key +
           ", category='" + category + '\'' +
           ", defaultDescription='" + defaultDescription + '\'' +
           ", possibleDescriptions=" + possibleDescriptions +
           ", defaultScope='" + defaultScope + '\'' +
           '}';
  }

  public String json() {

    GsonBuilder builder = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    return builder.create().toJson(this);
  }

  public int getKindId() {
    return kindId;
  }

  public void setKindId(int kindId) {
    this.kindId = kindId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDefaultDescription() {
    return defaultDescription;
  }

  public void setDefaultDescription(String defaultDescription) {
    this.defaultDescription = defaultDescription;
  }

  public String getDefaultScope() {
    return defaultScope;
  }

  public void setDefaultScope(String defaultScope) {
    this.defaultScope = defaultScope;
  }

  public List<String> getKey() {
    return key;
  }

  public void setKey(List<String> key) {
    this.key = key;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public List<String> getPossibleDescriptions() {
    return possibleDescriptions;
  }

  public void setPossibleDescriptions(List<String> possibleDescriptions) {
    this.possibleDescriptions = possibleDescriptions;
  }

  /*
   *
   *   PARCEL STUFF
   *   
   */

  @Override
  public int describeContents() {
    return 0;
  }

  public static final Parcelable.Creator<Objekt> CREATOR = new Creator<Objekt>() {

    public Objekt[] newArray(int size) {
      return new Objekt[size];
    }

    public Objekt createFromParcel(Parcel source) {
      return new Objekt(source);
    }
  };

  private Objekt(Parcel in) {
    readFromParcel(in);
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    if (kindId == null) {
      dest.writeInt(0);
    } else {
      dest.writeInt(kindId);
    }
    if (name == null) {
      dest.writeString("");
    } else {
      dest.writeString(name);
    }
    dest.writeList(key);
    dest.writeString(category);
    dest.writeString(defaultDescription);
    dest.writeList(possibleDescriptions);
    dest.writeString(defaultScope);
  }

  private void readFromParcel(Parcel in) {
    kindId = in.readInt();
    name = in.readString();
    key = in.readArrayList(ClassLoader.getSystemClassLoader());
    category = in.readString();
    defaultDescription = in.readString();
    possibleDescriptions = in.readArrayList(ClassLoader.getSystemClassLoader());
    defaultScope = in.readString();
  }
}
