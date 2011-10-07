package com.jaeckel.amdroid.api;

import com.jaeckel.amdroid.api.model.Amen;
import com.jaeckel.amdroid.api.model.Dispute;
import com.jaeckel.amdroid.api.model.Objekt;
import com.jaeckel.amdroid.api.model.Statement;
import com.jaeckel.amdroid.api.model.Topic;
import com.jaeckel.amdroid.api.model.User;
import com.jaeckel.amdroid.api.model.UserInfo;

import java.util.List;

/**
 * User: biafra
 * Date: 9/23/11
 * Time: 8:35 PM
 */
public interface AmenService {

  public AmenService init(String username, String password);


  public String getCsrfToken();

  public String getCookie();

  public User getMe();

  public List<Amen> getFeed();

  public List<Amen> getFeed(long sinceId, int pageSize);

  public boolean follow(User u);

  public boolean unfollow(User u);

  public Amen amen(Statement a);

  public Amen amen(Long amenId);

  public boolean dispute(Dispute dispute);

  public boolean takeBack(Amen a);

  public List<Amen> getAmenForUser(Long userId);

  public UserInfo getUserInfo(Long id);

  public void addStatement(Statement statement);

  public Statement getStatementForId(Long id);

  public Amen getAmenForId(Long id);

  public Topic getTopicsForId(Long id);

  public List<User> followers(Long id);
  
  public List<User> following(Long id);
  
  public List<Objekt> objektsForQuery(CharSequence query, int kindId, Double lat, Double lon);
}
