package com.jaeckel.amdroid.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.AmenServiceImpl;

/**
 * User: biafra
 * Date: 9/22/11
 * Time: 1:05 AM
 */
public class AmdroidApp extends Application {

  public static final String TAG = "amdroid/AmdroidApp";
  private static AmdroidApp instance;

  //  public final static
  private String      authCookie;
  private AmenService service;


  public AmdroidApp() {


  }

  @Override
  public void onCreate() {

    Log.v(TAG, "onCreate");


  }

  public static AmdroidApp getInstance() {
    if (instance == null) {
      instance = new AmdroidApp();
    }
    return instance;
  }

  public String getAuthCookie() {
    return authCookie;
  }

  public void setAuthCookie(String authCookie) {
    this.authCookie = authCookie;
  }

  public AmenService getService(String username, String password) {
    if (service == null) {
      service = new AmenServiceImpl();

      service.init(username, password);
    }
    return service;
  }
}
