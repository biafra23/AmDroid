package com.jaeckel.amenoid.api;

/**
 * @author biafra
 * @date 5/14/12 7:08 PM
 */
public class SignupFailedException extends Exception {

  String field;
  String msg;


  public SignupFailedException(final String field, final String msg) {

    this.field = field;
    this.msg = msg;

  }


  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  @Override public String toString() {
    final StringBuffer sb = new StringBuffer();
    sb.append("SignupFailedException");
    sb.append("{field='").append(field).append('\'');
    sb.append(", message='").append(msg).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
