package com.jaeckel.amdroid.api.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: biafra
 * Date: 9/24/11
 * Time: 2:17 AM
 */
public class Topic {

  private long    id;
  private boolean best;
  private String  description;
  private String  scope;
  private int     objektsCount;

  public Topic(JSONObject topic) {

    try {


      this.id = topic.getLong("id");
      this.best = topic.getBoolean("best");
      this.description = topic.getString("description");
      this.scope = topic.getString("scope");
      this.objektsCount = topic.getInt("objekts_count");
      
    } catch (JSONException e) {
      throw new RuntimeException("", e);
    }
  }

  @Override
  public String toString() {
    return "Topic{" +
           "id=" + id +
           ", best=" + best +
           ", description='" + description + '\'' +
           ", scope='" + scope + '\'' +
           ", objektsCount=" + objektsCount +
           '}';
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public boolean isBest() {
    return best;
  }

  public void setBest(boolean best) {
    this.best = best;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public int getObjektsCount() {
    return objektsCount;
  }

  public void setObjektsCount(int objektsCount) {
    this.objektsCount = objektsCount;
  }


}
