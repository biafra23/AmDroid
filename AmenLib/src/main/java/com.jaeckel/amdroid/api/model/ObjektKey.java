package com.jaeckel.amdroid.api.model;

/**
 * User: biafra
 * Date: 9/24/11
 * Time: 2:18 AM
 */
public class ObjektKey {

  private long id;
  private String type; //local, freebase

  @Override
  public String toString() {
    return "ObjektKey{" +
           "id=" + id +
           ", type='" + type + '\'' +
           '}';
  }
}
