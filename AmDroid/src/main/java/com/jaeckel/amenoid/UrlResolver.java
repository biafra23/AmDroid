package com.jaeckel.amenoid;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.util.List;

/**
 * User: biafra
 * Date: 11/10/11
 * Time: 10:55 PM
 */
public class UrlResolver extends Activity {
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent intent = getIntent();
    // check if this intent is started via custom scheme link
    if (Intent.ACTION_VIEW.equals(intent.getAction())) {
      Uri uri = intent.getData();
//      Toast.makeText(this, "uri: " + uri, Toast.LENGTH_LONG).show();
      // may be some test here with your custom uri
      List<String> pathSegments = uri.getPathSegments();
      if ("users".equalsIgnoreCase(pathSegments.get(0))) {

        Long id = Long.valueOf(pathSegments.get(1));

        Intent startUserDetailActivity = new Intent(this, UserDetailActivity.class);
        startUserDetailActivity.putExtra(Constants.EXTRA_USER_ID, id);

        startActivity(startUserDetailActivity);

        finish();
      }
      if ("topics".equalsIgnoreCase(pathSegments.get(0))) {

        Long id = Long.valueOf(pathSegments.get(1));

        Intent startScoreboard = new Intent(this, ScoreBoardActivity.class);
        startScoreboard.putExtra(Constants.EXTRA_TOPIC_ID, id);

        startActivity(startScoreboard);

        finish();
      }

    }


  }


}