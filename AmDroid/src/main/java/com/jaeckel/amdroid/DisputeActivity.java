package com.jaeckel.amdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import com.jaeckel.amdroid.api.model.Amen;
import com.jaeckel.amdroid.api.model.Objekt;

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
    
    Log.d(TAG, "Current Amen from intent: " + currentAmen );
    
  }


  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    Log.d(TAG, "onItemClick: adapterView: " + adapterView + " view: " + view + ", i: " + i + " l: " +l);
    
  }
}