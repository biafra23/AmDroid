package com.jaeckel.amdroid.api;

import com.jaeckel.amdroid.api.model.Amen;
import com.jaeckel.amdroid.api.model.Statement;
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

  public boolean amen(Amen a);

  public boolean dispute(Amen a, String dispute);

  public long takeBack(Amen a);

  public List<Amen> getAmenForUser(User u);

  public UserInfo getUserInfo(User u);

  public void addStatement(Statement statement);
}
