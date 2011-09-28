package com.jaeckel.amdroid.api.model;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * User: biafra
 * Date: 9/23/11
 * Time: 8:37 PM
 */
public class User {


  private Long    id;
  private String  name;
  private String  picture;
  private Date    createdAt;
  private Integer createdStatementsCount;
  private Integer givenAmenCount;
  private Integer receivedAmenCount;
  private Integer followersCount;
  private Integer followingCount;
  private Boolean following;
  private List<Amen> recentAmen;


  public User() {

  }

  public User(Long id) {
    this.id = id;
  }

  public User(String id) {
    this.id = Long.valueOf(id);
  }

  public User(UserInfo ui) {
    this.id = ui.getId();
    this.name = ui.getName();
    this.picture = ui.getPicture();
  }

  public User(JSONObject object) {
    try {

      this.id = object.getLong("id");
      this.name = object.getString("name");
      this.picture = object.getString("picture");

    } catch (JSONException e) {
      throw new RuntimeException("Error parsing User", e);
    }
  }

  public long getId() {
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
           ", createdAt=" + createdAt +
           ", createdStatementsCount=" + createdStatementsCount +
           ", givenAmenCount=" + givenAmenCount +
           ", receivedAmenCount=" + receivedAmenCount +
           ", followersCount=" + followersCount +
           ", followingCount=" + followingCount +
           ", following=" + following +
           ", recentAmen=" + recentAmen +
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
}
