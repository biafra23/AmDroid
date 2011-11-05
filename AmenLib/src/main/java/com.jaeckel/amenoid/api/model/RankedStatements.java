package com.jaeckel.amenoid.api.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * User: biafra
 * Date: 9/29/11
 * Time: 1:38 AM
 */
public class RankedStatements implements Parcelable {
  private Integer   rank;
  private Statement statement;

  public RankedStatements() {
  }

  public Integer getRank() {
    return rank;
  }

  public void setRank(Integer rank) {
    this.rank = rank;
  }

  public Statement getStatement() {
    return statement;
  }

  public void setStatement(Statement statement) {
    this.statement = statement;
  }

  @Override
  public String toString() {
    return "RankedStatements{" +
           "rank=" + rank +
           ", statement=" + statement +
           '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RankedStatements that = (RankedStatements) o;

    if (rank != null ? !rank.equals(that.rank) : that.rank != null) return false;
    if (statement != null ? !statement.equals(that.statement) : that.statement != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = rank != null ? rank.hashCode() : 0;
    result = 31 * result + (statement != null ? statement.hashCode() : 0);
    return result;
  }

/*
    *
    *   PARCEL STUFF
    *
    */

  public static final Parcelable.Creator<RankedStatements> CREATOR = new Parcelable.Creator<RankedStatements>() {

    public RankedStatements[] newArray(int size) {
      return new RankedStatements[size];
    }

    public RankedStatements createFromParcel(Parcel source) {
      return new RankedStatements(source);
    }
  };

  private RankedStatements(Parcel in) {
    readFromParcel(in);
  }

  @Override
  public int describeContents() {
    return 0;
  }


  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(rank);
    dest.writeParcelable(statement, 0);
  }

  private void readFromParcel(Parcel in) {
    final ClassLoader cl = getClass().getClassLoader();

    rank = (Integer)in.readValue(cl);
    statement = in.readParcelable(cl);

  }

}
