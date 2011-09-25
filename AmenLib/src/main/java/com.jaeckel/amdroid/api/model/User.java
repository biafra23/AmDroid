package com.jaeckel.amdroid.api.model;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: biafra
 * Date: 9/23/11
 * Time: 8:37 PM
 */
public class User {


  private Long id;
  private String name;
  private String picture;

  public User(Long id) {
    this.id = id;
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
           '}';
  }

  public String json() {

    return new Gson().toJson(this);
  }

}
