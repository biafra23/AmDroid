package com.jaeckel.amdroid.api.model;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: biafra
 * Date: 9/24/11
 * Time: 2:17 AM
 */
public class Objekt {

  private int       kindId;
  private String    name;
  private ObjektKey key;

  public Objekt(JSONObject objekt) {
    try {
      this.kindId = objekt.getInt("kind_id");
      this.name = objekt.getString("name");
      this.key = new ObjektKey(objekt.getJSONArray("key"));
    } catch (JSONException e) {
      throw new RuntimeException("", e);
    }
  }

  @Override
  public String toString() {
    return "Objekt{" +
           "kindId=" + kindId +
           ", name='" + name + '\'' +
           ", key=" + key +
           '}';
  }
  public String json() {

    return new Gson().toJson(this);
  }

  public int getKindId() {
    return kindId;
  }

  public void setKindId(int kindId) {
    this.kindId = kindId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ObjektKey getKey() {
    return key;
  }

  public void setKey(ObjektKey key) {
    this.key = key;
  }
}
