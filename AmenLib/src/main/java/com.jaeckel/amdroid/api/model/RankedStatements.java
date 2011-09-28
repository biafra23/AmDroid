package com.jaeckel.amdroid.api.model;

/**
 * User: biafra
 * Date: 9/29/11
 * Time: 1:38 AM
 */
public class RankedStatements {
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
}
