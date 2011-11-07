package com.jaeckel.amenoid.api.model;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

/**
 * User: biafra
 * Date: 11/7/11
 * Time: 1:39 AM
 */
public class Count {

  int count;


  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  @Override
  public String toString() {
    return "Count{" +
           "count=" + count +
           '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Count count1 = (Count) o;

    if (count != count1.count) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return count;
  }

  public String json() {

    GsonBuilder builder = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    return builder.create().toJson(this);
  }
}
