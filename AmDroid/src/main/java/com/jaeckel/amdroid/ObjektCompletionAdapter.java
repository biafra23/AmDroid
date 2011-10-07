package com.jaeckel.amdroid;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.model.Objekt;
import com.jaeckel.amdroid.app.AmdroidApp;

import java.util.List;

/**
 * User: biafra
 * Date: 10/4/11
 * Time: 12:22 AM
 */
public class ObjektCompletionAdapter extends ArrayAdapter<Objekt> implements Filterable {

  private LayoutInflater inflater;
  private static final String TAG = "ObjektCompletionAdapter";

//  public ObjektCompletionAdapter(Context context, int textViewResourceId) {
//    super(context, textViewResourceId);
//    inflater = LayoutInflater.from(context);
//  }
//
//  public ObjektCompletionAdapter(Context context, int resource, int textViewResourceId) {
//    super(context, resource, textViewResourceId);
//    inflater = LayoutInflater.from(context);
//  }
//
//  public ObjektCompletionAdapter(Context context, int textViewResourceId, Objekt[] objects) {
//    super(context, textViewResourceId, objects);
//    inflater = LayoutInflater.from(context);
//  }
//
//  public ObjektCompletionAdapter(Context context, int resource, int textViewResourceId, Objekt[] objects) {
//    super(context, resource, textViewResourceId, objects);
//    inflater = LayoutInflater.from(context);
//  }

  public ObjektCompletionAdapter(Context context, int textViewResourceId, List<Objekt> objects) {
    super(context, textViewResourceId, objects);
    inflater = LayoutInflater.from(context);
  }

//  public ObjektCompletionAdapter(Context context, int resource, int textViewResourceId, List<Objekt> objects) {
//    super(context, resource, textViewResourceId, objects);
//    inflater = LayoutInflater.from(context);
//  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {

    View row = convertView;
    Objekt objekt = getItem(position);
    if (row == null) {
      row = inflater.inflate(R.layout.dispute_list_item_objekt, parent, false);
      row.setTag(R.id.completion_item, row.findViewById(R.id.completion_item));

    }

    TextView textView = (TextView) row.findViewById(R.id.completion_item);
    textView.setText(objekt.getName());
    
    TextView description = (TextView) row.findViewById(R.id.default_description);
    description.setText(objekt.getDefaultDescription());
    
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

//      @Override
//      protected FilterResults performFiltering(CharSequence constraint) {
//        if (constraint != null) {
//          ArrayList<Objekt> tmpAllData = mAllData;
//          ArrayList<Objekt> tmpDataShown = mDataShown;
//
//          tmpDataShown.clear();
//          for (int i = 0; i < tmpAllData.size();
//               i++) {
//            if (tmpAllData.get(i).get(Country.NAME).toLowerCase().startsWith(constraint.toString().toLowerCase())) {
//              tmpDataShown.add(tmpAllData.get(i));
//            }
//          }
//
//          FilterResults filterResults = new FilterResults();
//          filterResults.values = tmpDataShown;
//          filterResults.count = tmpDataShown.size();
//          return filterResults;
//        } else {
//          return new FilterResults();
//        }
//      }

      @Override
      protected FilterResults performFiltering(CharSequence charSequence) {

        Log.d(TAG, "performFiltering: charSequence: " + charSequence);

        FilterResults results = new FilterResults();

        AmenService service = AmdroidApp.getInstance().getService();

        List<Objekt> values = service.objektsForQuery(charSequence, Objekt.THING, null, null);


        results.values = values;
        results.count = values.size();
        return results;
      }

      @Override
      protected void publishResults(CharSequence constraint,
                                    FilterResults results) {


        if (results != null && results.count > 0) {

          Log.d(TAG, "Results changed. ");

          ObjektCompletionAdapter.this.clear();
          List<Objekt> list = (List<Objekt>) results.values;
          for (Objekt objekt : list) {
            ObjektCompletionAdapter.this.add(objekt);
          }
          notifyDataSetChanged();
        }
      }
    };
  }
}


