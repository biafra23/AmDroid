package com.jaeckel.amdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import com.jaeckel.amdroid.api.model.Amen;
import com.jaeckel.amdroid.api.model.Objekt;
import com.jaeckel.amdroid.api.model.Statement;
import com.jaeckel.amdroid.util.StyleableSpannableStringBuilder;

import java.util.ArrayList;

/**
 * User: biafra
 * Date: 9/25/11
 * Time: 12:59 PM
 */
public class DisputeActivity extends Activity implements AdapterView.OnItemClickListener {

  private static final String TAG = "DisputeActivity";

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.dispute);

    ObjektAutoCompleteTextView textView = (ObjektAutoCompleteTextView) findViewById(R.id.autocomplete_objekt);
    ObjektCompletionAdapter adapter = new ObjektCompletionAdapter(this, R.layout.dispute_list_item_objekt, new ArrayList<Objekt>());
    textView.setAdapter(adapter);
    textView.setOnItemClickListener(this);

    Intent startingIntent = getIntent();

    Amen currentAmen = startingIntent.getParcelableExtra(Constants.EXTRA_AMEN);

    Log.d(TAG, "Current Amen from intent: " + currentAmen);

    TextView disputedStatement = (TextView) findViewById(R.id.disputed_statement);
    disputedStatement.setText(styleDisputedStatementWithColor(currentAmen.getStatement()));

  }


  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    Log.d(TAG, "onItemClick: adapterView: " + adapterView + " view: " + view + ", i: " + i + " l: " + l);
    Log.d(TAG, "onItemClick: Item: " + adapterView.getAdapter().getItem(i));

  }

  public static CharSequence styleDisputedStatementWithColor(Statement stmt) {

    StyleableSpannableStringBuilder statementBuilder = new StyleableSpannableStringBuilder();

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


}