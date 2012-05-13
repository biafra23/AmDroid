package com.jaeckel.amenoid.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.Gson;

import java.util.Date;
import java.util.List;

/**
 * User: biafra
 * Date: 9/23/11
 * Time: 8:37 PM
 */
public class User implements Parcelable {


  private Long       id;
  private String     name;
  private String     picture;
  private String     photo;
  private Date       createdAt;
  private Integer    createdStatementsCount;
  private Integer    givenAmenCount;
  private Integer    receivedAmenCount;
  private Integer    followersCount;
  private Integer    followingCount;
  private Boolean    following;
  private List<Amen> recentAmen;
  private String     authToken;
  private String     bio;
  private String     username;


  public User() {
  }

  public User(Long id) {
    this.id = id;
  }

  public User(String id) {
    this.id = Long.valueOf(id);
  }

  public long getId() {
    if (id == null) {
      return -1;
    }
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPicture() {
    return picture;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }

  @Override
  public String toString() {
    return "User{" +
           "id=" + id +
           ", name='" + name + '\'' +
           ", picture='" + picture + '\'' +
           ", photo='" + photo + '\'' +
           ", createdAt=" + createdAt +
           ", createdStatementsCount=" + createdStatementsCount +
           ", givenAmenCount=" + givenAmenCount +
           ", receivedAmenCount=" + receivedAmenCount +
           ", followersCount=" + followersCount +
           ", followingCount=" + followingCount +
           ", following=" + following +
           ", recentAmen=" + recentAmen +
           ", authToken='" + authToken + '\'' +
           ", bio='" + bio + '\'' +
           ", username='" + username + '\'' +
           '}';
  }

  public String json() {

    return new Gson().toJson(this);
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Integer getCreatedStatementsCount() {
    return createdStatementsCount;
  }

  public void setCreatedStatementsCount(Integer createdStatementsCount) {
    this.createdStatementsCount = createdStatementsCount;
  }

  public Integer getGivenAmenCount() {
    return givenAmenCount;
  }

  public void setGivenAmenCount(Integer givenAmenCount) {
    this.givenAmenCount = givenAmenCount;
  }

  public Integer getReceivedAmenCount() {
    return receivedAmenCount;
  }

  public void setReceivedAmenCount(Integer receivedAmenCount) {
    this.receivedAmenCount = receivedAmenCount;
  }

  public Integer getFollowersCount() {
    return followersCount;
  }

  public void setFollowersCount(Integer followersCount) {
    this.followersCount = followersCount;
  }

  public Integer getFollowingCount() {
    return followingCount;
  }

  public void setFollowingCount(Integer followingCount) {
    this.followingCount = followingCount;
  }

  public Boolean getFollowing() {
    return following;
  }

  public void setFollowing(Boolean following) {
    this.following = following;
  }

  public List<Amen> getRecentAmen() {
    return recentAmen;
  }

  public void setRecentAmen(List<Amen> recentAmen) {
    this.recentAmen = recentAmen;
  }

  /*
  *
  *   PARCEL STUFF
  *
  */

  public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

    public User[] newArray(int size) {
      return new User[size];
    }

    public User createFromParcel(Parcel source) {
      return new User(source);
    }
  };

  private User(Parcel in) {
    readFromParcel(in);
  }

  @Override
  public int describeContents() {
    return 0;
  }


  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(id);
    dest.writeString(name);
    dest.writeString(picture);
    dest.writeValue(createdAt);
    dest.writeValue(createdStatementsCount);
    dest.writeValue(givenAmenCount);
    dest.writeValue(receivedAmenCount);
    dest.writeValue(followersCount);
    dest.writeValue(followingCount);
    dest.writeValue(following);
    dest.writeString(authToken);
    dest.writeString(photo);
    dest.writeString(bio);
    dest.writeString(username);

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    User user = (User) o;

    if (authToken != null ? !authToken.equals(user.authToken) : user.authToken != null) return false;
    if (bio != null ? !bio.equals(user.bio) : user.bio != null) return false;
    if (createdAt != null ? !createdAt.equals(user.createdAt) : user.createdAt != null) return false;
    if (createdStatementsCount != null ? !createdStatementsCount.equals(user.createdStatementsCount) : user.createdStatementsCount != null)
      return false;
    if (followersCount != null ? !followersCount.equals(user.followersCount) : user.followersCount != null)
      return false;
    if (following != null ? !following.equals(user.following) : user.following != null) return false;
    if (followingCount != null ? !followingCount.equals(user.followingCount) : user.followingCount != null)
      return false;
    if (givenAmenCount != null ? !givenAmenCount.equals(user.givenAmenCount) : user.givenAmenCount != null)
      return false;
    if (id != null ? !id.equals(user.id) : user.id != null) return false;
    if (name != null ? !name.equals(user.name) : user.name != null) return false;
    if (photo != null ? !photo.equals(user.photo) : user.photo != null) return false;
    if (picture != null ? !picture.equals(user.picture) : user.picture != null) return false;
    if (receivedAmenCount != null ? !receivedAmenCount.equals(user.receivedAmenCount) : user.receivedAmenCount != null)
      return false;
    if (recentAmen != null ? !recentAmen.equals(user.recentAmen) : user.recentAmen != null) return false;
    if (username != null ? !username.equals(user.username) : user.username != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (picture != null ? picture.hashCode() : 0);
    result = 31 * result + (photo != null ? photo.hashCode() : 0);
    result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
    result = 31 * result + (createdStatementsCount != null ? createdStatementsCount.hashCode() : 0);
    result = 31 * result + (givenAmenCount != null ? givenAmenCount.hashCode() : 0);
    result = 31 * result + (receivedAmenCount != null ? receivedAmenCount.hashCode() : 0);
    result = 31 * result + (followersCount != null ? followersCount.hashCode() : 0);
    result = 31 * result + (followingCount != null ? followingCount.hashCode() : 0);
    result = 31 * result + (following != null ? following.hashCode() : 0);
    result = 31 * result + (recentAmen != null ? recentAmen.hashCode() : 0);
    result = 31 * result + (authToken != null ? authToken.hashCode() : 0);
    result = 31 * result + (bio != null ? bio.hashCode() : 0);
    result = 31 * result + (username != null ? username.hashCode() : 0);
    return result;
  }

  private void readFromParcel(Parcel in) {
    final ClassLoader cl = getClass().getClassLoader();

    id = (Long) in.readValue(cl);
    name = in.readString();
    picture = in.readString();
    createdAt = (Date) in.readValue(cl);
    createdStatementsCount = (Integer) in.readValue(cl);
    givenAmenCount = (Integer) in.readValue(cl);
    receivedAmenCount = (Integer) in.readValue(cl);
    followersCount = (Integer) in.readValue(cl);
    followingCount = (Integer) in.readValue(cl);
    following = (Boolean) in.readValue(cl);
    authToken = in.readString();
    photo = in.readString();
    bio = in.readString();
    username = in.readString();
  }

  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }

  public String getPhoto() {
    if (photo != null) {
      return photo.replace(".jpg", "_small.jpg");
    }
    return photo;
  }

  public void setPhoto(String photo) {
    this.photo = photo;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
