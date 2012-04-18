package com.jaeckel.amenoid.api.model;

import java.util.Date;

/**
 * @author biafra
 * @date 4/18/12 9:42 PM
 */
public class Comment {

  private Long id;
  private String body;
  private Date createdAt;
  private User user;

  @Override public String toString() {
    final StringBuffer sb = new StringBuffer();
    sb.append("Comment");
    sb.append("{id=").append(id);
    sb.append(", body='").append(body).append('\'');
    sb.append(", createdAt=").append(createdAt);
    sb.append(", user=").append(user);
    sb.append('}');
    return sb.toString();
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }
}
