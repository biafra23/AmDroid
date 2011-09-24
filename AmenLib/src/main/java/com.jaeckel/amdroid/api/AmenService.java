package com.jaeckel.amdroid.api;

/**
 * User: biafra
 * Date: 9/23/11
 * Time: 8:35 PM
 */
public interface AmenService {
  
  public AmenService init(String username, String password);
  public String getCsrfToken();
  public String getCookie();
}
