package com.jaeckel.amdroid;

import android.app.Activity;
import android.os.Bundle;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.model.Objekt;
import com.jaeckel.amdroid.app.AmdroidApp;

import java.util.List;

/**
 * User: biafra
 * Date: 9/25/11
 * Time: 12:59 PM
 */
public class DisputeActivity extends Activity {

  private AmenService service;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.dispute);

    service = AmdroidApp.getInstance().getService();
    List<Objekt> objekts = service.objektsForQuery("amen", Objekt.THING, null, null);
    for (Objekt o : objekts) {
      System.out.println("o: " + o);
    }
    ObjektAutoCompleteTextView textView = (ObjektAutoCompleteTextView) findViewById(R.id.autocomplete_country);

    ObjektCompletionAdapter adapter = new ObjektCompletionAdapter(this, R.layout.dispute_list_item_objekt, objekts);
    textView.setAdapter(adapter);


  }
}