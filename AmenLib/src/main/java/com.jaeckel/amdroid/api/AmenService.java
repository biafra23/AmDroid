package com.jaeckel.amdroid.api;

import com.jaeckel.amdroid.api.model.Amen;
import com.jaeckel.amdroid.api.model.Objekt;
import com.jaeckel.amdroid.api.model.Statement;
import com.jaeckel.amdroid.api.model.Topic;
import com.jaeckel.amdroid.api.model.User;
import com.jaeckel.amdroid.api.model.UserInfo;

import java.io.IOException;
import java.util.List;

/**
 * User: biafra
 * Date: 9/23/11
 * Time: 8:35 PM
 */
public interface AmenService {

  public final int AMEN_KIND_STATEMENT = 0;
  public final int AMEN_KIND_AMEN      = 1;
  public final int AMEN_KIND_DISPUTE   = 2;

  public final static int OBJEKT_KIND_PERSON = 0;
  public final static int OBJEKT_KIND_PLACE  = 1;
  public final static int OBJEKT_KIND_THING  = 2;

  public final static int FEED_TYPE_FOLLOWING   = 0;
  public final static int FEED_TYPE_INTERESTING = 1;

  public User getMe();

  public String getAuthToken();

  public AmenService init(String username, String password) throws IOException;

  public AmenService init(String authToken, User me);

  public List<Amen> getFeed(int type) throws IOException;

  public List<Amen> getFeed(long sinceId, int pageSize, int type) throws IOException;

  public List<Amen> getFeed(long beforeId, long sinceId, int pageSize, int type) throws IOException;

  public boolean follow(User u) throws IOException;

  public boolean unfollow(User u) throws IOException;

  public Amen amen(Statement a) throws IOException;

  public Amen amen(Long amenId) throws IOException;

  public Long dispute(Amen dispute) throws IOException;

  public boolean takeBack(Long statementId) throws IOException;

  public List<Amen> getAmenForUser(Long userId) throws IOException;

  public UserInfo getUserInfo(Long id) throws IOException;

  public Amen addStatement(Statement statement) throws IOException;

  public Statement getStatementForId(Long id) throws IOException;

  public Amen getAmenForId(Long id) throws IOException;

  public Topic getTopicsForId(Long id, Long includeStatementId) throws IOException;

  public List<User> followers(Long id) throws IOException;

  public List<User> following(Long id) throws IOException;

  public List<Objekt> objektsForQuery(CharSequence query, int kindId, Double lat, Double lon) throws IOException;


}
