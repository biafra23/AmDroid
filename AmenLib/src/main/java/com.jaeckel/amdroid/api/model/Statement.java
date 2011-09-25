package com.jaeckel.amdroid.api.model;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * User: biafra
 * Date: 9/24/11
 * Time: 2:09 AM
 */
public class Statement {

  private long       id;
  private long       totalAmenCount;
  private boolean    agreeable;
  private List<User> agreeingNetwork;
  private Topic      topic;
  private Objekt     objekt;

  public Statement(JSONObject o) {
    try {
      //optional in referring_amen
      if (o.has("total_amen_count")) {
        this.id = o.getLong("id");
        this.totalAmenCount = o.getLong("total_amen_count");
        this.agreeingNetwork = new ArrayList<User>();
        JSONArray agreeingNetworkArray = o.getJSONArray("agreeing_network");
        for (int i = 0; i < agreeingNetworkArray.length(); i++) {
          agreeingNetwork.add(new User(agreeingNetworkArray.getJSONObject(i)));
        }
        this.agreeable = o.getBoolean("agreeable");
        this.topic = new Topic(o.getJSONObject("topic"));
      }

      this.objekt = new Objekt(o.getJSONObject("objekt"));

    } catch (JSONException e) {

      throw new RuntimeException("Error parsing statement: (" + o.toString() + ")", e);  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  @Override
  public String toString() {
    return "Statement{" +
           "id=" + id +
           ", totalAmenCount=" + totalAmenCount +
           ", agreeable=" + agreeable +
           ", agreeingNetwork=" + agreeingNetwork +
           ", topic=" + topic +
           ", objekt=" + objekt +
           '}';
  }

  public String json() {

    return new Gson().toJson(this);
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getTotalAmenCount() {
    return total_amen_count;
  }

  public void setTotalAmenCount(long totalAmenCount) {
    this.total_amen_count = totalAmenCount;
  }

  public boolean isAgreeable() {
    return agreeable;
  }

  public void setAgreeable(boolean agreeable) {
    this.agreeable = agreeable;
  }

  public List<User> getAgreeingNetwork() {
    return agreeing_network;
  }

  public void setAgreeingNetwork(List<User> agreeingNetwork) {
    this.agreeing_network = agreeingNetwork;
  }

  public Topic getTopic() {
    return topic;
  }

  public void setTopic(Topic topic) {
    this.topic = topic;
  }

  public Objekt getObjekt() {
    return objekt;
  }

  public void setObjekt(Objekt objekt) {
    this.objekt = objekt;
  }
}
