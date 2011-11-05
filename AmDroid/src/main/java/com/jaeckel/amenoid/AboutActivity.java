package com.jaeckel.amenoid;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

/**
 * User: biafra
 * Date: 11/5/11
 * Time: 8:13 PM
 */
public class AboutActivity extends Activity {
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.about);

    TextView versionTextView = (TextView) findViewById(R.id.version);
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

    versionTextView.setText("Version: " + versionName + "(" + versionCode + ")");
    authorTextView.setText("Author: Dirk Jaeckel <dirk@jaeckel.com>\n");


  }
}