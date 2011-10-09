package com.jaeckel.amdroid.api.model;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

/**
 * User: biafra
 * Date: 10/9/11
 * Time: 1:17 PM
 */
public class ServerError {

  private String error;

  public ServerError() {
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ServerError that = (ServerError) o;

    if (error != null ? !error.equals(that.error) : that.error != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return error != null ? error.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "ServerError{" +
           "error='" + error + '\'' +
           '}';
  }

  public String json() {

    GsonBuilder builder = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    return builder.create().toJson(this);
  }
}
