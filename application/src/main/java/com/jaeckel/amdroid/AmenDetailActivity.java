package com.jaeckel.amdroid;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.model.Amen;
import com.jaeckel.amdroid.api.model.Topic;
import com.jaeckel.amdroid.api.model.User;
import com.jaeckel.amdroid.app.AmdroidApp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: biafra
 * Date: 9/25/11
 * Time: 10:27 AM
 */
public class AmenDetailActivity extends Activity {
  public static final String STATEMENT_ID = "statement_id";
  public static final String AMEN_ID      = "amen_id";
  private AmenService service;
  private static final String TAG = "amdroid/AmenDetailActivity";
  private Amen     currentAmen;
  private Topic    topicWithRankedStatements;
  private TextView statementView;
  private TextView userView;
  private TextView amenCount;
  private TextView agreeingNetwork;
  private Button   amenTakeBackButton;
  private Button   hellNoButton;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.v(TAG, "onCreate");
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    String username = prefs.getString("user_name", "nbotvin@different.name");
    String password = prefs.getString("password", "foobar23");
    service = AmdroidApp.getInstance().getService(username, password);

    setContentView(R.layout.details);
    Long id = getIntent().getLongExtra(AMEN_ID, -1);
    currentAmen = service.getAmenForId(id);

  }

  public void onResume() {
    super.onResume();
    statementView = (TextView) findViewById(R.id.statement);
    userView = (TextView) findViewById(R.id.user);
    amenCount = (TextView) findViewById(R.id.amen_count);
    agreeingNetwork = (TextView) findViewById(R.id.agreeing_network);
    amenTakeBackButton = (Button) findViewById(R.id.amen_take_back);
    hellNoButton = (Button) findViewById(R.id.hell_no);

    populateFormWithAmen(true);


  }

  private void populateFormWithAmen(boolean updateName) {

    statementView.setText(currentAmen.getStatement().toDisplayString());
    //TODO: find a better way to have the original? name here
    if (updateName) {
      userView.setText(currentAmen.getUser().getName() + ", " + format(currentAmen.getCreatedAt()));
    }

    amenCount.setText(currentAmen.getStatement().getTotalAmenCount() + " Amen");
    StringBuilder agreeing = new StringBuilder();
    for (User user : currentAmen.getStatement().getAgreeingNetwork()) {
      agreeing.append(user.getName() + ", ");
    }
    agreeingNetwork.setText(agreeing.toString().replace(", $", ""));
    if (amened(currentAmen)) {
      amenTakeBackButton.setText("Take Back");
    }
    amenTakeBackButton.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        //To change body of implemented methods use File | Settings | File Templates.
        Toast.makeText(AmenDetailActivity.this, "Amening...", Toast.LENGTH_SHORT).show();

        currentAmen = service.amen(currentAmen.getId());
        populateFormWithAmen(false);

      }
    });

    hellNoButton.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        //TODO: show hellno form here to let user slect different objekt

        populateFormWithAmen(false);
      }
    });
  }

  private boolean amened(Amen currentAmen) {
    for (User u :currentAmen.getStatement().getAgreeingNetwork()) {
      if (u.getName().equals(AmdroidApp.getInstance().getMe().getName())) {
        return true;
      }
    }
    return false;
  }

  private String format(Date firstPostedAt) {
    SimpleDateFormat fmt = new SimpleDateFormat("dd, MMMMM yyyy");

    return fmt.format(firstPostedAt);
  }

  public void onPause() {
    super.onPause();
  }
}