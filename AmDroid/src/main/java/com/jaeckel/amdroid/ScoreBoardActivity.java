package com.jaeckel.amdroid;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.model.RankedStatements;
import com.jaeckel.amdroid.api.model.Topic;
import com.jaeckel.amdroid.app.AmdroidApp;
import com.jaeckel.amdroid.statement.ChooseStatementTypeActivity;
import com.jaeckel.amdroid.util.AmenLibTask;

import java.io.IOException;
import java.util.ArrayList;

/**
 * User: biafra
 * Date: 10/15/11
 * Time: 5:44 PM
 */
public class ScoreBoardActivity extends ListActivity {


  private static String TAG = "ScoreBoardActivity";

  private AmenService       service;
  private ScoreBoardAdapter adapter;
  private Topic             currentTopic;
  private TextView          description;

  private AlertDialog.Builder errorDialog;
  private Handler             handler;

  private ProgressBar progressBar;

  private ListView list;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    handler = new Handler();
    errorDialog = new AlertDialog.Builder(this);

    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    setProgressBarIndeterminateVisibility(true);

    Log.d(TAG, "onCreate");

    service = AmdroidApp.getInstance().getService();

    setContentView(R.layout.score_board);
    setTitle("Amenoid/Scorecard");
    progressBar = (ProgressBar) findViewById(R.id.progress_listview);
    
    list = (ListView) findViewById(android.R.id.list);
    View header = getLayoutInflater().inflate(R.layout.score_header, null, false);
    list.addHeaderView(header);

    Intent startingIntent = getIntent();

    currentTopic = startingIntent.getParcelableExtra(Constants.EXTRA_TOPIC);
    int currentObjectKind = startingIntent.getIntExtra(Constants.EXTRA_OBJEKT_KIND, 0);

    Log.d(TAG, "currentTopic: " + currentTopic);

    new TopicStatementsTask(this).execute(currentTopic.getId());

    adapter = new ScoreBoardAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<RankedStatements>());

    setListAdapter(adapter);

    description = (TextView) findViewById(R.id.description_scope);
    if (TextUtils.isEmpty(currentTopic.getAsSentence())) {
      description.setText("The " + (currentTopic.isBest() ? "Best " : "Worst ") + currentTopic.getDescription() + " " + currentTopic.getScope() + " is");
    } else {
      description.setText(currentTopic.getAsSentence());
    }
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
  private class TopicStatementsTask extends AmenLibTask<Long, Integer, Topic> {

    public TopicStatementsTask(Context context) {
      super(context);
    }

    protected Topic wrappedDoInBackground(Long... topicId) {
      try {
        return service.getTopicsForId(currentTopic.getId(), null);

      } catch (final IOException e) {

        handler.post(new Runnable() {
          public void run() {

            Log.e(TAG, "Creating dialog");
            errorDialog
              .setTitle(R.string.exception)
              .setMessage(e.getMessage())
              .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                  Log.d(TAG, "OK clicked");
                }
              })
              .show();
            Log.e(TAG, "Created dialog");
          }
        });

        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
      return null;
    }

    @Override
    protected void onPreExecute() {
    }

    protected void onPostExecute(final Topic topic) {
      super.onPostExecute(topic);

      if (topic != null) {
        currentTopic = topic;
        description.setText(currentTopic.getAsSentence());
        adapter = new ScoreBoardAdapter(ScoreBoardActivity.this, android.R.layout.simple_list_item_1, topic.getRankedStatements());
        setListAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
      }

      setProgressBarIndeterminateVisibility(false);

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