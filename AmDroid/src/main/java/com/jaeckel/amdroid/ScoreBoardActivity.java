package com.jaeckel.amdroid;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.model.RankedStatements;
import com.jaeckel.amdroid.api.model.Topic;
import com.jaeckel.amdroid.app.AmdroidApp;

/**
 * User: biafra
 * Date: 10/15/11
 * Time: 5:44 PM
 */
public class ScoreBoardActivity extends ListActivity {


  private static String TAG = "amdroid/ScoreBoardActivity";

  private AmenService       service;
  private ScoreBoardAdapter adapter;
  private Topic             currentTopic;


  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    Log.d(TAG, "onCreate");

    service = AmdroidApp.getInstance().getService();

    setContentView(R.layout.user);

    ListView list = (ListView) findViewById(android.R.id.list);
    View header = getLayoutInflater().inflate(R.layout.score_header, null, false);
    list.addHeaderView(header);

    Intent startingIntent = getIntent();

    currentTopic = startingIntent.getParcelableExtra(Constants.EXTRA_TOPIC);

    // update neccessary?
    currentTopic = service.getTopicsForId(currentTopic.getId(), null);

    adapter = new ScoreBoardAdapter(this, android.R.layout.simple_list_item_1, currentTopic.getRankedStatements());

    setListAdapter(adapter);

    TextView description = (TextView) findViewById(R.id.description_scope);
    description.setText("The " + (currentTopic.isBest() ? "Best " : "Worst ") + currentTopic.getDescription() + " is");

  }


  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {

    RankedStatements statement = (RankedStatements) getListAdapter().getItem(position - 1);

    Log.d(TAG, "Selected Statement: " + statement);

    //TODO: make AmenDetailActivity show statements as well
    Intent intent = new Intent(this, AmenDetailActivity.class);
    intent.putExtra(Constants.EXTRA_STATEMENT, statement.getStatement());
    startActivity(intent);
  }
}