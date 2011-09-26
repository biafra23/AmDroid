package com.jaeckel.amdroid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.app.AmdroidApp;

/**
 * User: biafra
 * Date: 9/25/11
 * Time: 9:40 PM
 */
public class MakeStatementActivity extends Activity {

  private AmenService service;


  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String username = prefs.getString("user_name", null);
        String password = prefs.getString("password", null);

        if (username == null || password == null) {
          startActivity(new Intent(this, EditPreferencesActivity.class));
        }
        username = prefs.getString("user_name", null);
        password = prefs.getString("password", null);

        service = AmdroidApp.getInstance().getService(username, password);

        setContentView(R.layout.make_statement);

  }
}