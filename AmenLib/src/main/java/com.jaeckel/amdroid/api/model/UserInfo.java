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
  private String  photo;
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
           ", photo='" + photo + '\'' +
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

  public String getPhoto() {
    return picture;
//    return photo;
  }

  public void setPhoto(String photo) {
    this.photo = photo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    UserInfo userInfo = (UserInfo) o;

    if (createdAt != null ? !createdAt.equals(userInfo.createdAt) : userInfo.createdAt != null) return false;
    if (createdStatementCount != null ? !createdStatementCount.equals(userInfo.createdStatementCount) : userInfo.createdStatementCount != null)
      return false;
    if (followersCount != null ? !followersCount.equals(userInfo.followersCount) : userInfo.followersCount != null)
      return false;
    if (following != null ? !following.equals(userInfo.following) : userInfo.following != null) return false;
    if (followingCount != null ? !followingCount.equals(userInfo.followingCount) : userInfo.followingCount != null)
      return false;
    if (givenAmenCount != null ? !givenAmenCount.equals(userInfo.givenAmenCount) : userInfo.givenAmenCount != null)
      return false;
    if (id != null ? !id.equals(userInfo.id) : userInfo.id != null) return false;
    if (name != null ? !name.equals(userInfo.name) : userInfo.name != null) return false;
    if (photo != null ? !photo.equals(userInfo.photo) : userInfo.photo != null) return false;
    if (picture != null ? !picture.equals(userInfo.picture) : userInfo.picture != null) return false;
    if (receivedAmenCount != null ? !receivedAmenCount.equals(userInfo.receivedAmenCount) : userInfo.receivedAmenCount != null)
      return false;
    if (recentAmen != null ? !recentAmen.equals(userInfo.recentAmen) : userInfo.recentAmen != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (picture != null ? picture.hashCode() : 0);
    result = 31 * result + (photo != null ? photo.hashCode() : 0);
    result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
    result = 31 * result + (createdStatementCount != null ? createdStatementCount.hashCode() : 0);
    result = 31 * result + (givenAmenCount != null ? givenAmenCount.hashCode() : 0);
    result = 31 * result + (receivedAmenCount != null ? receivedAmenCount.hashCode() : 0);
    result = 31 * result + (followersCount != null ? followersCount.hashCode() : 0);
    result = 31 * result + (followingCount != null ? followingCount.hashCode() : 0);
    result = 31 * result + (following != null ? following.hashCode() : 0);
    result = 31 * result + (recentAmen != null ? recentAmen.hashCode() : 0);
    return result;
  }
}
