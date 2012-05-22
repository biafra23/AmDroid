package com.jaeckel.amenoid;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.jaeckel.amenoid.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

/**
 * User: biafra
 * Date: 9/25/11
 * Time: 7:02 AM
 */
public class EditPreferencesActivity extends PreferenceActivity {


  final private static int    MENU_MAP  = 0;
  final private static int    MENU_SAVE = 1;
  private static final String TAG       = "EditPreferencesActivity";


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    addPreferencesFromResource(R.xml.preferences);
    setTitle("Preferences");
    Log.d(TAG, "onCreate()");

  }

  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);

//        menu.add(Menu.NONE, MENU_MAP, Menu.NONE, "Back to Map");
//        menu.add(Menu.NONE, MENU_SAVE, Menu.NONE, "Save");


    return true;
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);

    Log.d(TAG, "onOptionsItemSelected");

    switch (item.getItemId()) {

    }

    return false;
  }

  @Override
  protected void onListItemClick(ListView l, View v, int pos, long id) {
    super.onListItemClick(l, v, pos, id);
    Log.d(TAG, "onListItemClick");

  }

}
