package com.jaeckel.amdroid.statement;

import android.app.ListActivity;
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
import com.jaeckel.amdroid.Constants;
import com.jaeckel.amdroid.R;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.model.Objekt;
import com.jaeckel.amdroid.api.model.Topic;
import com.jaeckel.amdroid.app.AmdroidApp;

import java.util.ArrayList;
import java.util.List;

/**
 * User: biafra
 * Date: 10/9/11
 * Time: 10:55 PM
 */
public class ChooseScopeActivity extends ListActivity {


  private static final String TAG = "ChooseScopeActivity";

  private Topic        currentTopic;
  private Objekt       currentObjekt;
  private int          currentObjektKind;
  private AmenService  service;
  private EditText     scopeEditText;
  private ScopeAdapter adapter;
  private Drawable     backgroundDrawable;
  private List<Topic>  topics;
  public  Boolean      currentTopicBest;
  public  String       currentTopicScope;
  public  String       currentTopicDescription;
  private List<String> possibleScopes;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    service = AmdroidApp.getInstance().getService();
    currentTopic = (Topic) getIntent().getParcelableExtra(Constants.EXTRA_TOPIC);
    currentObjekt = (Objekt) getIntent().getParcelableExtra(Constants.EXTRA_OBJEKT);
    currentObjektKind = getIntent().getIntExtra(Constants.EXTRA_OBJEKT_KIND, AmenService.OBJEKT_KIND_THING);

    //neccessary? isn't currentTopic enough?
    currentTopicBest = currentTopic.isBest();
    currentTopicScope = currentTopic.getScope();
    currentTopicDescription = currentTopic.getDescription();

    possibleScopes = currentObjekt.getPossibleScopes();

    setContentView(R.layout.choose_scope);

    scopeEditText = (EditText) findViewById(R.id.scope);
    scopeEditText.setText(currentTopic.getScope());
    scopeEditText.addTextChangedListener(new TextWatcher() {

      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      public void afterTextChanged(Editable editable) {
        Log.d(TAG, "afterTextChanged()");
        topics = new ArrayList<Topic>();
        if (possibleScopes != null && possibleScopes.size() > 0) {

          for (String possibleScope : possibleScopes) {
            topics.add(new Topic(currentTopicDescription, currentTopicBest, possibleScope));
          }

        } else {
          topics.add(new Topic(currentTopicDescription, currentTopicBest, "Ever"));
          topics.add(new Topic(currentTopicDescription, currentTopicBest, "So far"));
          topics.add(new Topic(currentTopicDescription, currentTopicBest, "This Year"));
          topics.add(new Topic(currentTopicDescription, currentTopicBest, "Today"));
        }
        if (!scopeInList(editable.toString(), topics)) {

          topics.add(0, new Topic(currentTopicDescription, currentTopicBest, editable.toString()));

        } else {

          Log.d(TAG, "Scope " + editable.toString() + " already in list: " + topics);
        }
        adapter = new ScopeAdapter(ChooseScopeActivity.this, R.layout.list_item_scope, topics);
        setListAdapter(adapter);
      }
    });

    List<String> topicDescriptions = currentObjekt.getPossibleDescriptions();
    topics = new ArrayList<Topic>();
    if (possibleScopes != null && possibleScopes.size() > 0) {

      for (String possibleScope : possibleScopes) {
        topics.add(new Topic(currentTopicDescription, currentTopicBest, possibleScope));
      }

    } else {

      topics.add(new Topic(currentTopicDescription, currentTopicBest, "Ever"));
      topics.add(new Topic(currentTopicDescription, currentTopicBest, "So far"));
      topics.add(new Topic(currentTopicDescription, currentTopicBest, "This Year"));
      topics.add(new Topic(currentTopicDescription, currentTopicBest, "Today"));
    }
    if (!scopeInList(currentTopic.getScope(), topics)) {
      topics.add(0, currentTopic);
    }

    adapter = new ScopeAdapter(ChooseScopeActivity.this, R.layout.list_item_scope, topics);
    setListAdapter(adapter);

    getListView().setDivider(null);
    getListView().setDividerHeight(0);

  }

  private boolean scopeInList(String scope, List<Topic> topics) {
    if (topics == null) {
      return false;
    }
    if (topics.size() == 0) {
      return false;
    }
    for (Topic o : topics) {
      if (scope.equalsIgnoreCase(o.getScope())) {
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


  class ScopeAdapter extends ArrayAdapter<Topic> {


    private LayoutInflater inflater;
    private static final String TAG        = "TopicAdapter";
    private static final int    BACKGROUND = 1;


    public ScopeAdapter(Context context, int textViewResourceId, List<Topic> topicList) {
      super(context, textViewResourceId, topicList);
      inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

      View row = convertView;
      Topic objekt = getItem(position);
      Drawable background;

      if (row == null) {
        row = inflater.inflate(R.layout.list_item_scope, parent, false);
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
      textView.setText(objekt.getScope());
      textView.setBackgroundDrawable(background);

      return row;
    }

  }
}