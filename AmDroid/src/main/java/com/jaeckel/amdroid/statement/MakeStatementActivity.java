package com.jaeckel.amdroid.statement;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.jaeckel.amdroid.Constants;
import com.jaeckel.amdroid.EditPreferencesActivity;
import com.jaeckel.amdroid.R;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.model.Objekt;
import com.jaeckel.amdroid.api.model.Statement;
import com.jaeckel.amdroid.api.model.Topic;
import com.jaeckel.amdroid.app.AmdroidApp;

/**
 * User: biafra
 * Date: 9/25/11
 * Time: 9:40 PM
 */
public class MakeStatementActivity extends Activity {

  private AmenService service;

  private int objektKind;

  TextView bestView;
  TextView bestPlaceView;
  TextView objektView;
  TextView scopeView;
  TextView topicPlaceScopeView;
  TextView topicView;

  Objekt  currentObjekt;
  Topic   currentTopic;
  String  currentTopicScope;
  Boolean currentBest;

  private Drawable backgroundDrawable;

  public static final int REQUEST_CODE_OBJEKT = 10101;
  public static final int REQUEST_CODE_TOPIC  = 10102;
  public static final int REQUEST_CODE_SCOPE  = 10103;

  private static final String TAG = "MakeStatementActivity";

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.v(TAG, "onCreate");

    currentBest = true;
    currentObjekt = new Objekt();
    currentTopic = new Topic();


    objektKind = getIntent().getIntExtra(Constants.EXTRA_OBJEKT_KIND, AmenService.OBJEKT_KIND_THING);

    if (objektKind == AmenService.OBJEKT_KIND_PERSON) {
      backgroundDrawable = getResources().getDrawable(R.drawable.rounded_edges_green);
      currentObjekt.setName("Karl Marx");
      currentTopic.setBest(true);
      currentTopic.setDescription("Author");
      currentTopic.setScope("Ever");
    }
    if (objektKind == AmenService.OBJEKT_KIND_THING) {
      backgroundDrawable = getResources().getDrawable(R.drawable.rounded_edges_orange);
      currentObjekt.setName("Android");
      currentTopic.setBest(true);
      currentTopic.setDescription("Operating System");
      currentTopic.setScope("So far");
    }
    if (objektKind == AmenService.OBJEKT_KIND_PLACE) {
      backgroundDrawable = getResources().getDrawable(R.drawable.rounded_edges_blue);
      currentObjekt.setName("Drachenspielplatz");
      currentTopic.setBest(true);
      currentTopic.setDescription("Playground");
      currentTopic.setScope("in Berlin");

    }

    currentTopicScope = currentTopic.getScope();
    currentBest = currentTopic.isBest();

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    String username = prefs.getString("user_name", null);
    String password = prefs.getString("password", null);

    if (username == null || password == null) {
      startActivity(new Intent(this, EditPreferencesActivity.class));
    }
    username = prefs.getString("user_name", null);
    password = prefs.getString("password", null);

    service = AmdroidApp.getInstance().getService(username, password);

    setContentView(R.layout.make_statement);


  }


  @Override
  public void onResume() {
    super.onResume();
    Log.v(TAG, "onResume");
    objektView = (TextView) findViewById(R.id.objekt_name);
    objektView.setBackgroundDrawable(backgroundDrawable);
    objektView.setText(currentObjekt.getName());

    objektView.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Intent intent = new Intent(MakeStatementActivity.this, ChooseObjektActivity.class);
        intent.putExtra(Constants.EXTRA_OBJEKT, currentObjekt);
        intent.putExtra(Constants.EXTRA_OBJEKT_KIND, objektKind);
        startActivityForResult(intent, REQUEST_CODE_OBJEKT);
      }
    });

    bestPlaceView = (TextView) findViewById(R.id.best_place);
    if (objektKind != AmenService.OBJEKT_KIND_PLACE) {
      bestPlaceView.setText("");
    }

    bestView = (TextView) findViewById(R.id.best);
    bestView.setBackgroundDrawable(backgroundDrawable);
    if (currentBest) {
      bestView.setText("the Best");
    } else {
      bestView.setText("the Worst");
    }

    bestView.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        if (bestView.getText().equals("the Best")) {
          bestView.setText("the Worst");
          currentBest = false;
        } else {
          bestView.setText("the Best");
          currentBest = false;
        }
      }
    });

    topicView = (TextView) findViewById(R.id.topic_name);
    topicView.setBackgroundDrawable(backgroundDrawable);
    topicView.setText(currentTopic.getDescription());
    topicView.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Intent intent = new Intent(MakeStatementActivity.this, ChooseTopicActivity.class);
        intent.putExtra(Constants.EXTRA_OBJEKT, currentObjekt);
        intent.putExtra(Constants.EXTRA_TOPIC, currentTopic);
        intent.putExtra(Constants.EXTRA_OBJEKT_KIND, objektKind);
        startActivityForResult(intent, REQUEST_CODE_TOPIC);
      }
    });
    //    topicPlaceScopeView = (TextView) findViewById(R.id.topic_place_sco           pe);
    //    if (objektKind != AmenService.OBJEKT_KIND_PLACE) {
    //      topicPlaceScopeView.setText("");
    //    }
    scopeView = (TextView) findViewById(R.id.topic_scope);
    scopeView.setBackgroundDrawable(backgroundDrawable);
    scopeView.setText(currentTopic.getScope());
    scopeView.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Intent intent = new Intent(MakeStatementActivity.this, ChooseScopeActivity.class);
        intent.putExtra(Constants.EXTRA_OBJEKT, currentObjekt);
        intent.putExtra(Constants.EXTRA_TOPIC, currentTopic);
        intent.putExtra(Constants.EXTRA_OBJEKT_KIND, objektKind);
        startActivityForResult(intent, REQUEST_CODE_SCOPE);
      }
    });
    // repopulate textview values

    Button amenTakeBack = (Button) findViewById(R.id.amen_take_back);
    amenTakeBack.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {


        final Statement statement = new Statement(currentObjekt, currentTopic);
        Log.v(TAG, "Making Statement: " + statement);
        service.addStatement(statement);
      
      }
    });


  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    Log.v(TAG, "onActivityResult");
    if (resultCode == RESULT_OK) {
      Log.v(TAG, "onActivityResult | resultCode: RESULT_OK");

      if (requestCode == REQUEST_CODE_OBJEKT) {
        // receive the new currentObjekt
        Log.v(TAG, "onActivityResult | requestCode: REQUEST_CODE_OBJEKT");

        final Objekt objekt = data.getParcelableExtra(Constants.EXTRA_OBJEKT);
        if (objekt != null) {
          currentObjekt = objekt;
          currentTopic = new Topic(objekt.getDefaultDescription(), currentBest, currentTopicScope);

        } else {
          Log.v(TAG, "onActivityResult | objekt: " + objekt);
        }

      } else if (requestCode == REQUEST_CODE_TOPIC || requestCode == REQUEST_CODE_SCOPE) {
        // receive the new currentTopic
        Log.v(TAG, "onActivityResult | requestCode: REQUEST_CODE_TOPIC");
        final Topic topic = data.getParcelableExtra(Constants.EXTRA_TOPIC);
        if (topic != null) {
          Log.v(TAG, "onActivityResult | topic: " + topic);
          currentTopic = topic;
//          currentBest = topic.isBest();
//          currentTopicScope = topic.getScope();
          
        } else { 
          Log.v(TAG, "topic was null");
        }
      }

    } else if (resultCode == RESULT_CANCELED) {
      Log.v(TAG, "onActivityResult | resultCode: RESULT_CANCELED");
    }
  }
}