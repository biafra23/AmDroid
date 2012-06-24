package com.jaeckel.amenoid;

import java.io.IOException;
import java.util.List;

import com.github.ignition.location.annotations.IgnitedLocation;
import com.github.ignition.location.annotations.IgnitedLocationActivity;
import com.github.ignition.location.templates.OnIgnitedLocationChangedListener;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.model.Objekt;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.util.Log;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

/**
 * User: biafra
 * Date: 10/4/11
 * Time: 12:22 AM
 */

@IgnitedLocationActivity()
public class ObjektCompletionAdapter extends ArrayAdapter<Objekt> implements Filterable, OnIgnitedLocationChangedListener {

  private LayoutInflater inflater;
  private static final String TAG = "ObjektCompletionAdapter";
  private Typeface amenTypeBold;
  private Typeface amenTypeThin;
  private int      currentKindId;

  @IgnitedLocation
  private Location lastLocation;
  private Context  context;

  public ObjektCompletionAdapter(Context context, int textViewResourceId, List<Objekt> objects, int disputedObjektKindId) {
    super(context, textViewResourceId, objects);
    this.inflater = LayoutInflater.from(context);
    this.amenTypeThin = AmenoidApp.getInstance().getAmenTypeThin();
    this.amenTypeBold = AmenoidApp.getInstance().getAmenTypeBold();
    this.currentKindId = disputedObjektKindId;
    this.context = context;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {

    View row = convertView;
    Objekt objekt = getItem(position);
    if (row == null) {
      row = inflater.inflate(R.layout.dispute_list_item_objekt, parent, false);
      row.setTag(R.id.completion_item_name, row.findViewById(R.id.completion_item_name));

    }

    TextView textView = (TextView) row.findViewById(R.id.completion_item_name);
    textView.setText(objekt.getName());
    textView.setTypeface(amenTypeBold);
    textView.setTextColor(Color.WHITE);

    TextView description = (TextView) row.findViewById(R.id.default_description);
    description.setTypeface(amenTypeThin);
    description.setText(objekt.getDefaultDescription());
    description.setTextColor(Color.WHITE);

    return row;
  }

  @Override
  public Filter getFilter() {

    return new Filter() {

      @Override
      public String convertResultToString(Object resultValue) {

        Objekt o = (Objekt) resultValue;

        return o.getName();
      }


      @Override
      protected FilterResults performFiltering(CharSequence charSequence) {

        Log.d(TAG, "performFiltering: charSequence: " + charSequence);

        FilterResults results = new FilterResults();

        AmenService service = AmenoidApp.getInstance().getService();

        List<Objekt> values = null;
        try {
          Double lat = null;
          Double lon = null;
          if (currentKindId == AmenService.OBJEKT_KIND_PLACE) {


            if (lastLocation == null) {
              lastLocation = AmenoidApp.getInstance().getLastLocation();
            }
            if (lastLocation != null) {
              lon = lastLocation.getLongitude();
              lat = lastLocation.getLatitude();
            }
            Log.v(TAG, "lastLocation: " + lastLocation);
            Log.v(TAG, "   longitude: " + lon);
            Log.v(TAG, "    latitude: " + lat);
          }
          values = service.objektsForQuery(charSequence, currentKindId, lat, lon);
          //This should not be necessary
          for (Objekt v : values) {
            v.setKindId(currentKindId);
          }
        } catch (
          IOException e
          )

        {
          e.printStackTrace();
        }


        results.values = values;
        results.count = values.size();
        return results;
      }


      @Override
      protected void publishResults(CharSequence constraint,
                                    FilterResults results) {


        if (results != null && results.count > 0) {

          Log.d(TAG, "publishResults: Results changed. ");

          ObjektCompletionAdapter.this.clear();
          List<Objekt> list = (List<Objekt>) results.values;
          for (Objekt objekt : list) {
            ObjektCompletionAdapter.this.add(objekt);
          }
          notifyDataSetChanged();
        }
      }
    }

      ;
  }

  @Override
  public boolean onIgnitedLocationChanged(Location newLocation) {
    if (AmenoidApp.DEVELOPER_MODE) {
      Toast.makeText(context, "Adapter received Location update: " + newLocation.getProvider()
                              + " acc: " + newLocation.getAccuracy(), Toast.LENGTH_LONG).show();
    }

    return true;
  }
}


