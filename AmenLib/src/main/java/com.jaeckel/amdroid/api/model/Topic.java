package com.jaeckel.amdroid.api.model;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * User: biafra
 * Date: 9/24/11
 * Time: 2:17 AM
 */
public class Topic {

  private Long id;
  private Boolean best;
  private String  description;
  private String  scope;
  private Integer objektsCount;
  private Map<Integer, Statement> rankedStatements;

  public Topic(String description, Boolean best, String scope) {
    this.description = description;
    this.best = best;
    this.scope = scope;
  }
  
  public Topic(JSONObject topic) {

    try {


      this.id = topic.getLong("id");
      this.best = topic.getBoolean("best");
      this.description = topic.getString("description");
      this.scope = topic.getString("scope");
      this.objektsCount = topic.getInt("objekts_count");

      if (topic.has("ranked_statements")) {
        JSONArray rankedStatementsArray = topic.getJSONArray("ranked_statements");
        rankedStatements = new HashMap<Integer, Statement>();
        for(int i = 0; i < rankedStatementsArray.length(); i++) {
          JSONObject rankedStatement = rankedStatementsArray.getJSONObject(i);
          Integer rank = rankedStatement.getInt("rank");
          Statement statement = new Statement(rankedStatement.getJSONObject("statement"));
          rankedStatements.put(rank, statement);
          
        }
      }
      
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

  public void setId(Long id) {
    this.id = id;
  }

  public boolean isBest() {
    return best;
  }

  public void setBest(Boolean best) {
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

  public void setObjektsCount(Integer objektsCount) {
    this.objektsCount = objektsCount;
  }

  public String json() {

    GsonBuilder builder = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    return builder.create().toJson(this);
  }

  public Map<Integer, Statement> getRankedStatements() {
    return rankedStatements;
  }

  public void setRankedStatements(Map<Integer, Statement> rankedStatements) {
    this.rankedStatements = rankedStatements;
  }
}