package com.jaeckel.amdroid.api.model;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: biafra
 * Date: 9/24/11
 * Time: 2:09 AM
 */
public class Statement {

  private Long       id;
  private Long       totalAmenCount;
  private Boolean    agreeable;
  private List<User> agreeingNetwork;
  private Topic      topic;
  private Objekt     objekt;
  private User       firstPoster;
  private Date       firstPostedAt;

  public Statement(Objekt o, Topic t) {
    this.topic = t;
    this.objekt = o;
  }

  public String toDisplayString() {

    return objekt.getName() + " is " + (topic.isBest() ? "the Best " : "the Worst ") + (objekt.getKindId() == 1 ? " Place for " : "") + topic.getDescription() + " " + topic.getScope();

  }

  public Statement(Long id) {
    this.id = id;
  }

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

        this.topic = new Topic(o.getJSONObject("topic"));
      }
      if (o.has("agreeable")) {
        this.agreeable = o.getBoolean("agreeable");
      } else {
        this.agreeable = false;
      }

      if (o.has("first_poster") && !o.isNull("first_poster")) {


        firstPoster = new User(o.getJSONObject("first_poster"));

      }
      if (o.has("first_posted_at")) {
        firstPostedAt = Amen.parseIso8601DateJoda(o.getString("first_posted_at"));
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

    GsonBuilder builder = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    return builder.create().toJson(this);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getTotalAmenCount() {
    return totalAmenCount;
  }

  public void setTotalAmenCount(Long totalAmenCount) {
    this.totalAmenCount = totalAmenCount;
  }

  public Boolean isAgreeable() {
    return agreeable;
  }

  public void setAgreeable(Boolean agreeable) {
    this.agreeable = agreeable;
  }

  public List<User> getAgreeingNetwork() {
    return agreeingNetwork;
  }

  public void setAgreeingNetwork(List<User> agreeingNetwork) {
    this.agreeingNetwork = agreeingNetwork;
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


  public User getFirstPoster() {
    return firstPoster;
  }

  public Date getFirstPostedAt() {
    return firstPostedAt;
  }

  public void setFirstPostedAt(Date firstPostedAt) {
    this.firstPostedAt = firstPostedAt;
  }
}
