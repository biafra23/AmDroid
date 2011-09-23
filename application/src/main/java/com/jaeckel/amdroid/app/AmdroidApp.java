package com.jaeckel.amdroid.app;

import android.app.Application;
import android.util.Log;

/**
 * User: biafra
 * Date: 9/22/11
 * Time: 1:05 AM
 */
public class AmdroidApp extends Application {

  public static final String TAG = "amdroid/AmdroidApp";
  private static AmdroidApp instance;

//  public final static
  private String authCookie;


  @Override
  public void onCreate() {
    Log.v(TAG, "onCreate");
//    CookieSyncManager.createInstance(this);
//    CookieSyncManager.getInstance().sync();
//    final boolean hasCookies = CookieManager.getInstance().hasCookies();
//    if (!hasCookies) {
//      Log.e(TAG, "CookieManager has cookies");
//
//      String c = CookieManager.getInstance().getCookie("https://getamen.com/");
//
//      Log.e(TAG, "Cookie: " + c);
//
//    } else {
//      Log.d(TAG, "CookieManager has no cookies");
//
//    }
  }

  public static AmdroidApp getInstance() {
    if (instance != null) {
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
}
