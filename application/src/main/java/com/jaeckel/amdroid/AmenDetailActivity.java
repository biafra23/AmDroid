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
import com.jaeckel.amdroid.api.model.Statement;
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
  private AmenService service;
  private static final String TAG = "amdroid/AmenDetailActivity";
  private Amen      currentAmen;
  private Statement statement;
  private Topic     topicWithRankedStatements;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.v(TAG, "onCreate");
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    String username = prefs.getString("user_name", "");
    String password = prefs.getString("password", "");
    service = AmdroidApp.getInstance().getService(username, password);

    setContentView(R.layout.details);
    Long id = getIntent().getLongExtra(STATEMENT_ID, -1);
    statement = service.getStatementForId(id);
    topicWithRankedStatements = service.getTopicsForId(statement.getTopic().getId());

    TextView statementView = (TextView) findViewById(R.id.statement);
    statementView.setText(statement.toDisplayString());

    TextView userView = (TextView) findViewById(R.id.user);
    userView.setText(statement.getFirstPoster().getName() + ", " + format(statement.getFirstPostedAt()) );
    TextView amenCount = (TextView) findViewById(R.id.amen_count);
    amenCount.setText(statement.getTotalAmenCount() + " Amen");

    TextView agreeingNetwork = (TextView) findViewById(R.id.agreeing_network);
    StringBuilder agreeing = new StringBuilder();
    for (User user : statement.getAgreeingNetwork()) {

      agreeing.append( user.getName() + ", ") ;
    }

    agreeingNetwork.setText(agreeing.toString().replace(", $", ""));
    Button amenTakeBackButton = (Button) findViewById(R.id.amen_take_back);
    amenTakeBackButton.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        //To change body of implemented methods use File | Settings | File Templates.
        Toast.makeText(AmenDetailActivity.this, "Amening...", Toast.LENGTH_SHORT).show();

        service.amen(new Statement(statement.getId()));

      }
    });
    Button hellNoButton = (Button) findViewById(R.id.hell_no);
    hellNoButton.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        //To change body of implemented methods use File | Settings | File Templates.
      }
    });


  }

  private String format(Date firstPostedAt) {
    SimpleDateFormat fmt = new SimpleDateFormat("dd, MMMMM yyyy");

    return fmt.format(firstPostedAt);
  }

  public void onResume() {
    super.onResume();
  }

  public void onPause() {
    super.onPause();
  }
}