package com.jaeckel.amenoid.statement;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockListActivity;
import com.jaeckel.amenoid.Constants;
import com.jaeckel.amenoid.R;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.model.Objekt;
import com.jaeckel.amenoid.api.model.Topic;
import com.jaeckel.amenoid.app.AmenoidApp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * User: biafra
 * Date: 10/9/11
 * Time: 10:55 PM
 */
public class ChooseTopicActivity extends SherlockListActivity {

  private static final String TAG = "ChooseTopicActivity";

  private Topic        currentTopic;
  private Objekt       currentObjekt;
  private int          currentObjektKind;
  private AmenService  service;
  private EditText     objektEditText;
  private TopicAdapter adapter;
  private Drawable     backgroundDrawable;
  private List<Topic>  topics;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    service = AmenoidApp.getInstance().getService();
    currentTopic = (Topic) getIntent().getParcelableExtra(Constants.EXTRA_TOPIC);
    currentObjekt = (Objekt) getIntent().getParcelableExtra(Constants.EXTRA_OBJEKT);
    currentObjektKind = getIntent().getIntExtra(Constants.EXTRA_OBJEKT_KIND, AmenService.OBJEKT_KIND_THING);

    setContentView(R.layout.choose_objekt);
    setTitle("Choose Topic");
    objektEditText = (EditText) findViewById(R.id.objekt);
    objektEditText.setHint(currentTopic.getDescription());
    objektEditText.addTextChangedListener(new TextWatcher() {

      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      public void afterTextChanged(Editable editable) {
        List<String> topicDescriptions = currentObjekt.getPossibleDescriptions();
        topics = new ArrayList<Topic>();
        if (topicDescriptions != null && topicDescriptions.size() > 0) {
          for (String description : topicDescriptions) {
            topics.add(new Topic(description, currentTopic.isBest(), currentTopic.getScope()));
          }
        }
        if (!topicInList(editable.toString(), topics)) {
          topics.add(0, new Topic(editable.toString(), currentTopic.isBest(), currentTopic.getScope()));
        }

        adapter = new TopicAdapter(ChooseTopicActivity.this, R.layout.list_item_objekt, topics);
        setListAdapter(adapter);
      }
    });

    topics = new ArrayList<Topic>();
    Boolean currentTopicBest = true;
    String currentTopicScope = "Ever";
    if (currentTopic != null) {
      currentTopicBest = currentTopic.isBest();
      currentTopicScope = currentTopic.getScope();
    }

    switch (currentObjektKind) {
      case AmenService.OBJEKT_KIND_PERSON: {
        backgroundDrawable = getResources().getDrawable(R.drawable.rounded_edges_green);
        topics.add(new Topic("Dude", currentTopicBest, currentTopicScope));
        topics.add(new Topic("Dudette", currentTopicBest, currentTopicScope));
        topics.add(new Topic("Lady", currentTopicBest, currentTopicScope));
        topics.add(new Topic("Gentleman", currentTopicBest, currentTopicScope));
        topics.add(new Topic("Person To Know", currentTopicBest, currentTopicScope));
        topics.add(new Topic("Person To Work With", currentTopicBest, currentTopicScope));
        topics.add(new Topic("Person You've Never Heard Of But Should Have", currentTopicBest, currentTopicScope));

        break;
      }
      case AmenService.OBJEKT_KIND_PLACE: {
        backgroundDrawable = getResources().getDrawable(R.drawable.rounded_edges_blue);
        topics.add(new Topic("Having Fun", currentTopicBest, currentTopicScope));
        topics.add(new Topic("Making Magic", currentTopicBest, currentTopicScope));
        topics.add(new Topic("Hiding from the Cops", currentTopicBest, currentTopicScope));
        break;
      }
      case AmenService.OBJEKT_KIND_THING: {
        backgroundDrawable = getResources().getDrawable(R.drawable.rounded_edges_orange);

        topics.add(new Topic("Thing", currentTopicBest, currentTopicScope));
        topics.add(new Topic("Idea", currentTopicBest, currentTopicScope));
        topics.add(new Topic("Experience", currentTopicBest, currentTopicScope));
        topics.add(new Topic("Saying", currentTopicBest, currentTopicScope));
        topics.add(new Topic("Waste of Time", currentTopicBest, currentTopicScope));
        topics.add(new Topic("Ego Booster", currentTopicBest, currentTopicScope));
        break;
      }
    }

    List<String> topicDescriptions = currentObjekt.getPossibleDescriptions();
    topics = new ArrayList<Topic>();
    if (topicDescriptions != null && topicDescriptions.size() > 0) {
      for (String description : topicDescriptions) {
        topics.add(new Topic(description, currentTopicBest, currentTopicScope));
      }
    }
    if (!topicInList(currentTopic.getDescription(), topics)) {
      topics.add(0, currentTopic);
    }

    adapter = new TopicAdapter(this, R.layout.list_item_objekt, topics);
    setListAdapter(adapter);

    getListView().setDivider(null);
    getListView().setDividerHeight(0);

  }

  private boolean topicInList(String description, List<Topic> topics) {
    if (topics == null) {
      return false;
    }
    if (topics.size() == 0) {
      return false;
    }
    for (Topic o : topics) {
      if (description.equalsIgnoreCase(o.getDescription())) {
        return true;
      }
    }
    return false;
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {

    Topic topic = (Topic) getListAdapter().getItem(position);

    Log.d(TAG, "onListItemClick | Selected Topic: " + topic);

    Intent intent = new Intent();
    intent.putExtra(Constants.EXTRA_TOPIC, topic);
    setResult(RESULT_OK, intent);
    finish();
  }


  class TopicAdapter extends ArrayAdapter<Topic> {


    private LayoutInflater inflater;
    private static final String TAG        = "TopicAdapter";
    private static final int    BACKGROUND = 1;


    public TopicAdapter(Context context, int textViewResourceId, List<Topic> topicList) {
      super(context, textViewResourceId, topicList);
      inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

      View row = convertView;
      Topic objekt = getItem(position);
      Drawable background;

      if (row == null) {
        row = inflater.inflate(R.layout.list_item_topic, parent, false);
        background = getResources().getDrawable(R.drawable.rounded_edges);
        switch (currentObjektKind) {
          case AmenService.OBJEKT_KIND_PERSON: {
            background.setColorFilter(Color.parseColor("#C0D000"), PorterDuff.Mode.SRC_OVER);
            break;
          }
          case AmenService.OBJEKT_KIND_PLACE: {
            background.setColorFilter(Color.parseColor("#0080D0"), PorterDuff.Mode.SRC_OVER);
            break;
          }
          case AmenService.OBJEKT_KIND_THING: {
            background.setColorFilter(Color.parseColor("#D08000"), PorterDuff.Mode.SRC_OVER);
            break;
          }
          default: {
            break;
          }
        }
        row.setTag(R.drawable.rounded_edges, background);
      }
      background = (Drawable) row.getTag(R.drawable.rounded_edges);

      TextView textView = (TextView) row.findViewById(R.id.completion_item_name);
      textView.setText(objekt.getDescription());
      textView.setBackgroundDrawable(background);

      return row;
    }

  }
}