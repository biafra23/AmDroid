package com.jaeckel.amenoid.api.model;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * User: biafra
 * Date: 9/24/11
 * Time: 2:09 AM
 */
public class Statement implements Parcelable {

  private Long       id;
  private Long       totalAmenCount;
  private Boolean    agreeable;
  private List<User> agreeingNetwork;
  private Long       agreeingNetworkCount;
  private Topic      topic;
  private Objekt     objekt;
  private User       firstPoster;
  private Date       firstPostedAt;
  private Long       firstAmenId;
  private Integer    rankInTopic;
  private String     slug;

  static private transient final Logger log = LoggerFactory.getLogger(Statement.class.getSimpleName());

  public Statement() {
  }

  public Statement(Objekt o, Topic t) {
    this.topic = t;
    this.objekt = o;
  }

  public String toDisplayString() {
    return objekt.getName() + " is " + (topic.isBest() ? "the Best " : "the Worst ")
           + (objekt.getKindId() == 1 ? " Place for " : "") + topic.getDescription() + " " + topic.getScope();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Statement statement = (Statement) o;

    if (agreeable != null ? !agreeable.equals(statement.agreeable) : statement.agreeable != null) return false;
    if (agreeingNetwork != null ? !agreeingNetwork.equals(statement.agreeingNetwork) : statement.agreeingNetwork != null)
      return false;
    if (agreeingNetworkCount != null ? !agreeingNetworkCount.equals(statement.agreeingNetworkCount) : statement.agreeingNetworkCount != null)
      return false;
    if (firstAmenId != null ? !firstAmenId.equals(statement.firstAmenId) : statement.firstAmenId != null) return false;
    if (firstPostedAt != null ? !firstPostedAt.equals(statement.firstPostedAt) : statement.firstPostedAt != null)
      return false;
    if (firstPoster != null ? !firstPoster.equals(statement.firstPoster) : statement.firstPoster != null) return false;
    if (id != null ? !id.equals(statement.id) : statement.id != null) return false;
    if (objekt != null ? !objekt.equals(statement.objekt) : statement.objekt != null) return false;
    if (rankInTopic != null ? !rankInTopic.equals(statement.rankInTopic) : statement.rankInTopic != null) return false;
    if (topic != null ? !topic.equals(statement.topic) : statement.topic != null) return false;
    if (totalAmenCount != null ? !totalAmenCount.equals(statement.totalAmenCount) : statement.totalAmenCount != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (totalAmenCount != null ? totalAmenCount.hashCode() : 0);
    result = 31 * result + (agreeable != null ? agreeable.hashCode() : 0);
    result = 31 * result + (agreeingNetwork != null ? agreeingNetwork.hashCode() : 0);
    result = 31 * result + (agreeingNetworkCount != null ? agreeingNetworkCount.hashCode() : 0);
    result = 31 * result + (topic != null ? topic.hashCode() : 0);
    result = 31 * result + (objekt != null ? objekt.hashCode() : 0);
    result = 31 * result + (firstPoster != null ? firstPoster.hashCode() : 0);
    result = 31 * result + (firstPostedAt != null ? firstPostedAt.hashCode() : 0);
    result = 31 * result + (firstAmenId != null ? firstAmenId.hashCode() : 0);
    result = 31 * result + (rankInTopic != null ? rankInTopic.hashCode() : 0);
    return result;
  }

  @Override public String toString() {
    final StringBuffer sb = new StringBuffer();
    sb.append("Statement");
    sb.append("{id=").append(id);
    sb.append(", totalAmenCount=").append(totalAmenCount);
    sb.append(", agreeable=").append(agreeable);
    sb.append(", agreeingNetwork=").append(agreeingNetwork);
    sb.append(", agreeingNetworkCount=").append(agreeingNetworkCount);
    sb.append(", topic=").append(topic);
    sb.append(", objekt=").append(objekt);
    sb.append(", firstPoster=").append(firstPoster);
    sb.append(", firstPostedAt=").append(firstPostedAt);
    sb.append(", firstAmenId=").append(firstAmenId);
    sb.append(", rankInTopic=").append(rankInTopic);
    sb.append(", slug='").append(slug).append('\'');
    sb.append('}');
    return sb.toString();
  }

  public Integer getRankInTopic() {
    return rankInTopic;
  }

  public void setRankInTopic(Integer rankInTopic) {
    this.rankInTopic = rankInTopic;
  }

  public String json() {

    GsonBuilder builder = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    return builder.create().toJson(this);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getTotalAmenCount() {
    return totalAmenCount;
  }

  public void setTotalAmenCount(Long totalAmenCount) {
    this.totalAmenCount = totalAmenCount;
  }

  public Boolean isAgreeable() {
    return agreeable;
  }

  public void setAgreeable(Boolean agreeable) {
    this.agreeable = agreeable;
  }

  public List<User> getAgreeingNetwork() {
    return agreeingNetwork;
  }

  public void setAgreeingNetwork(List<User> agreeingNetwork) {
    this.agreeingNetwork = agreeingNetwork;
  }

  public Topic getTopic() {
    return topic;
  }

  public void setTopic(Topic topic) {
    this.topic = topic;
  }

  public Objekt getObjekt() {
    return objekt;
  }

  public void setObjekt(Objekt objekt) {
    this.objekt = objekt;
  }


  public User getFirstPoster() {
    return firstPoster;
  }

  public Date getFirstPostedAt() {
    return firstPostedAt;
  }

  public void setFirstPostedAt(Date firstPostedAt) {
    this.firstPostedAt = firstPostedAt;
  }

  /*
  *
  *   PARCEL STUFF
  *
  */

  public static final Parcelable.Creator<Statement> CREATOR = new Parcelable.Creator<Statement>() {

    public Statement[] newArray(int size) {
      return new Statement[size];
    }

    public Statement createFromParcel(Parcel source) {
      log.debug("createFromParcel: " + source);
      return new Statement(source);
    }
  };

  private Statement(Parcel in) {
    log.debug("Statement() parcel: " + in);

    readFromParcel(in);
  }

  @Override
  public int describeContents() {
    return 0;
  }


  @Override
  public void writeToParcel(Parcel dest, int flags) {
    log.debug("writeToParcel");
    dest.writeValue(id);
    dest.writeValue(totalAmenCount);
    dest.writeValue(agreeable);
    dest.writeList(agreeingNetwork);
    dest.writeValue(agreeingNetworkCount);
    dest.writeParcelable(topic, flags);
    dest.writeParcelable(objekt, flags);
    dest.writeParcelable(firstPoster, flags);
    dest.writeValue(firstPostedAt);
    dest.writeValue(firstAmenId);
    dest.writeValue(rankInTopic);
    dest.writeString(slug);

    log.debug("writeToParcel. done.");
  }

  private void readFromParcel(Parcel in) {
    final ClassLoader cl = getClass().getClassLoader();
    log.debug("readFromParcel");
    id = (Long) in.readValue(cl);
    totalAmenCount = (Long) in.readValue(cl);
    agreeable = (Boolean) in.readValue(cl);
    agreeingNetwork = in.readArrayList(cl);
    agreeingNetworkCount = (Long) in.readValue(cl);
    topic = in.readParcelable(cl);
    objekt = in.readParcelable(cl);
    firstPoster = in.readParcelable(cl);
    firstPostedAt = (Date) in.readValue(cl);
    firstAmenId = (Long) in.readValue(cl);
    rankInTopic = (Integer) in.readValue(cl);
    slug = (String) in.readString();

    log.debug("readFromParcel. done.");

  }

  public Long getFirstAmenId() {
    return firstAmenId;
  }

  public void setFirstAmenId(Long firstAmenId) {
    this.firstAmenId = firstAmenId;
  }

  public Long getAgreeingNetworkCount() {
    return agreeingNetworkCount;
  }

  public void setAgreeingNetworkCount(Long agreeingNetworkCount) {
    this.agreeingNetworkCount = agreeingNetworkCount;
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }
}
