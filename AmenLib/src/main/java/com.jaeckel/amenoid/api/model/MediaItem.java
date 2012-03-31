package com.jaeckel.amenoid.api.model;

/**
 * @author biafra
 * @date 3/31/12 4:41 PM
 */
public class MediaItem {

  private String type;
  private String contentUrl;
  private String contributor_name;


  @Override public String toString() {
    final StringBuffer sb = new StringBuffer();
    sb.append("MediaItem");
    sb.append("{type='").append(type).append('\'');
    sb.append(", contenUrl='").append(contentUrl).append('\'');
    sb.append(", contributor_name='").append(contributor_name).append('\'');
    sb.append('}');
    return sb.toString();
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getContentUrl() {
    return contentUrl;
  }

  public void setContentUrl(String contentUrl) {
    this.contentUrl = contentUrl;
  }

  public String getContributor_name() {
    return contributor_name;
  }

  public void setContributor_name(String contributor_name) {
    this.contributor_name = contributor_name;
  }
}
