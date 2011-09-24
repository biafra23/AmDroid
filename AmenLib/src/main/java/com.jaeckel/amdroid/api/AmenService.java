package com.jaeckel.amdroid.api;

import com.jaeckel.amdroid.api.model.Amen;

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
  public  List<Amen> getFeed(long sinceId, int pageSize);

}
