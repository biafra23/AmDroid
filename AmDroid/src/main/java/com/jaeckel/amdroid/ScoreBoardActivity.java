package com.jaeckel.amdroid;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.model.RankedStatements;
import com.jaeckel.amdroid.api.model.Topic;
import com.jaeckel.amdroid.app.AmdroidApp;
import com.jaeckel.amdroid.statement.ChooseStatementTypeActivity;

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
    int currentObjectKind = startingIntent.getIntExtra(Constants.EXTRA_OBJEKT_KIND, 0);

    Log.d(TAG, "currentTopic: " + currentTopic);

    new TopicStatementsTask().execute(currentTopic.getId());

    adapter = new ScoreBoardAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<RankedStatements>());

    setListAdapter(adapter);

    TextView description = (TextView) findViewById(R.id.description_scope);
    description.setText(currentTopic.getAsSentence());

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

//      if (topic.getRankedStatements() != null
//          && topic.getRankedStatements().size() > 0
//          && topic.getRankedStatements().get(0).getStatement().getObjekt().getKindId() == AmenService.OBJEKT_KIND_PLACE
//        ) {
//
//        TextView description = (TextView) findViewById(R.id.description_scope);
//        description.setText("The " + (currentTopic.isBest() ? "Best " : "Worst ") + " Place for " + currentTopic.getDescription() + " " + currentTopic.getScope() + " is");
//      }

      adapter = new ScoreBoardAdapter(ScoreBoardActivity.this, android.R.layout.simple_list_item_1, topic.getRankedStatements());
      setListAdapter(adapter);

    }
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);

    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_scoreboard, menu);
    return true;
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);

    switch (item.getItemId()) {

      case R.id.timeline:
        startActivity(new Intent(this, AmenListActivity.class));
        return true;

      case R.id.share:

        String amenText = currentTopic.getAsSentence();

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, amenText + "... #getamen https://getamen.com/topics/" + currentTopic.getId());
        startActivity(Intent.createChooser(sharingIntent, "Share using"));

        return true;

      case R.id.amen:
        startActivity(new Intent(this, ChooseStatementTypeActivity.class));
        return true;
    }

    return false;
  }
}