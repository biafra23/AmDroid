package com.jaeckel.amenoid.api.model;

import java.util.List;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.jaeckel.amenoid.api.AmenService;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * User: biafra
 * Date: 9/24/11
 * Time: 2:17 AM
 */
public class Objekt implements Parcelable {

  private Long id;
  private Integer kindId = AmenService.OBJEKT_KIND_THING;
  private String          name;
  private List<String>    key;
  private String          category;
  private String          defaultDescription;
  private List<String>    possibleDescriptions;
  private List<String>    possibleScopes;
  private List<MediaItem> media;
  private String          defaultScope;
  private Double          lat;
  private Double          lng;
  private String          slug;


  public Objekt() {

  }

  public Objekt(String name, Integer kindId) {
    this.name = name;
    this.kindId = kindId;

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Objekt objekt = (Objekt) o;

    if (category != null ? !category.equals(objekt.category) : objekt.category != null) return false;
    if (defaultDescription != null ? !defaultDescription.equals(objekt.defaultDescription) : objekt.defaultDescription != null)
      return false;
    if (defaultScope != null ? !defaultScope.equals(objekt.defaultScope) : objekt.defaultScope != null) return false;
    if (id != null ? !id.equals(objekt.id) : objekt.id != null) return false;
    if (key != null ? !key.equals(objekt.key) : objekt.key != null) return false;
    if (kindId != null ? !kindId.equals(objekt.kindId) : objekt.kindId != null) return false;
    if (name != null ? !name.equals(objekt.name) : objekt.name != null) return false;
    if (possibleDescriptions != null ? !possibleDescriptions.equals(objekt.possibleDescriptions) : objekt.possibleDescriptions != null)
      return false;
    if (possibleScopes != null ? !possibleScopes.equals(objekt.possibleScopes) : objekt.possibleScopes != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (kindId != null ? kindId.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (key != null ? key.hashCode() : 0);
    result = 31 * result + (category != null ? category.hashCode() : 0);
    result = 31 * result + (defaultDescription != null ? defaultDescription.hashCode() : 0);
    result = 31 * result + (possibleDescriptions != null ? possibleDescriptions.hashCode() : 0);
    result = 31 * result + (possibleScopes != null ? possibleScopes.hashCode() : 0);
    result = 31 * result + (defaultScope != null ? defaultScope.hashCode() : 0);
    return result;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override public String toString() {
    final StringBuffer sb = new StringBuffer();
    sb.append("Objekt");
    sb.append("{id=").append(id);
    sb.append(", kindId=").append(kindId);
    sb.append(", name='").append(name).append('\'');
    sb.append(", key=").append(key);
    sb.append(", category='").append(category).append('\'');
    sb.append(", defaultDescription='").append(defaultDescription).append('\'');
    sb.append(", possibleDescriptions=").append(possibleDescriptions);
    sb.append(", possibleScopes=").append(possibleScopes);
    sb.append(", media=").append(media);
    sb.append(", defaultScope='").append(defaultScope).append('\'');
    sb.append(", lat=").append(lat);
    sb.append(", lng=").append(lng);
    sb.append('}');
    return sb.toString();
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

    dest.writeValue(kindId);
    dest.writeString(name);
    dest.writeList(key);
    dest.writeString(category);
    dest.writeString(defaultDescription);
    dest.writeList(possibleDescriptions);
    dest.writeList(possibleScopes);
    dest.writeList(media);
    dest.writeString(defaultScope);
    dest.writeString(slug);
    dest.writeValue(id);
    dest.writeValue(lat);
    dest.writeValue(lng);
  }

  private void readFromParcel(Parcel in) {
    final ClassLoader cl = getClass().getClassLoader();

    kindId = (Integer) in.readValue(cl);
    name = in.readString();
    key = in.readArrayList(cl);
    category = in.readString();
    defaultDescription = in.readString();
    possibleDescriptions = in.readArrayList(cl);
    possibleScopes = in.readArrayList(cl);
    media = in.readArrayList(cl);
    defaultScope = in.readString();
    slug = in.readString();
    id = (Long) in.readValue(cl);
    lat = (Double) in.readValue(cl);
    lng = (Double) in.readValue(cl);
  }

  public List<String> getPossibleScopes() {
    return possibleScopes;
  }

  public void setPossibleScopes(List<String> possibleScopes) {
    this.possibleScopes = possibleScopes;
  }

  public List<MediaItem> getMedia() {
    return media;
  }

  public void setMedia(List<MediaItem> media) {
    this.media = media;
  }

  public Double getLat() {
    return lat;
  }

  public void setLat(Double lat) {
    this.lat = lat;
  }

  public Double getLng() {
    return lng;
  }

  public void setLng(Double lng) {
    this.lng = lng;
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }
}
