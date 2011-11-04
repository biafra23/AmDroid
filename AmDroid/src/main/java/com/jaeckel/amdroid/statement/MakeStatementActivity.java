package com.jaeckel.amdroid.statement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.jaeckel.amdroid.Constants;
import com.jaeckel.amdroid.R;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.model.Amen;
import com.jaeckel.amdroid.api.model.Objekt;
import com.jaeckel.amdroid.api.model.Statement;
import com.jaeckel.amdroid.api.model.Topic;
import com.jaeckel.amdroid.app.AmdroidApp;
import com.jaeckel.amdroid.util.AmenLibTask;

import java.io.IOException;

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

//  private Drawable backgroundDrawable;

  public static final int REQUEST_CODE_OBJEKT = 10101;
  public static final int REQUEST_CODE_TOPIC  = 10102;
  public static final int REQUEST_CODE_SCOPE  = 10103;

  private static final String TAG = "MakeStatementActivity";

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    service = AmdroidApp.getInstance().getService();
    setTitle("Amenoid/Make A Statement");

    currentBest = true;
    currentObjekt = new Objekt();
    currentTopic = new Topic();


    objektKind = getIntent().getIntExtra(Constants.EXTRA_OBJEKT_KIND, AmenService.OBJEKT_KIND_THING);

    if (objektKind == AmenService.OBJEKT_KIND_PERSON) {
//      backgroundDrawable = createBackgroundDrawable(objektKind);
      currentObjekt.setName("Karl Marx");
      currentObjekt.setKindId(AmenService.OBJEKT_KIND_PERSON);
      currentTopic.setBest(true);
      currentTopic.setDescription("Author");
      currentTopic.setScope("Ever");
    }
    if (objektKind == AmenService.OBJEKT_KIND_THING) {
//      backgroundDrawable = createBackgroundDrawable(objektKind);
      currentObjekt.setName("Android");
      currentObjekt.setKindId(AmenService.OBJEKT_KIND_THING);
      currentTopic.setBest(true);
      currentTopic.setDescription("Operating System");
      currentTopic.setScope("So far");
    }
    if (objektKind == AmenService.OBJEKT_KIND_PLACE) {
//      backgroundDrawable = createBackgroundDrawable(objektKind);
      currentObjekt.setName("Drachenspielplatz");
      currentObjekt.setKindId(AmenService.OBJEKT_KIND_PLACE);
      currentTopic.setBest(true);
      currentTopic.setDescription("Playground");
      currentTopic.setScope("in Berlin");

    }

    currentTopicScope = currentTopic.getScope();
    currentBest = currentTopic.isBest();


    setContentView(R.layout.make_statement);
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
    objektView = (TextView) findViewById(R.id.objekt_name);
    objektView.setBackgroundDrawable(createBackgroundDrawable(objektKind));
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
    bestView.setBackgroundDrawable(createBackgroundDrawable(objektKind));
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
          currentBest = true;
        }
      }
    });

    topicView = (TextView) findViewById(R.id.topic_name);
    topicView.setBackgroundDrawable(createBackgroundDrawable(objektKind));
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
    scopeView.setBackgroundDrawable(createBackgroundDrawable(objektKind));
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

        currentTopic.setBest(currentBest);
        final Statement statement = new Statement(currentObjekt, currentTopic);

        new MakeStatementTask(MakeStatementActivity.this).execute(statement);

      }
    });


  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    Log.d(TAG, "onActivityResult");
    if (resultCode == RESULT_OK) {
      Log.d(TAG, "onActivityResult | resultCode: RESULT_OK");

      if (requestCode == REQUEST_CODE_OBJEKT) {
        // receive the new currentObjekt
        Log.d(TAG, "onActivityResult | requestCode: REQUEST_CODE_OBJEKT");

        final Objekt objekt = data.getParcelableExtra(Constants.EXTRA_OBJEKT);
        Log.d(TAG, "onActivityResult | objekt: " + objekt);
        if (objekt != null) {

          currentObjekt = objekt;
          if (objekt.getDefaultDescription() != null) {
            currentTopic = new Topic(objekt.getDefaultDescription(), currentBest, currentTopicScope);
          }
        }

      } else if (requestCode == REQUEST_CODE_TOPIC || requestCode == REQUEST_CODE_SCOPE) {
        // receive the new currentTopic
        Log.d(TAG, "onActivityResult | requestCode: REQUEST_CODE_TOPIC");
        final Topic topic = data.getParcelableExtra(Constants.EXTRA_TOPIC);
        if (topic != null) {
          Log.d(TAG, "onActivityResult | topic: " + topic);
          currentTopic = topic;
//          currentBest = topic.isBest();
//          currentTopicScope = topic.getScope();

        } else {
          Log.d(TAG, "topic was null");
        }
      }

    } else if (resultCode == RESULT_CANCELED) {
      Log.d(TAG, "onActivityResult | resultCode: RESULT_CANCELED");
    }
  }

  private Drawable createBackgroundDrawable(int kind) {
    Drawable result;
    if (kind == AmenService.OBJEKT_KIND_PERSON) {
      result = getResources().getDrawable(R.drawable.rounded_edges_green);
    } else if (kind == AmenService.OBJEKT_KIND_PLACE) {
      result = getResources().getDrawable(R.drawable.rounded_edges_blue);
    } else {
      result = getResources().getDrawable(R.drawable.rounded_edges_orange);
    }

    return result;
  }

  //
  // MakeStatementTask
  //
  private class MakeStatementTask extends AmenLibTask<Statement, Integer, Amen> {

    public MakeStatementTask(Context context) {
      super(context);
    }

    protected Amen wrappedDoInBackground(Statement... statements) throws IOException {

      Amen result = null;

      for (Statement statement : statements) {

        // clean up statement
        statement.getObjekt().setCategory(null);
        statement.getObjekt().setDefaultDescription(null);
        statement.getObjekt().setDefaultScope(null);
        statement.getObjekt().setPossibleDescriptions(null);
        statement.getObjekt().setPossibleScopes(null);

        result = service.addStatement(statement);
      }


      return result;
    }

    @Override
    protected void onPreExecute() {
    }

    protected void wrappedOnPostExecute(Amen result) {
      if (result != null) {
        Toast.makeText(MakeStatementActivity.this, "Amen.", Toast.LENGTH_LONG).show();
        finish();
      }
    }
  }

}