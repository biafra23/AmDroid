package com.jaeckel.amdroid.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * User: biafra
 * Date: 9/29/11
 * Time: 1:38 AM
 */
public class RankedStatements implements Parcelable {
  private Integer   rank;
  private Statement statement;

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
    dest.writeInt(rank);
    dest.writeParcelable(statement, 0);
  }

  private void readFromParcel(Parcel in) {
    rank = in.readInt();
    statement = in.readParcelable(getClass().getClassLoader());

  }

}
