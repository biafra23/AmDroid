package com.jaeckel.amdroid;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.model.RankedStatements;
import com.jaeckel.amdroid.api.model.Topic;
import com.jaeckel.amdroid.app.AmdroidApp;

import java.util.ArrayList;

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
    
    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

    Log.d(TAG, "onCreate");

    service = AmdroidApp.getInstance().getService();

    setContentView(R.layout.user);

    ListView list = (ListView) findViewById(android.R.id.list);
    View header = getLayoutInflater().inflate(R.layout.score_header, null, false);
    list.addHeaderView(header);

    Intent startingIntent = getIntent();

    currentTopic = startingIntent.getParcelableExtra(Constants.EXTRA_TOPIC);
    
    Log.d(TAG, "currentTopic: " + currentTopic);

    new TopicStatementsTask().execute(currentTopic.getId());

    adapter = new ScoreBoardAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<RankedStatements>());

    setListAdapter(adapter);

    TextView description = (TextView) findViewById(R.id.description_scope);
    description.setText("The " + (currentTopic.isBest() ? "Best " : "Worst ") + currentTopic.getDescription() + " " + currentTopic.getScope() + " is");

  }


  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {


//    RankedStatements statement = (RankedStatements) getListAdapter().getItem(position - 1);
//
//    Log.d(TAG, "Selected Statement: " + statement);
//
//    //TODO: make AmenDetailActivity show statements as well
//    Intent intent = new Intent(this, AmenDetailActivity.class);
//    intent.putExtra(Constants.EXTRA_STATEMENT, statement.getStatement());
//    startActivity(intent);
  }

  //
  // TopicStatementsTask
  //
  private class TopicStatementsTask extends AsyncTask<Long, Integer, Topic> {

    protected Topic doInBackground(Long... topicId) {
      return service.getTopicsForId(currentTopic.getId(), null);
    }

    @Override
    protected void onPreExecute() {
    }

    protected void onPostExecute(final Topic topic) {

      adapter = new ScoreBoardAdapter(ScoreBoardActivity.this, android.R.layout.simple_list_item_1, topic.getRankedStatements());
      setListAdapter(adapter);

    }
  }


}