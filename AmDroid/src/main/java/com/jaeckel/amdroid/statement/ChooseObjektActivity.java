package com.jaeckel.amdroid.statement;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
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
import com.jaeckel.amdroid.app.AmdroidApp;
import com.jaeckel.amdroid.util.ObjektsForQueryTask;

import java.util.ArrayList;
import java.util.List;

/**
 * User: biafra
 * Date: 10/9/11
 * Time: 10:54 PM
 */
public class ChooseObjektActivity extends ListActivity implements ObjektsForQueryTask.ReturnedObjektsHandler {

  private static final String TAG = "ChooseObjektActivity";


  private Objekt        currentObjekt;
  private int           currentObjektKind;
  private AmenService   service;
  private EditText      objektEditText;
  private ObjektAdapter adapter;
  private Drawable      backgroundDrawable;
  private Double longitude = null;
  private Double latitude  = null;
  private TextView headerDefaultDescription;
  private TextView headerCompletionItemName;

  private ObjektsForQueryTask queryTask;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    final AmdroidApp app = AmdroidApp.getInstance();

    service = app.getService();
    currentObjekt = (Objekt) getIntent().getParcelableExtra(Constants.EXTRA_OBJEKT);
    currentObjektKind = getIntent().getIntExtra(Constants.EXTRA_OBJEKT_KIND, AmenService.OBJEKT_KIND_THING);

    setContentView(R.layout.choose_objekt);
    setTitle("Amenoid/Choose Statement Objekt");

    ListView list = (ListView) findViewById(android.R.id.list);
    View header = getLayoutInflater().inflate(R.layout.choose_objekt_header, null, false);
    header.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Objekt objekt = new Objekt(objektEditText.getText() + "", currentObjektKind);

        Log.d(TAG, "new Objekt: " + objekt);

        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_OBJEKT, objekt);

        setResult(RESULT_OK, intent);
        finish();
      }
    });
    list.addHeaderView(header);

    headerDefaultDescription = (TextView) findViewById(R.id.header_default_description);
    headerCompletionItemName = (TextView) findViewById(R.id.header_completion_item_name);

    if (currentObjektKind == AmenService.OBJEKT_KIND_PERSON) {
      backgroundDrawable = getResources().getDrawable(R.drawable.rounded_edges_green);
    }
    if (currentObjektKind == AmenService.OBJEKT_KIND_THING) {
      backgroundDrawable = getResources().getDrawable(R.drawable.rounded_edges_orange);

    }
    if (currentObjektKind == AmenService.OBJEKT_KIND_PLACE) {
      backgroundDrawable = getResources().getDrawable(R.drawable.rounded_edges_blue);
    }

    headerCompletionItemName.setBackgroundDrawable(backgroundDrawable);

    if (currentObjektKind == AmenService.OBJEKT_KIND_PLACE) {

      final Location lastLocation = app.getLastLocation();
      if (lastLocation != null) {
        longitude = lastLocation.getLongitude();
        latitude = lastLocation.getLatitude();
      }
      Log.v(TAG, "lastLocation: " + lastLocation);
      Log.v(TAG, "   longitude: " + longitude);
      Log.v(TAG, "    latitude: " + latitude);
    }

    objektEditText = (EditText) findViewById(R.id.objekt);
    objektEditText.setText(currentObjekt.getName());
    objektEditText.addTextChangedListener(new TextWatcher() {

      boolean isDelete = false;
      CharSequence before;
      CharSequence after;
      long delay;

      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        before = charSequence;
      }

      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        after = charSequence;
      }

      public void afterTextChanged(Editable editable) {
        delay = 0;

        //TODO: when a String does not yield any result, adding more characters will not fix that. Stop searching then

        Log.d(TAG, "afterTextChanged:          editable: " + editable);
        Log.d(TAG, "afterTextChanged: currentObjektKind: " + currentObjektKind);


        if (after.length() < before.length()) {
          isDelete = true;
          delay = 300;
        }
        Log.d(TAG, "before: " + before + " after: " + after + " delete?: " + isDelete);
        headerCompletionItemName.setText(editable.toString());
        headerDefaultDescription.setText("");

        if (queryTask != null) {
          queryTask.cancel(true);
        }
        queryTask = new ObjektsForQueryTask(service, ChooseObjektActivity.this);

        ObjektsForQueryTask.ObjektQuery query = queryTask.new ObjektQuery(editable.toString(), currentObjektKind, latitude, longitude, delay);
        queryTask.execute(query);


      }
    });

    final ObjektsForQueryTask queryTask = new ObjektsForQueryTask(service, ChooseObjektActivity.this);
    ObjektsForQueryTask.ObjektQuery query = queryTask.new ObjektQuery(null, currentObjektKind, latitude, longitude);
    queryTask.execute(query);

//    List<Objekt> objekts = service.objektsForQuery(currentObjekt.getName(), currentObjektKind, latitude, longitude);
//    List<Objekt> objekts = service.objektsForQuery(null, currentObjektKind, latitude, longitude);
//    if (!objektInList(currentObjekt.getName(), objekts)) {
//      objekts.add(0, currentObjekt);
//    }

    headerCompletionItemName.setText(currentObjekt.getName());
    headerDefaultDescription.setText(currentObjekt.getDefaultDescription());

    adapter = new ObjektAdapter(this, R.layout.list_item_objekt, new ArrayList<Objekt>());
    setListAdapter(adapter);

    getListView().setDivider(null);
    getListView().setDividerHeight(0);

  }

  private boolean objektInList(String name, List<Objekt> objekts) {
    if (objekts == null) {
      return false;
    }
    if (objekts.size() == 0) {
      return false;
    }
    for (Objekt o : objekts) {
      if (name.equalsIgnoreCase(o.getName())) {
        return true;
      }
    }
    return false;
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {

    if (position > 0) {

      Objekt objekt = (Objekt) getListAdapter().getItem(position - 1);

      Log.d(TAG, "onListItemClick | Selected Objekt: " + objekt);

      Intent intent = new Intent();
      intent.putExtra(Constants.EXTRA_OBJEKT, objekt);

      setResult(RESULT_OK, intent);
      finish();
    }
  }

  public void handleObjektsResult(List<Objekt> result) {

    adapter = new ObjektAdapter(ChooseObjektActivity.this, R.layout.list_item_objekt, result);
    setListAdapter(adapter);

  }


  class ObjektAdapter extends ArrayAdapter<Objekt> {


    private LayoutInflater inflater;
    private static final String TAG = "ObjektAdapter";


    public ObjektAdapter(Context context, int textViewResourceId, List<Objekt> objects) {
      super(context, textViewResourceId, objects);
      inflater = LayoutInflater.from(context);
      setNotifyOnChange(true);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

      View row = convertView;
      Objekt objekt = getItem(position);
      Drawable background;

      if (row == null) {
        row = inflater.inflate(R.layout.list_item_objekt, parent, false);
        row.setTag(R.id.completion_item_name, row.findViewById(R.id.completion_item_name));
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
      textView.setText(objekt.getName());
      textView.setBackgroundDrawable(background);

      TextView description = (TextView) row.findViewById(R.id.default_description);
      description.setText(objekt.getDefaultDescription());

      return row;
    }

  }
}