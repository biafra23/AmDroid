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

  public AmenService init(String username, String password);
  public AmenService init(String authToken, User me);

  public final int AMEN_KIND_STATEMENT = 0;
  public final int AMEN_KIND_AMEN      = 1;
  public final int AMEN_KIND_DISPUTE   = 2;

  public final static int OBJEKT_KIND_PERSON = 0;
  public final static int OBJEKT_KIND_PLACE  = 1;
  public final static int OBJEKT_KIND_THING  = 2;


  public User getMe();

  public List<Amen> getFeed();

  public List<Amen> getFeed(long sinceId, int pageSize);
  public List<Amen> getFeed(long beforeId, long sinceId, int pageSize);

  public boolean follow(User u);

  public boolean unfollow(User u);

  public Amen amen(Statement a);

  public Amen amen(Long amenId);

  public Long dispute(Amen dispute);

  public boolean takeBack(Long statementId);

  public List<Amen> getAmenForUser(Long userId);

  public UserInfo getUserInfo(Long id);

  public void addStatement(Statement statement);

  public Statement getStatementForId(Long id);

  public Amen getAmenForId(Long id);

  public Topic getTopicsForId(Long id, Long includeStatementId) throws IOException;

  public List<User> followers(Long id);

  public List<User> following(Long id);

  public List<Objekt> objektsForQuery(CharSequence query, int kindId, Double lat, Double lon);

  public String getAuthToken();
}
