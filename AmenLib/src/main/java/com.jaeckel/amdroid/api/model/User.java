package com.jaeckel.amdroid.api.model;

/**
 * User: biafra
 * Date: 9/23/11
 * Time: 8:37 PM
 */
public class User {


  private long   id;
  private String name;
  private String picture;

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
           '}';
  }
}
