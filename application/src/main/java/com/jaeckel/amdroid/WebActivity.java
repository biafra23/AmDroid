package com.jaeckel.amdroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.jaeckel.amdroid.app.AmdroidApp;

/**
 * User: biafra
 * Date: 9/21/11
 * Time: 11:59 PM
 */
public class WebActivity extends Activity {

  private static String TAG = "amdroid/WebActivity";

  private WebView webView;

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

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    CookieSyncManager.getInstance().sync();
    final boolean hasCookies = CookieManager.getInstance().hasCookies();
    if (hasCookies) {
      Log.v(TAG, "Cookies found before WebView started");
      String authCookie = CookieManager.getInstance().getCookie("https://getamen.com/");
      AmdroidApp.getInstance().setAuthCookie(authCookie);
    }

    setContentView(R.layout.web);

    webView = (WebView) findViewById(R.id.webview);

    webView.getSettings().setJavaScriptEnabled(true);
    webView.getSettings().setSaveFormData(true);
    webView.getSettings().setSavePassword(true);
    webView.loadUrl("https://getamen.com/sign-in");
//    webView.loadUrl("http://www.ccc.de");

    webView.setWebViewClient(new HelloWebViewClient());


    final boolean hasCookies2 = CookieManager.getInstance().hasCookies();
    if (hasCookies2) {
      Log.v(TAG, "Cookies found after WebView started (2)");
    }
  }

  private class HelloWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      view.loadUrl(url);
      return true;
    }


  }

}