package com.jaeckel.amenoid;

import java.io.IOException;
import java.util.List;

import com.actionbarsherlock.app.SherlockActivity;
import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.api.model.Statement;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.util.AmenLibTask;
import com.jaeckel.amenoid.util.Log;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

/**
 * User: biafra
 * Date: 11/10/11
 * Time: 10:55 PM
 */
public class UrlResolver extends SherlockActivity {

  private static String TAG = "UrlResolver";

  //  Floria Weber: 6.12.2012
//  in rails ist das so implementiert, das unsere eigenen urls immer
//  vorrang haben. defininiert sind zb:
//  /account
//  /about
//  /help
//  /find-friends
//  /sign-up
//  /sign-in
//  /forgot-password
//  /feed
//  /things
//  /people
//  /places
//  /news
//  /topics
//  /statements
//  /follows
//  /invitations
//  /search
//  /amen
//  /notifications
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent intent = getIntent();
    // check if this intent is started via custom scheme link
    if (Intent.ACTION_VIEW.equals(intent.getAction())) {

      Uri uri = intent.getData();
//      Toast.makeText(this, "uri: " + uri, Toast.LENGTH_LONG).show();
      // may be some test here with your custom uri

      List<String> pathSegments = uri.getPathSegments();
      Log.d(TAG, "uri: " + uri);
      for (String pathSegment : pathSegments) {
        Log.d(TAG, "pathSegment: " + pathSegment);
      }
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

      } else if ("statements".equals(pathSegments.get(0))) {
        Log.d(TAG, "pathSegments.get(0): " + pathSegments.get(0));
        Long statementId = Long.valueOf(pathSegments.get(1));
        Log.d(TAG, "pathSegments.get(1): " + pathSegments.get(1));

        new GetStatementForIdTask(this).execute(statementId);

      } else {

        String name = pathSegments.get(0);
        // Is it a comment?
        if (pathSegments.size() > 1) {
          String segment2 = pathSegments.get(1);
          if ("amen".equals(segment2)) {
            new GetAmenByUrlTask(this).execute(uri.toString() + ".json");
          }
        } else {

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
//      Toast.makeText(this, "Could not handle given Url: " + uri, Toast.LENGTH_LONG).show();
    }

  }

  // TODO: add support for the following urls:
  // Comment:
  // http://getamen.com/jbrennholt/amen/bi-sexual-is-the-best-plan-b-ever
  // user pages
  // http://getamen.com/anduela

  //
  // GetStatementForIdTask
  //
  private class GetStatementForIdTask extends AmenLibTask<Long, Integer, Statement> {

    public GetStatementForIdTask(Activity context) {
      super(context);
    }

    @Override
    protected Statement wrappedDoInBackground(Long... statementIds) throws IOException {
      Statement result = null;
      for (Long statementId : statementIds) {
        result = AmenoidApp.getInstance().getService().getStatementForId(statementId);
      }
      return result;
    }

    @Override
    protected void wrappedOnPostExecute(Statement result) {

      Intent startAmenDetailActivity = new Intent(UrlResolver.this, AmenDetailActivity.class);
      startAmenDetailActivity.putExtra(Constants.EXTRA_STATEMENT, result);
      startActivity(startAmenDetailActivity);
      finish();
    }
  }


  //
  // GetStatementForIdTask
  //
  private class GetAmenByUrlTask extends AmenLibTask<String, Integer, Amen> {

    public GetAmenByUrlTask(Activity context) {
      super(context);
    }

    @Override
    protected Amen wrappedDoInBackground(String... statementIds) throws IOException {
      Amen result = null;
      for (String amenUrl : statementIds) {
        result = AmenoidApp.getInstance().getService().getAmenByUrl(amenUrl);
      }
      return result;
    }

    @Override
    protected void wrappedOnPostExecute(Amen result) {

      Intent startAmenDetailActivity = new Intent(UrlResolver.this, AmenDetailActivity.class);
      startAmenDetailActivity.putExtra(Constants.EXTRA_AMEN, result);

      startActivity(startAmenDetailActivity);

      finish();
    }
  }
}