package com.jaeckel.amdroid;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.jaeckel.amdroid.cwac.cache.SimpleWebImageCache;
import com.jaeckel.amdroid.cwac.thumbnail.ThumbnailAdapter;
import com.jaeckel.amdroid.statement.ChooseStatementTypeActivity;
import com.jaeckel.amdroid.util.AmenLibTask;

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
  private SimpleWebImageCache cache;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "onCreate");

    service = AmdroidApp.getInstance().getService();
    cache = AmdroidApp.getInstance().getCache();

    setContentView(R.layout.details);
    setTitle("Amendetails");

    ListView list = (ListView) findViewById(android.R.id.list);
    View header = getLayoutInflater().inflate(R.layout.details_header, null, false);
    list.addHeaderView(header);


    Intent startingIntent = getIntent();
    currentAmen = startingIntent.getParcelableExtra(Constants.EXTRA_AMEN);
    if (currentAmen == null) {

      currentStatement = startingIntent.getParcelableExtra(Constants.EXTRA_STATEMENT);

      // start downloading statement again to get the first_amen_id
      new GetStatementTask(this).execute(currentStatement.getId());

    } else {
      currentStatement = currentAmen.getStatement();
    }

    header.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        startScoreBoardActivity();

      }
    });

    final List<User> users = currentStatement.getAgreeingNetwork();
//    adapter = new UserListAdapter(this, android.R.layout.simple_list_item_1, users);
//    for (User u : users) {
//      Log.d(TAG, "AgreeingNetwork: " + u);
//    }
    thumbs = new ThumbnailAdapter(this, new UserListAdapter(this, android.R.layout.activity_list_item, users), cache, IMAGE_IDS);
    setListAdapter(thumbs);

    Intent resultIntent = new Intent();
    resultIntent.putExtra(Constants.EXTRA_STATEMENT_ID, currentStatement.getId());
    setResult(AmenListActivity.REQUEST_CODE_AMEN_DETAILS, resultIntent);


  }

  private void startScoreBoardActivity() {
    Intent intent = new Intent(this, ScoreBoardActivity.class);
    intent.putExtra(Constants.EXTRA_TOPIC, currentStatement.getTopic());
    intent.putExtra(Constants.EXTRA_OBJEKT_KIND, currentStatement.getObjekt().getKindId());
    startActivity(intent);
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

        amenTakeBackButton.setEnabled(false);

        if (amened(currentStatement)) {

          Log.d(TAG, "Back taking: " + currentAmen);
          new AmenDetailActivity.TakeBackTask().execute(currentStatement.getId());

        } else {
          Log.d(TAG, "amening: " + currentAmen);
          new AmenDetailActivity.AmenTask(AmenDetailActivity.this).execute(currentAmen.getId());

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
      if (u.getName().equals(service.getMe().getName())) {
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
      amenTakeBackButton.setEnabled(false);
    }

    protected void onPostExecute(Amen result) {

      super.onPostExecute(result);
      if (result != null) {

        currentAmen = result;
        currentStatement = currentAmen.getStatement();
        populateFormWithAmen(false);
        Toast.makeText(AmenDetailActivity.this, "Taken Back.", Toast.LENGTH_SHORT).show();

        final List<User> users = currentStatement.getAgreeingNetwork();
        //    adapter = new UserListAdapter(this, android.R.layout.simple_list_item_1, users);
        thumbs = new ThumbnailAdapter(AmenDetailActivity.this, new UserListAdapter(AmenDetailActivity.this, android.R.layout.activity_list_item, users), cache, IMAGE_IDS);
        setListAdapter(thumbs);
      }
      amenTakeBackButton.setEnabled(true);

    }

  }

  //
  // AmenTask
  //
  private class AmenTask extends AmenLibTask<Long, Integer, Amen> {

    public AmenTask(Context context) {
      super(context);
    }

    protected Amen wrappedDoInBackground(Long... amenId) {
      Amen amen = service.amen(amenId[0]);
      Log.d(TAG, "Amen returned from amen(): " + amen);
      return amen;
    }

    @Override
    protected void onPreExecute() {
      amenTakeBackButton.setEnabled(false);
    }

    protected void onPostExecute(Amen result) {

      super.onPostExecute(result);
      if (result != null) {

        currentAmen = result;
        currentStatement = currentAmen.getStatement();
        populateFormWithAmen(false);
        Toast.makeText(AmenDetailActivity.this, "Amen'd.", Toast.LENGTH_SHORT).show();

        final List<User> users = currentStatement.getAgreeingNetwork();
        //    adapter = new UserListAdapter(this, android.R.layout.simple_list_item_1, users);
        thumbs = new ThumbnailAdapter(AmenDetailActivity.this, new UserListAdapter(AmenDetailActivity.this, android.R.layout.activity_list_item, users), cache, IMAGE_IDS);
        setListAdapter(thumbs);

      }
      amenTakeBackButton.setEnabled(true);
    }
  }


  //
  // AmenTask
  //
  private class GetStatementTask extends AmenLibTask<Long, Integer, Statement> {

    public GetStatementTask(Context context) {
      super(context);
    }

    protected Statement wrappedDoInBackground(Long... statementIds) {

      Statement statement = service.getStatementForId(statementIds[0]);
      Log.d(TAG, "Statement returned from statement(): " + statement);
      return statement;

    }

    @Override
    protected void onPreExecute() {
    }

    protected void onPostExecute(Statement result) {
      super.onPostExecute(result);

      if (result != null) {
        currentAmen = new Amen(result);
        currentAmen.setId(result.getFirstAmenId());
        setAmenButtonListener();
//        Toast.makeText(AmenDetailActivity.this, "setId on currentAmen", Toast.LENGTH_SHORT).show();
        currentStatement = result;
        populateFormWithAmen(false);

        // amen button freischalten

        final List<User> users = currentStatement.getAgreeingNetwork();
        //    adapter = new UserListAdapter(this, android.R.layout.simple_list_item_1, users);
        thumbs = new ThumbnailAdapter(AmenDetailActivity.this, new UserListAdapter(AmenDetailActivity.this, android.R.layout.activity_list_item, users), cache, IMAGE_IDS);
        setListAdapter(thumbs);
      }
    }
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);

    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_detail, menu);
    return true;
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);

    switch (item.getItemId()) {

      case R.id.timeline: {
        startActivity(new Intent(this, AmenListActivity.class));
        return true;
      }

      case R.id.scoreboard: {
        startScoreBoardActivity();
        return true;
      }
      case R.id.share: {
        String amenText = currentAmen.getStatement().toDisplayString();

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, amenText + " #getamen https://getamen.com/statements/" + currentAmen.getStatement().getId());
        startActivity(Intent.createChooser(sharingIntent, "Share using"));

        return true;
      }
      case R.id.amen:
        startActivity(new Intent(this, ChooseStatementTypeActivity.class));
        return true;
    }

    return false;
  }

}
