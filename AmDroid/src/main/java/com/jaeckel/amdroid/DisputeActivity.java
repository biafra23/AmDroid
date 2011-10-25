package com.jaeckel.amdroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.model.Amen;
import com.jaeckel.amdroid.api.model.Objekt;
import com.jaeckel.amdroid.api.model.Statement;
import com.jaeckel.amdroid.app.AmdroidApp;
import com.jaeckel.amdroid.util.AmenLibTask;
import com.jaeckel.amdroid.util.StyleableSpannableStringBuilder;

import java.util.ArrayList;

/**
 * User: biafra
 * Date: 9/25/11
 * Time: 12:59 PM
 */
public class DisputeActivity extends Activity implements AdapterView.OnItemClickListener {

  private static final String TAG = "DisputeActivity";
  private AmenService service;
  private Objekt      newObjektName;
  private Amen        currentAmen;
  private Button      disputeButton;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    service = AmdroidApp.getInstance().getService();

    setContentView(R.layout.dispute);

    setTitle("Amenoid/Dispute");

    final ObjektAutoCompleteTextView textView = (ObjektAutoCompleteTextView) findViewById(R.id.autocomplete_objekt);
    ObjektCompletionAdapter adapter = new ObjektCompletionAdapter(this, R.layout.dispute_list_item_objekt, new ArrayList<Objekt>());
    textView.setAdapter(adapter);
    textView.setOnItemClickListener(this);

    Intent startingIntent = getIntent();
    currentAmen = startingIntent.getParcelableExtra(Constants.EXTRA_AMEN);

    Log.d(TAG, "Current Amen from intent: " + currentAmen);

    TextView disputedStatement = (TextView) findViewById(R.id.disputed_statement);
    disputedStatement.setText(styleDisputedStatementWithColor(currentAmen.getStatement(), this));

    disputeButton = (Button) findViewById(R.id.dispute_amen);
    disputeButton.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        if (!TextUtils.isEmpty(textView.getText().toString())) {

          disputeButton.setEnabled(false);

          if (newObjektName == null) {
            newObjektName = new Objekt();
            newObjektName.setName(textView.getText().toString());
            newObjektName.setKindId(currentAmen.getStatement().getObjekt().getKindId());
          }
          new DisputeTask(DisputeActivity.this).execute(new Amen(currentAmen.getStatement(), newObjektName, currentAmen.getId()));

          finish();
        } else {

          Toast.makeText(DisputeActivity.this, "Empty dispute ignored", Toast.LENGTH_SHORT).show();
          disputeButton.setEnabled(true);
        }
      }
    });


    Button neverMindButton = (Button) findViewById(R.id.dispute_never_mind);
    neverMindButton.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        finish();
      }
    });

  }


  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    Log.d(TAG, "onItemClick: adapterView: " + adapterView + " view: " + view + ", i: " + i + " l: " + l);
    Log.d(TAG, "onItemClick: Item: " + adapterView.getAdapter().getItem(i));
    newObjektName = (Objekt) adapterView.getAdapter().getItem(i);
    Log.d(TAG, "-----> newObjektName: " + newObjektName);
  }

  public static CharSequence styleDisputedStatementWithColor(Statement stmt, Context context) {

    StyleableSpannableStringBuilder statementBuilder = new StyleableSpannableStringBuilder(context);

    statementBuilder
      .append("Hell No! ");

    if (stmt.getObjekt().getKindId() == 2) {
      //Thing

      if (stmt.getTopic().isBest()) {
        statementBuilder.appendOrange("The Best ");
      } else {
        statementBuilder.appendOrange("The Worst ");
      }
      statementBuilder
        .appendOrange(stmt.getTopic().getDescription())
        .appendOrange(" ")
        .appendOrange(stmt.getTopic().getScope());


    } else if (stmt.getObjekt().getKindId() == 1) {
      //Place

      if (stmt.getTopic().isBest()) {
        statementBuilder.appendBlue("The Best Place for ");
      } else {
        statementBuilder.appendBlue("The Worst Place for ");
      }
      statementBuilder
        .appendBlue(stmt.getTopic().getDescription())
        .appendBlue(" ")
        .appendBlue(stmt.getTopic().getScope());

    } else if (stmt.getObjekt().getKindId() == 0) {

      if (stmt.getTopic().isBest()) {
        statementBuilder.appendGreen("The Best ");
      } else {
        statementBuilder.appendGreen("The Worst ");
      }
      statementBuilder
        .appendGreen(stmt.getTopic().getDescription())
        .appendGreen(" ")
        .appendGreen(stmt.getTopic().getScope());
    }

    statementBuilder.appendBold(" is ");


    return statementBuilder;
  }

  class DisputeTask extends AmenLibTask<Amen, Integer, Long> {

    public DisputeTask(Context context) {
      super(context);
    }

    @Override
    protected Long wrappedDoInBackground(Amen... amens) {
      for (Amen amen : amens) {
        service.dispute(amen);
      }

      return null;
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    protected void wrappedOnPostExecute(Long result) {
      if (result != null) {
        Toast.makeText(DisputeActivity.this, "Disputed", Toast.LENGTH_SHORT).show();
      }
      disputeButton.setEnabled(true);
    }
  }

}