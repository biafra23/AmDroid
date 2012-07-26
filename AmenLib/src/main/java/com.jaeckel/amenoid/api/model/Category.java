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
  private String subtitle;
  private String icon;
  private String image;
  private String color;
  private String amenUrl;

  public Category() {
  }

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

  public String getSubtitle() {
    return subtitle;
  }

  public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
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
    sb.append(", subTitle='").append(subtitle).append('\'');
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
    dest.writeString(name);
    dest.writeString(id);
    dest.writeString(subtitle);
    dest.writeString(icon);
    dest.writeString(image);
    dest.writeString(color);
    dest.writeString(amenUrl);


  }

  private void readFromParcel(Parcel in) {

    name = in.readString();
    id = in.readString();
    subtitle = in.readString();
    icon = in.readString();
    image = in.readString();
    color = in.readString();
    amenUrl = in.readString();

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Category category = (Category) o;

    if (amenUrl != null ? !amenUrl.equals(category.amenUrl) : category.amenUrl != null) return false;
    if (color != null ? !color.equals(category.color) : category.color != null) return false;
    if (icon != null ? !icon.equals(category.icon) : category.icon != null) return false;
    if (id != null ? !id.equals(category.id) : category.id != null) return false;
    if (image != null ? !image.equals(category.image) : category.image != null) return false;
    if (name != null ? !name.equals(category.name) : category.name != null) return false;
    if (subtitle != null ? !subtitle.equals(category.subtitle) : category.subtitle != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (id != null ? id.hashCode() : 0);
    result = 31 * result + (subtitle != null ? subtitle.hashCode() : 0);
    result = 31 * result + (icon != null ? icon.hashCode() : 0);
    result = 31 * result + (image != null ? image.hashCode() : 0);
    result = 31 * result + (color != null ? color.hashCode() : 0);
    result = 31 * result + (amenUrl != null ? amenUrl.hashCode() : 0);
    return result;
  }
}
