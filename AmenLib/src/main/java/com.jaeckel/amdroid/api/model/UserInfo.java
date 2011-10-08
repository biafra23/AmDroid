package com.jaeckel.amdroid.api.model;

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

  public UserInfo() {
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

  public List<Amen> getRecentAmen() {
    return recentAmen;
  }

  public void setRecentAmen(List<Amen> recentAmen) {
    this.recentAmen = recentAmen;
  }
}
