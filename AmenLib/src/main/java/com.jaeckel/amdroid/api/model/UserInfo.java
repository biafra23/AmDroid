package com.jaeckel.amdroid.api.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: biafra
 * Date: 9/25/11
 * Time: 4:06 AM
 */
public class UserInfo {
  private Long    id;
  private String  name;
  private String  picture;
  private Date    createdAt;
  private Integer createdStatementCount;
  private Integer givenAmenCount;
  private Integer receivedAmenCount;
  private Integer followersCount;
  private Integer followingCount;
  private Boolean following;
  private List<Amen> recentAmen;


  public UserInfo(JSONObject object) {
    try {

      this.id = object.getLong("id");
      this.name = object.getString("name");
      this.picture = object.getString("picture");
      this.picture = object.getString("picture");
      if (object.has("created_at")) {
        final String created_at = (String) object.get("created_at");
        Date date = Amen.parseIso8601DateJoda(created_at);
        this.setCreatedAt(date);
      }
      this.createdStatementCount = object.getInt("created_statements_count");
      this.givenAmenCount = object.getInt("given_amen_count");
      this.receivedAmenCount = object.getInt("received_amen_count");
      this.followersCount = object.getInt("followers_count");
      this.followingCount = object.getInt("following_count");
      this.following = object.getBoolean("following");

      JSONArray recentAmenArray = object.getJSONArray("recent_amen");
      recentAmen = new ArrayList<Amen>();

      for(int i = 0; i < recentAmenArray.length(); i++) {
        
        Amen amen = new Amen(recentAmenArray.getJSONObject(i));
        recentAmen.add(amen);
      }
      
    } catch (JSONException e) {
      throw new RuntimeException("Error parsing UserInfo", e);
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
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

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Integer getCreatedStatementCount() {
    return createdStatementCount;
  }

  public void setCreatedStatementCount(Integer createdStatementCount) {
    this.createdStatementCount = createdStatementCount;
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

  @Override
  public String toString() {
    return "UserInfo{" +
           "id=" + id +
           ", name='" + name + '\'' +
           ", picture='" + picture + '\'' +
           ", createdAt=" + createdAt +
           ", createdStatementCount=" + createdStatementCount +
           ", givenAmenCount=" + givenAmenCount +
           ", receivedAmenCount=" + receivedAmenCount +
           ", followersCount=" + followersCount +
           ", followingCount=" + followingCount +
           ", following=" + following +
           ", recentAmen=" + recentAmen +
           '}';
  }


}
