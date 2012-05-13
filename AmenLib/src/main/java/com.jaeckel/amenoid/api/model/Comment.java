package com.jaeckel.amenoid.api.model;

import java.util.ArrayList;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author biafra
 * @date 4/18/12 9:42 PM
 */
public class Comment implements Parcelable{

  private Long id;
  private String body;
  private Date createdAt;
  private User user;

  @Override public String toString() {
    final StringBuffer sb = new StringBuffer();
    sb.append("Comment");
    sb.append("{id=").append(id);
    sb.append(", body='").append(body).append('\'');
    sb.append(", createdAt=").append(createdAt);
    sb.append(", user=").append(user);
    sb.append('}');
    return sb.toString();
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }


   /*
  *
  *   PARCEL STUFF
  *
  */

  public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {

    public Comment[] newArray(int size) {
      return new Comment[size];
    }

    public Comment createFromParcel(Parcel source) {
      return new Comment(source);
    }
  };

  private Comment(Parcel in) {
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
    dest.writeValue(id);
    dest.writeParcelable(user, flags);
    dest.writeString(body);

  }

  private void readFromParcel(Parcel in) {
    final ClassLoader cl = getClass().getClassLoader();

    id = (Long) in.readValue(cl);
    user = in.readParcelable(cl);
    body = in.readString();

  }


}
