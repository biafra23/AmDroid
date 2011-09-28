package com.jaeckel.amdroid.api.model;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

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

  @Override
  public String toString() {
    return "Statement{" +
           "id=" + id +
           ", totalAmenCount=" + totalAmenCount +
           ", agreeable=" + agreeable +
           ", agreeingNetwork=" + agreeingNetwork +
           ", topic=" + topic +
           ", objekt=" + objekt +
           ", firstPoster=" + firstPoster +
           ", firstPostedAt=" + firstPostedAt +
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
