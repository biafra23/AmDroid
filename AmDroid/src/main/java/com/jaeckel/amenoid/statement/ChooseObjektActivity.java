package com.jaeckel.amenoid.statement;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockListActivity;
import com.github.ignition.location.annotations.IgnitedLocation;
import com.github.ignition.location.annotations.IgnitedLocationActivity;
import com.github.ignition.location.templates.OnIgnitedLocationChangedListener;
import com.jaeckel.amenoid.Constants;
import com.jaeckel.amenoid.R;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.model.Objekt;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.util.Log;
import com.jaeckel.amenoid.util.ObjektsForQueryTask;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * User: biafra
 * Date: 10/9/11
 * Time: 10:54 PM
 */
@IgnitedLocationActivity()
public class ChooseObjektActivity extends SherlockListActivity implements ObjektsForQueryTask.ReturnedObjektsHandler, OnIgnitedLocationChangedListener {

  private static final String TAG = ChooseObjektActivity.class.getSimpleName();

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

  private boolean objektNameIsDefault = false;

  @IgnitedLocation
  private Location lastLocation;

  // MUST BE OVERRIDDEN OR IGNITION LOCATION WON'T WORK!
  @Override
  public void onResume() {
    super.onResume();
  }

  // MUST BE OVERRIDDEN OR IGNITION LOCATION WON'T WORK!
  @Override
  public void onPause() {
      super.onPause();
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onStop() {
    super.onStop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "onCreate()");

    final AmenoidApp app = AmenoidApp.getInstance();

    service = app.getService();
    currentObjekt = (Objekt) getIntent().getParcelableExtra(Constants.EXTRA_OBJEKT);
    currentObjektKind = getIntent().getIntExtra(Constants.EXTRA_OBJEKT_KIND, AmenService.OBJEKT_KIND_THING);

    if (TextUtils.isEmpty(currentObjekt.getName())) {


      if (currentObjektKind == AmenService.OBJEKT_KIND_PERSON
          && MakeStatementActivity.DEFAULT_PERSON.equals(currentObjekt.getName())) {

        objektNameIsDefault = true;

      } else if (currentObjektKind == AmenService.OBJEKT_KIND_PLACE
                 && MakeStatementActivity.DEFAULT_PLACE.equals(currentObjekt.getName())) {

        objektNameIsDefault = true;

      } else if (currentObjektKind == AmenService.OBJEKT_KIND_THING
                 && MakeStatementActivity.DEFAULT_THING.equals(currentObjekt.getName())) {

        objektNameIsDefault = true;
      }
    }

    setContentView(R.layout.choose_objekt);
    setTitle("Choose Statement Objekt");

    ListView list = (ListView) findViewById(android.R.id.list);
    final View header = getLayoutInflater().inflate(R.layout.choose_objekt_header, null, false);
    header.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {

        Objekt objekt = currentObjekt;

        if (!TextUtils.isEmpty(objektEditText.getText())) {
          objekt = new Objekt(objektEditText.getText() + "", currentObjektKind);
        }

        Log.d(TAG, "using object: " + objekt);

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


    objektEditText = (EditText) findViewById(R.id.objekt);
    if (objektNameIsDefault) {
      objektEditText.setHint(currentObjekt.getName());
    } else {
      objektEditText.setText(currentObjekt.getName());
    }
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
        queryTask.executeOnThreadPool(query);


      }
    });


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


  @Override
  public boolean onIgnitedLocationChanged(Location newLocation) {

    AmenoidApp.getInstance().setLastLocation(newLocation);

    if (AmenoidApp.DEVELOPER_MODE) {
      Toast.makeText(this, "Activity received Location update: " + newLocation.getProvider()
                           + " acc: " + newLocation.getAccuracy(), Toast.LENGTH_LONG).show();
    }
    if (currentObjektKind == AmenService.OBJEKT_KIND_PLACE) {

//      Location lastLocation = AmenoidApp.getInstance().getLastLocation();

      if (lastLocation != null) {
        longitude = lastLocation.getLongitude();
        latitude = lastLocation.getLatitude();
      }
      Log.d(TAG, "lastLocation: " + lastLocation);
      Log.d(TAG, "   longitude: " + longitude);
      Log.d(TAG, "    latitude: " + latitude);

    } else {

      Log.d(TAG, "NO PLACE: currentObjektKind: " + currentObjektKind);
    }

    final ObjektsForQueryTask queryTask = new ObjektsForQueryTask(service, ChooseObjektActivity.this);
    ObjektsForQueryTask.ObjektQuery query = queryTask.new ObjektQuery(null, currentObjektKind, latitude, longitude);
    queryTask.execute(query);

    return true;
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
      description.setText(objekt.getCategory());

      return row;
    }

  }
}