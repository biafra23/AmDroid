package com.jaeckel.amdroid;


import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.model.Amen;
import com.jaeckel.amdroid.api.model.Statement;
import com.jaeckel.amdroid.api.model.Topic;
import com.jaeckel.amdroid.api.model.User;
import com.jaeckel.amdroid.app.AmdroidApp;
import com.jaeckel.amdroid.cwac.thumbnail.ThumbnailAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * User: biafra
 * Date: 9/25/11
 * Time: 10:27 AM
 */
public class AmenDetailActivity extends ListActivity {

  private static final String TAG = "amdroid/AmenDetailActivity";
  private Amen             currentAmen;
  private Statement        currentStatement;
  private Topic            topicWithRankedStatements;
  private TextView         statementView;
  private TextView         userView;
  private TextView         amenCount;
  private Button           amenTakeBackButton;
  private Button           hellNoButton;
  private UserListAdapter  adapter;
  private ThumbnailAdapter thumbs;
  private AmenService      service;
  private static final int[]  IMAGE_IDS = {R.id.user_image};
  private              String lastError = null;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "onCreate");

    service = AmdroidApp.getInstance().getService();

    setContentView(R.layout.details);

    ListView list = (ListView) findViewById(android.R.id.list);
    View header = getLayoutInflater().inflate(R.layout.details_header, null, false);
    list.addHeaderView(header);


    Intent startingIntent = getIntent();
    currentAmen = startingIntent.getParcelableExtra(Constants.EXTRA_AMEN);
    if (currentAmen == null) {

      currentStatement = startingIntent.getParcelableExtra(Constants.EXTRA_STATEMENT);

      // start downloading statement again to get the first_amen_id
      new GetStatementTask().execute(currentStatement.getId());

    } else {
      currentStatement = currentAmen.getStatement();
    }

    header.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Intent intent = new Intent(AmenDetailActivity.this, ScoreBoardActivity.class);
        intent.putExtra(Constants.EXTRA_TOPIC, currentStatement.getTopic());
        intent.putExtra(Constants.EXTRA_OBJEKT_KIND, currentStatement.getObjekt().getKindId());
        startActivity(intent);

      }
    });

    final List<User> users = currentStatement.getAgreeingNetwork();
