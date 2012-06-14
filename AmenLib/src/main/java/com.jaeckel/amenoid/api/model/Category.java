package com.jaeckel.amenoid.api.model;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author biafra
 * @date 6/14/12 8:51 PM
 */
public class Category implements Parcelable {


  private String name;
  private String id;
  private String subTitle;
  private String icon;
  private String image;
  private String color;
  private String amenUrl;

  public String json() {

    GsonBuilder builder = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    return builder.create().toJson(this);
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSubTitle() {
    return subTitle;
  }

  public void setSubTitle(String subTitle) {
    this.subTitle = subTitle;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getAmenUrl() {
    return amenUrl;
  }

  public void setAmenUrl(String amenUrl) {
    this.amenUrl = amenUrl;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer();
    sb.append("Category");
    sb.append("{name='").append(name).append('\'');
    sb.append(", id='").append(id).append('\'');
    sb.append(", subTitle='").append(subTitle).append('\'');
    sb.append(", icon='").append(icon).append('\'');
    sb.append(", image='").append(image).append('\'');
    sb.append(", color='").append(color).append('\'');
    sb.append(", amenUrl='").append(amenUrl).append('\'');
    sb.append('}');
    return sb.toString();
  }

  /*
  *
  *   PARCEL STUFF
  *
  */

  public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {

    public Category[] newArray(int size) {
      return new Category[size];
    }

    public Category createFromParcel(Parcel source) {
      return new Category(source);
    }
  };

  private Category(Parcel in) {
    readFromParcel(in);
  }

  @Override
  public int describeContents() {
    return 0;
  }


  @Override
  public void writeToParcel(Parcel dest, int flags) {
    if (dest == null) {
      throw new RuntimeException("Parcel must not be null");

    }
    dest.writeValue(name);
    dest.writeValue(id);
    dest.writeValue(subTitle);
    dest.writeValue(icon);
    dest.writeValue(image);
    dest.writeValue(color);
    dest.writeValue(amenUrl);


  }

  private void readFromParcel(Parcel in) {

    name = in.readString();
    id = in.readString();
    subTitle = in.readString();
    icon = in.readString();
    image = in.readString();
    color = in.readString();
    amenUrl = in.readString();

  }

}
