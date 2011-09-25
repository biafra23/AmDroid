package com.jaeckel.amdroid;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * User: biafra
 * Date: 9/25/11
 * Time: 7:02 AM
 */
public class EditPreferencesActivity extends PreferenceActivity {


    final private static int MENU_MAP = 0;
    final private static int MENU_SAVE = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

//        menu.add(Menu.NONE, MENU_MAP, Menu.NONE, "Back to Map");
//        menu.add(Menu.NONE, MENU_SAVE, Menu.NONE, "Save");


        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

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


}
