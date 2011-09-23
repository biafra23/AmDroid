package com.jaeckel.amdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class HelloAndroidActivity extends Activity {

  private static String TAG = "amdroid/HelloAndroidActivity";

  /**
   * Called when the activity is first created.
   *
   * @param savedInstanceState If the activity is being re-initialized after
   *                           previously being shut down then this Bundle contains the data it most
   *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.v(TAG, "onCreate");
    setContentView(R.layout.main);

    CookieSyncManager.createInstance(this);
    CookieSyncManager.getInstance().sync();
    final boolean hasCookies = CookieManager.getInstance().hasCookies();
    if (!hasCookies) {
      Log.v(TAG, "CookieManager has cookies");

      String c = CookieManager.getInstance().getCookie("https://getamen.com/");



      Log.v(TAG, "Cookie: " + c);

    } else {

      Log.v(TAG, "CookieManager has no cookies");

      Intent web = new Intent(this, WebActivity.class);

      startActivity(web);
    }

  }

  @Override
  public void onResume() {
    super.onResume();
    CookieSyncManager.getInstance().startSync();
  }

  @Override
  public void onPause() {
    super.onPause();
    CookieSyncManager.getInstance().stopSync();
  }

}

