package com.jaeckel.amenoid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.model.RankedStatements;
import com.jaeckel.amenoid.api.model.Topic;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.statement.ChooseStatementTypeActivity;
import com.jaeckel.amenoid.util.AmenLibTask;

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

  private Typeface amenTypeThin;
  private Typeface amenTypeBold;

  private ListView list;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    amenTypeThin = AmenoidApp.getInstance().getAmenTypeThin();
    amenTypeBold = AmenoidApp.getInstance().getAmenTypeBold();

    handler = new Handler();
    errorDialog = new AlertDialog.Builder(this);

    Log.d(TAG, "onCreate");

    service = AmenoidApp.getInstance().getService();

    setContentView(R.layout.score_board);
    setTitle("Amenoid/Scorecard");
    progressBar = (ProgressBar) findViewById(R.id.progress_listview);

    list = (ListView) findViewById(android.R.id.list);
    View header = getLayoutInflater().inflate(R.layout.score_header, null, false);
    list.addHeaderView(header);

    Intent startingIntent = getIntent();

    currentTopic = startingIntent.getParcelableExtra(Constants.EXTRA_TOPIC);
    Long currentTopicId = startingIntent.getLongExtra(Constants.EXTRA_TOPIC_ID, -1L);
    int currentObjectKind = startingIntent.getIntExtra(Constants.EXTRA_OBJEKT_KIND, 0);

    Log.d(TAG, "currentTopic: " + currentTopic);
    if (currentTopic != null) {
      new TopicStatementsTask(this).execute(currentTopic.getId());
    } else {
      new TopicStatementsTask(this).execute(currentTopicId);

    }


    adapter = new ScoreBoardAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<RankedStatements>());

    setListAdapter(adapter);

    description = (TextView) findViewById(R.id.description_scope);
    description.setTypeface(amenTypeBold);
    if (currentTopic != null) {
      if (TextUtils.isEmpty(currentTopic.getAsSentence())) {
        description.setText("The " + (currentTopic.isBest() ? "Best " : "Worst ") + currentTopic.getDescription() + " " + currentTopic.getScope() + " is");
      } else {
        description.setText(currentTopic.getAsSentence());
      }
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

    public TopicStatementsTask(Activity context) {
      super(context);
    }

    protected Topic wrappedDoInBackground(Long... topicId) {
      try {
        return service.getTopicsForId(topicId[0], null);

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

    protected void wrappedOnPostExecute(final Topic topic) {

      if (topic != null) {
        currentTopic = topic;
        description.setText(currentTopic.getAsSentence());
        adapter = new ScoreBoardAdapter(ScoreBoardActivity.this, android.R.layout.simple_list_item_1, topic.getRankedStatements());
        setListAdapter(adapter);

      }

      progressBar.setVisibility(View.GONE);
      list.setVisibility(View.VISIBLE);

    }
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);

    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_scoreboard, menu);
    if (!AmenoidApp.getInstance().isSignedIn()) {
      MenuItem amenSth = menu.findItem(R.id.amen);
      amenSth.setEnabled(false);
    }
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