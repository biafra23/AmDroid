package com.jaeckel.amdroid;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
  private static final int[] IMAGE_IDS = {R.id.user_image};


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
    } else {
      currentStatement = currentAmen.getStatement();
    }


    final List<User> users = currentStatement.getAgreeingNetwork();
//    adapter = new UserListAdapter(this, android.R.layout.simple_list_item_1, users);
    thumbs = new ThumbnailAdapter(this, new UserListAdapter(this, android.R.layout.activity_list_item, users), AmdroidApp.getInstance().getCache(), IMAGE_IDS);
    setListAdapter(thumbs);


  }

  public void onResume() {
    super.onResume();
    statementView = (TextView) findViewById(R.id.statement);
    userView = (TextView) findViewById(R.id.user);
    amenCount = (TextView) findViewById(R.id.amen_count);
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
}