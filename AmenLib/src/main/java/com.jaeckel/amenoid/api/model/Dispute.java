package com.jaeckel.amenoid.api.model;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

/**
 * User: biafra
 * Date: 9/25/11
 * Time: 3:18 AM
 */
public class Dispute {

  private Integer   kindId;
  private Long      referringAmenId;
  private Statement statement;


  public int getKindId() {
    return kindId;
  }

  public void setKindId(int kindId) {
    this.kindId = kindId;
  }

  public long getReferringAmenId() {
    return referringAmenId;
  }

  public void setReferringAmenId(long referringAmenId) {
    this.referringAmenId = referringAmenId;
  }

  public Statement getStatement() {
    return statement;
  }

  public void setStatement(Statement statement) {
    this.statement = statement;
  }

  @Override
  public String toString() {
    return "Dispute{" +
           "kind_id=" + kindId +
           ", referring_amen_id=" + referringAmenId +
           ", statement=" + statement +
           '}';
  }

  public String json() {

    GsonBuilder builder = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    return builder.create().toJson(this);
  }
}
