package com.jaeckel.amdroid.statement;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import java.util.List;

/**
 * User: biafra
 * Date: 10/9/11
 * Time: 10:54 PM
 */
public class ChooseObjektActivity extends ListActivity {

  private static final String TAG = "ChooseObjektActivity";


  private Objekt        currentObjekt;
  private int           currentObjektKind;
  private AmenService   service;
  private EditText      objektEditText;
  private ObjektAdapter adapter;
  private Drawable      backgroundDrawable;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    service = AmdroidApp.getInstance().getService();
    currentObjekt = (Objekt) getIntent().getParcelableExtra(Constants.EXTRA_OBJEKT);
    currentObjektKind = getIntent().getIntExtra(Constants.EXTRA_OBJEKT_KIND, AmenService.OBJEKT_KIND_THING);

    setContentView(R.layout.choose_objekt);
    if (currentObjektKind == AmenService.OBJEKT_KIND_PERSON) {
      backgroundDrawable = getResources().getDrawable(R.drawable.rounded_edges_green);
    }
    if (currentObjektKind == AmenService.OBJEKT_KIND_THING) {
      backgroundDrawable = getResources().getDrawable(R.drawable.rounded_edges_orange);

    }
    if (currentObjektKind == AmenService.OBJEKT_KIND_PLACE) {
      backgroundDrawable = getResources().getDrawable(R.drawable.rounded_edges_blue);
    }

    objektEditText = (EditText) findViewById(R.id.objekt);
    objektEditText.setText(currentObjekt.getName());

    List<Objekt> objekts = service.objektsForQuery(currentObjekt.getName(), currentObjektKind, null, null);
    for (Objekt o : objekts) {
      Log.d(TAG, "o: " + o);
    }

    adapter = new ObjektAdapter(this, R.layout.list_item_objekt, objekts);
    setListAdapter(adapter);


  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {

    Objekt objekt = (Objekt) getListAdapter().getItem(position);

    Log.v(TAG, "onListItemClick | Selected Objekt: " + objekt);

    Intent intent = new Intent();
    intent.putExtra(Constants.EXTRA_OBJEKT, objekt);
    setResult(RESULT_OK, intent);
    finish();
  }


  class ObjektAdapter extends ArrayAdapter<Objekt> {


    private LayoutInflater inflater;
    private static final String TAG = "ObjektAdapter";


    public ObjektAdapter(Context context, int textViewResourceId, List<Objekt> objects) {
      super(context, textViewResourceId, objects);
      inflater = LayoutInflater.from(context);
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