package com.jaeckel.amdroid.api.model;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * User: biafra
 * Date: 9/24/11
 * Time: 2:18 AM
 */
public class ObjektKey {

  private String value;
  private String type; //local, freebase

  public ObjektKey(JSONArray object) {
    try {

      this.type = object.getString(0);
      this.value = object.getString(1);
      
    } catch (JSONException e) {
      throw new RuntimeException("", e);
    }

  }

  @Override
  public String toString() {
    return "ObjektKey{" +
           "value=" + value +
           ", type='" + type + '\'' +
           '}';
  }
  public String json() {

    return new Gson().toJson(this);
  }

}
