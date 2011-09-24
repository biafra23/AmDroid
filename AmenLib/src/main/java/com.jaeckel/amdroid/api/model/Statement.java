package com.jaeckel.amdroid.api.model;

import java.util.List;

/**
 * User: biafra
 * Date: 9/24/11
 * Time: 2:09 AM
 */
public class Statement {

  private long id;
  private long totalAmenCount;
  private boolean agreeable;
  private List<User> agreeingNetwork;
  private Topic topic;
  private Objekt objekt;

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
}
