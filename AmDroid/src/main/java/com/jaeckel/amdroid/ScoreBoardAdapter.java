package com.jaeckel.amdroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jaeckel.amdroid.api.model.RankedStatements;

import java.util.List;

/**
 * User: biafra
 * Date: 10/15/11
 * Time: 5:44 PM
 */
public class ScoreBoardAdapter extends ArrayAdapter<RankedStatements> {

  private LayoutInflater inflater;

  public ScoreBoardAdapter(Context context, int textViewResourceId, List<RankedStatements> objects) {
    super(context, textViewResourceId, objects);
    inflater = LayoutInflater.from(context);
  }

//  public ScoreBoardAdapter(Context context, int resource, int textViewResourceId, List<Statement> objects) {
//    super(context, resource, textViewResourceId, objects);
//  }


  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    View row = convertView;
    RankedStatements statement = getItem(position);

    if (row == null) {
      row = inflater.inflate(R.layout.list_item_score, parent, false);
      row.setTag(R.id.objekt_name, row.findViewById(R.id.objekt_name));
      row.setTag(R.id.amen_count, row.findViewById(R.id.amen_count));
    }

    TextView nameView = (TextView) row.getTag(R.id.objekt_name);
    nameView.setText(statement.getStatement().getObjekt().getName());

    TextView countView = (TextView) row.getTag(R.id.amen_count);
    countView.setText(statement.getStatement().getTotalAmenCount() + " Amen");

    LinearLayout bar = (LinearLayout) row.findViewById(R.id.bar);
//    Drawable scaleBar =
//    bar.setBackgroundDrawable();

    return row;
  }

}