//    adapter = new UserListAdapter(this, android.R.layout.simple_list_item_1, users);
    thumbs = new ThumbnailAdapter(this, new UserListAdapter(this, android.R.layout.activity_list_item, users), AmdroidApp.getInstance().getCache(), IMAGE_IDS);
    setListAdapter(thumbs);

    Intent resultIntent = new Intent();
    resultIntent.putExtra(Constants.EXTRA_STATEMENT_ID, currentStatement.getId());
    setResult(AmenListActivity.REQUEST_CODE_AMEN_DETAILS, resultIntent);


  }

  public void onResume() {
    super.onResume();
    statementView = (TextView) findViewById(R.id.statement);
    userView = (TextView) findViewById(R.id.user);
    amenCount = (TextView) findViewById(R.id.amen_count);
    amenTakeBackButton = (Button) findViewById(R.id.amen_take_back);
    amenTakeBackButton.setEnabled(false);
    hellNoButton = (Button) findViewById(R.id.hell_no);
    hellNoButton.setEnabled(false);
    populateFormWithAmen(true);


  }


  private void populateFormWithAmen(boolean updateName) {

    if (currentAmen == null) {
      Log.d(TAG, "currentAmen: " + currentAmen);
      Log.d(TAG, "currentStatement: " + currentStatement);
      statementView.setText(AmenListAdapter.styleAmenWithColor(currentStatement, false, null, this));
    } else {
      statementView.setText(AmenListAdapter.styleAmenWithColor(currentAmen, this));
    }


//    statementView.setText(currentAmen.getStatement().toDisplayString());
    //TODO: find a better way to have the original? name here
    if (updateName) {
      if (currentAmen != null && currentAmen.getUser() != null) {
        userView.setText(currentAmen.getUser().getName() + ", " + format(currentAmen.getCreatedAt()));
      } else {
        userView.setText(currentStatement.getFirstPoster().getName() + ", " + format(currentStatement.getFirstPostedAt()));
      }

    }

    amenCount.setText(currentStatement.getTotalAmenCount() + " Amen");
    StringBuilder agreeing = new StringBuilder();
    for (User user : currentStatement.getAgreeingNetwork()) {
      agreeing.append(user.getName() + ", ");
    }
    if (amened(currentStatement)) {
      amenTakeBackButton.setText("Take Back");
    } else {
      amenTakeBackButton.setText("Amen!");
    }
    if (currentAmen != null && currentAmen.getId() != null) {

      setAmenButtonListener();
    }


  }

  private void setAmenButtonListener() {

    amenTakeBackButton.setEnabled(true);
    amenTakeBackButton.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        //To change body of implemented methods use File | Settings | File Templates.
//        Toast.makeText(AmenDetailActivity.this, "Amening...", Toast.LENGTH_SHORT).show();

        if (amened(currentStatement)) {

          Log.d(TAG, "Back taking: " + currentAmen);
          new AmenDetailActivity.TakeBackTask().execute(currentStatement.getId());

        } else {
          Log.d(TAG, "amening: " + currentAmen);
          new AmenDetailActivity.AmenTask().execute(currentAmen.getId());

        }
        populateFormWithAmen(false);

      }
    });

    hellNoButton.setEnabled(true);
    hellNoButton.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        //TODO: show hellno form here to let user select different objekt


        populateFormWithAmen(false);

        Intent intent = new Intent(AmenDetailActivity.this, DisputeActivity.class);
        intent.putExtra(Constants.EXTRA_AMEN, currentAmen);
        startActivity(intent);
      }
    });
  }

  private boolean amened(Statement currentStatement) {
    for (User u : currentStatement.getAgreeingNetwork()) {
      if (u.getName().equals(AmdroidApp.getInstance().getService().getMe().getName())) {
        return true;
      }
    }
    return false;
  }

  public static String format(Date firstPostedAt) {
    SimpleDateFormat fmt = new SimpleDateFormat("dd, MMMMM yyyy - HH:mm");

    return fmt.format(firstPostedAt);
  }

  public void onPause() {
    super.onPause();
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {

    User user = (User) getListAdapter().getItem(position - 1);

    Log.d(TAG, "======> Selected User: " + user);
    Intent intent = new Intent(this, UserDetailActivity.class);
    intent.putExtra(Constants.EXTRA_USER, user);
    startActivity(intent);
  }


  //
  // TakeBackTask
  //
  private class TakeBackTask extends AsyncTask<Long, Integer, Amen> {

    protected Amen doInBackground(Long... statementId) {
      try {
        service.takeBack(statementId[0]);
        Amen amen = new Amen(service.getStatementForId(statementId[0]));

        amen.setId(currentAmen.getId());
        Log.d(TAG, "Amen returned from amen(): " + amen);
        return amen;
      } catch (RuntimeException e) {
        lastError = e.getMessage();
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPreExecute() {
    }

    protected void onPostExecute(Amen result) {

      if (lastError != null) {
        Toast.makeText(AmenDetailActivity.this, lastError, Toast.LENGTH_SHORT).show();
        lastError = null;
      } else {
        currentAmen = result;
        currentStatement = currentAmen.getStatement();
        populateFormWithAmen(false);
        Toast.makeText(AmenDetailActivity.this, "Taken Back.", Toast.LENGTH_SHORT).show();

        final List<User> users = currentStatement.getAgreeingNetwork();
        //    adapter = new UserListAdapter(this, android.R.layout.simple_list_item_1, users);
        thumbs = new ThumbnailAdapter(AmenDetailActivity.this, new UserListAdapter(AmenDetailActivity.this, android.R.layout.activity_list_item, users), AmdroidApp.getInstance().getCache(), IMAGE_IDS);
        setListAdapter(thumbs);
      }
    }
  }

  //
  // AmenTask
  //
  private class AmenTask extends AsyncTask<Long, Integer, Amen> {

    protected Amen doInBackground(Long... amenId) {
      try {
        lastError = null;
        Amen amen = service.amen(amenId[0]);
        Log.d(TAG, "Amen returned from amen(): " + amen);
        return amen;
      } catch (RuntimeException e) {
        lastError = e.getMessage();
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPreExecute() {
    }

    protected void onPostExecute(Amen result) {

      if (lastError != null) {
        Toast.makeText(AmenDetailActivity.this, lastError, Toast.LENGTH_SHORT).show();
        lastError = null;
      } else {
        currentAmen = result;
        currentStatement = currentAmen.getStatement();
        populateFormWithAmen(false);
        Toast.makeText(AmenDetailActivity.this, "Amen'd.", Toast.LENGTH_SHORT).show();

        final List<User> users = currentStatement.getAgreeingNetwork();
        //    adapter = new UserListAdapter(this, android.R.layout.simple_list_item_1, users);
        thumbs = new ThumbnailAdapter(AmenDetailActivity.this, new UserListAdapter(AmenDetailActivity.this, android.R.layout.activity_list_item, users), AmdroidApp.getInstance().getCache(), IMAGE_IDS);
        setListAdapter(thumbs);
      }
    }
  }


  //
  // AmenTask
  //
  private class GetStatementTask extends AsyncTask<Long, Integer, Statement> {

    protected Statement doInBackground(Long... statementIds) {
      try {
        lastError = null;
        Statement statement = service.getStatementForId(statementIds[0]);
        Log.d(TAG, "Statement returned from statement(): " + statement);
        return statement;
      } catch (RuntimeException e) {
        lastError = e.getMessage();
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPreExecute() {
    }

    protected void onPostExecute(Statement result) {

      if (lastError != null) {
        Toast.makeText(AmenDetailActivity.this, lastError, Toast.LENGTH_SHORT).show();
        lastError = null;
      } else {
        currentAmen = new Amen(result);
        currentAmen.setId(result.getFirstAmenId());
        setAmenButtonListener();
//        Toast.makeText(AmenDetailActivity.this, "setId on currentAmen", Toast.LENGTH_SHORT).show();
        currentStatement = result;
        populateFormWithAmen(false);

        // amen button freischalten

        final List<User> users = currentStatement.getAgreeingNetwork();
        //    adapter = new UserListAdapter(this, android.R.layout.simple_list_item_1, users);
        thumbs = new ThumbnailAdapter(AmenDetailActivity.this, new UserListAdapter(AmenDetailActivity.this, android.R.layout.activity_list_item, users), AmdroidApp.getInstance().getCache(), IMAGE_IDS);
        setListAdapter(thumbs);
      }
    }
  }
}
