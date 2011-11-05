package com.jaeckel.amenoid;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.jaeckel.amenoid.app.AmdroidApp;

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

//            case MENU_MAP:
//                Toast.makeText(this, "MENU_MAP", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(this, Map.class));
//
//                return true;

//            case MENU_SAVE:
//                Toast.makeText(this, "MENU_SAVE", Toast.LENGTH_SHORT).show();
//
//                startActivity(new Intent(this, Map.class));
//                return true;

    }

    return false;
  }

  @Override
  protected void onListItemClick(ListView l, View v, int pos, long id) {
    super.onListItemClick(l, v, pos, id);
    Log.d(TAG, "onListItemClick");

  }

  @Override
  protected void onStop() {
    super.onStop();

    Log.d(TAG, "onStop");

    if (AmdroidApp.getInstance().getService().getMe() == null) {
      Intent restart = new Intent(this, AmenListActivity.class);

      startActivity(restart);
    }


  }

}
