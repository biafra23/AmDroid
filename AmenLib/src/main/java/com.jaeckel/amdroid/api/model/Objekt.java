package com.jaeckel.amdroid.api.model;

/**
 * User: biafra
 * Date: 9/24/11
 * Time: 2:17 AM
 */
public class Objekt {

  private int kindId;
  private String name;
  private ObjektKey key;

  @Override
  public String toString() {
    return "Objekt{" +
           "kindId=" + kindId +
           ", name='" + name + '\'' +
           ", key=" + key +
           '}';
  }
}
