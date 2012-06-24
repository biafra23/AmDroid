package com.jaeckel.amenoid;

import java.text.SimpleDateFormat;

import com.actionbarsherlock.app.SherlockActivity;
import com.github.ignition.location.annotations.IgnitedLocation;
import com.github.ignition.location.annotations.IgnitedLocationActivity;
import com.github.ignition.location.templates.OnIgnitedLocationChangedListener;
import com.github.ignition.location.utils.IgnitedLocationSupport;
import com.jaeckel.amenoid.util.Log;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

/**
 * User: biafra
 * Date: 11/5/11
 * Time: 8:13 PM
 */
public class AboutActivity extends Activity {

  private final static String TAG = AboutActivity.class.getSimpleName();

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.about);

    TextView versionTextView = (TextView) findViewById(R.id.version);
    TextView buildTimeTextView = (TextView) findViewById(R.id.build_time);
    TextView authorTextView = (TextView) findViewById(R.id.author);

    setTitle("About Amenoid");

    String myPackageName = this.getPackageName();

    int versionCode = 0;
    String versionName = null;
    try {

      PackageInfo packageInfo = this.getPackageManager().getPackageInfo(myPackageName, 0);

      versionName = packageInfo.versionName;
      versionCode = packageInfo.versionCode;

    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    versionTextView.setText("Version: " + versionName + " (" + versionCode + ")");
    authorTextView.setText("Author: Dirk Jaeckel <amenoid@dirk.jaeckel.name>\n");
    final String timestampString = getResources().getString(R.string.buildtimestamp);

    buildTimeTextView.setText("Built: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.valueOf(timestampString)));

//    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("ssh://biafra@biafra.diskordia.com:22/#biafra")));
//    startActivity(new Intent(Intent.ACTION_PICK, Uri.parse("ssh://biafra@biafra.diskordia.com:22/#biafra")));

  }
  // MUST BE OVERRIDDEN OR IGNITION LOCATION WON'T WORK!
    @Override
    public void onResume() {
        super.onResume();
    }

    // MUST BE OVERRIDDEN OR IGNITION LOCATION WON'T WORK!
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



      // If location is still to be determined, show a "wait for location" dialog
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == R.id.ign_loc_dialog_wait_for_fix) {
            return IgnitedLocationSupport.createWaitForLocationDialog(this);
        } else if (id == R.id.ign_loc_dialog_no_providers_enabled) {
            return IgnitedLocationSupport.createNoProvidersEnabledDialog(this, true).create();
        }
        return super.onCreateDialog(id);
    }
}