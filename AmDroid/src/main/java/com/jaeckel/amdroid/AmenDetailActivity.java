package com.jaeckel.amdroid;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

    Log.d(TAG, "onCreate");

    service = AmdroidApp.getInstance().getService();

    setContentView(R.layout.details);
    
    Intent startingIntent = getIntent();
    currentAmen = startingIntent.getParcelableExtra(Constants.EXTRA_AMEN);

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

    statementView.setText(AmenListAdapter.styleAmenWithColor(currentAmen, this));

//    statementView.setText(currentAmen.getStatement().toDisplayString());
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
    } else {
      amenTakeBackButton.setText("Amen!");
    }
    amenTakeBackButton.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        //To change body of implemented methods use File | Settings | File Templates.
//        Toast.makeText(AmenDetailActivity.this, "Amening...", Toast.LENGTH_SHORT).show();

        if (amened(currentAmen)) {
          service.takeBack(currentAmen.getStatement().getId());
          currentAmen = new Amen(service.getStatementForId(currentAmen.getStatement().getId()));
          Log.d(TAG, "currentAmen: " + currentAmen);
        } else {
          currentAmen = service.amen(currentAmen.getId());
        }
        populateFormWithAmen(false);

      }
    });

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

  private boolean amened(Amen currentAmen) {
    for (User u : currentAmen.getStatement().getAgreeingNetwork()) {
      if (u.getName().equals(AmdroidApp.getInstance().getService().getMe().getName())) {
        return true;
      }
    }
    return false;
  }

  private String format(Date firstPostedAt) {
    SimpleDateFormat fmt = new SimpleDateFormat("dd, MMMMM yyyy - HH:mm");

    return fmt.format(firstPostedAt);
  }

  public void onPause() {
    super.onPause();
  }
}