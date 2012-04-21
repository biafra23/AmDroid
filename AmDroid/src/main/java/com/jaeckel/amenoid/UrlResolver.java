package com.jaeckel.amenoid;

import java.io.IOException;
import java.util.List;

import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.app.AmenoidApp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

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

      } else if ("topics".equalsIgnoreCase(pathSegments.get(0))) {

        String topic = pathSegments.get(1);
        Long id = -1L;
        try {
          id = Long.parseLong(topic);

        } catch (Exception e) {
          // id still -1
        }
        Intent startScoreboard = new Intent(this, ScoreBoardActivity.class);
        if (id > 0) {
          startScoreboard.putExtra(Constants.EXTRA_TOPIC_NAME, id + "");
        } else {
          startScoreboard.putExtra(Constants.EXTRA_TOPIC_NAME, topic);
        }
        startActivity(startScoreboard);

        finish();

      } else {
        String name = pathSegments.get(0);
        // Is it a comment?
        String sement2 = pathSegments.get(1);
        if ("amen".equals(sement2)) {

          try {
            Amen amen = AmenoidApp.getInstance().getService().getAmenByUrl(uri.toString() + ".json");
            Intent startAmenDetailActivity = new Intent(this, AmenDetailActivity.class);
            startAmenDetailActivity.putExtra(Constants.EXTRA_AMEN, amen);

            startActivity(startAmenDetailActivity);

            finish();

          } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

          }


        }

        if ("account".equals(name)) {
          Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
          startActivity(browserIntent);

        }
        Intent startUserDetailActivity = new Intent(this, UserDetailActivity.class);
        startUserDetailActivity.putExtra(Constants.EXTRA_USER_ID_STRING, name);

        startActivity(startUserDetailActivity);

        finish();
      }
    }
  }

  // TODO: add support for the following urls:
  // Comment:
  // http://getamen.com/jbrennholt/amen/bi-sexual-is-the-best-plan-b-ever
  // user pages
  // http://getamen.com/anduela

}