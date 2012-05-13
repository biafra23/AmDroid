package com.jaeckel.amenoid.api.model;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author biafra
 * @date 3/31/12 4:41 PM
 */
public class MediaItem implements Parcelable {


  private String type;
  private String contentUrl;
  private String contributor_name;

  @Override public String toString() {
    final StringBuffer sb = new StringBuffer();
    sb.append("MediaItem");
    sb.append("{type='").append(type).append('\'');
    sb.append(", contenUrl='").append(contentUrl).append('\'');
    sb.append(", contributor_name='").append(contributor_name).append('\'');
    sb.append('}');
    return sb.toString();
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getContentUrl() {
    return contentUrl;
  }

  public void setContentUrl(String contentUrl) {
    this.contentUrl = contentUrl;
  }

  public String getContributor_name() {
    return contributor_name;
  }

  public void setContributor_name(String contributor_name) {
    this.contributor_name = contributor_name;
  }

   /*
  *
  *   PARCEL STUFF
  *
  */

  public static final Parcelable.Creator<MediaItem> CREATOR = new Parcelable.Creator<MediaItem>() {

    public MediaItem[] newArray(int size) {
      return new MediaItem[size];
    }

    public MediaItem createFromParcel(Parcel source) {
      return new MediaItem(source);
    }
  };

  private MediaItem(Parcel in) {
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
    dest.writeValue(type);
    dest.writeValue(contentUrl);
    dest.writeValue(contributor_name);
  }

  private void readFromParcel(Parcel in) {
    final ClassLoader cl = getClass().getClassLoader();

    type = (String) in.readValue(cl);
    contentUrl = (String) in.readValue(cl);
    contributor_name = (String) in.readValue(cl);

  }

}
