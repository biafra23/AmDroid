package com.jaeckel.amdroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.model.Amen;
import com.jaeckel.amdroid.api.model.Statement;
import com.jaeckel.amdroid.util.StyleableSpannableStringBuilder;

import java.util.List;

/**
 * User: biafra
 * Date: 10/15/11
 * Time: 6:06 AM
 */
public class AmenAdapter extends ArrayAdapter<Amen> {


  private LayoutInflater inflater;

  public AmenAdapter(Context context, int textViewResourceId, List<Amen> objects) {
    super(context, textViewResourceId, objects);
    inflater = LayoutInflater.from(context);
    //    setNotifyOnChange(true);
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    View row = convertView;
    Amen amen = getItem(position);

    final Statement stmt = amen.getStatement();
    if (row == null) {
      row = inflater.inflate(R.layout.list_item_amen_no_pic, parent, false);
      row.setTag(R.id.statement, row.findViewById(R.id.statement));
      row.setTag(R.id.date, row.findViewById(R.id.date));
      row.setTag(R.id.amen_count, row.findViewById(R.id.amen_count));

    }

    TextView statement = (TextView) row.getTag(R.id.statement);
    statement.setText(styleAmenWithColor(amen, getContext()));


    TextView dateView = (TextView) row.getTag(R.id.date);
    dateView.setText(AmenDetailActivity.format(amen.getCreatedAt()));

    TextView amenCountView = (TextView) row.getTag(R.id.amen_count);
    amenCountView.setText(amen.getStatement().getAgreeingNetwork().size() + " Amen");


    return row;
  }

  public static CharSequence styleAmenWithColor(Amen amen, Context context) {
    Statement stmt = amen.getStatement();

    StyleableSpannableStringBuilder statementBuilder = new StyleableSpannableStringBuilder(context);

    statementBuilder
      .appendBold(stmt.getObjekt().getName())
      .append(" ");

    if (stmt.getObjekt().getKindId() == AmenService.OBJEKT_KIND_THING) {
      //Thing
      statementBuilder.appendOrange("is the ");
      if (stmt.getTopic().isBest()) {
        statementBuilder.appendOrange(" Best ");
      } else {
        statementBuilder.appendOrange(" Worst ");
      }
      statementBuilder
        .appendOrange(stmt.getTopic().getDescription())
        .appendOrange(" ")
        .appendOrange(stmt.getTopic().getScope());


    } else if (stmt.getObjekt().getKindId() == AmenService.OBJEKT_KIND_PLACE) {
      //Place
      statementBuilder.appendBlue("is the ");
      if (stmt.getTopic().isBest()) {
        statementBuilder.appendBlue(" Best Place for ");
      } else {
        statementBuilder.appendBlue(" Worst Place for ");
      }
      statementBuilder
        .appendBlue(stmt.getTopic().getDescription())
        .appendBlue(" ")
        .appendBlue(stmt.getTopic().getScope());

    } else if (stmt.getObjekt().getKindId() == AmenService.OBJEKT_KIND_PERSON) {
      statementBuilder.appendGreen("is the ");
      if (stmt.getTopic().isBest()) {
        statementBuilder.appendGreen(" Best ");
      } else {
        statementBuilder.appendGreen(" Worst ");
      }
      statementBuilder
        .appendGreen(stmt.getTopic().getDescription())
        .appendGreen(" ")
        .appendGreen(stmt.getTopic().getScope());
    }

    if (amen.isDispute()) {

      statementBuilder.appendGray(" not ")
                      .appendGray(amen.getReferringAmen().getStatement().getObjekt().getName());

    }


    return statementBuilder;
  }


}
