package com.jaeckel.amdroid.api;

import com.jaeckel.amdroid.api.model.Amen;
import com.jaeckel.amdroid.api.model.User;

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

  public List<Amen> getFeed();

  public List<Amen> getFeed(long sinceId, int pageSize);

  public boolean follow(User u);

  public boolean unfollow(User u);

  public boolean amen(Amen a);

  public boolean dispute(Amen a, String dispute);

  public long createAmen(Amen a);

  public long takeBack(Amen a);
}
