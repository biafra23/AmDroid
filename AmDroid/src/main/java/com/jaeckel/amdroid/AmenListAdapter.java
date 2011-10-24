package com.jaeckel.amdroid;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.model.Amen;
import com.jaeckel.amdroid.api.model.Statement;
import com.jaeckel.amdroid.util.StyleableSpannableStringBuilder;

import java.util.List;

/**
 * User: biafra
 * Date: 9/25/11
 * Time: 5:58 AM
 */
public class AmenListAdapter extends ArrayAdapter<Amen> {

  private LayoutInflater inflater;

  public AmenListAdapter(Context context, int textViewResourceId, List<Amen> objects) {
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
      row = inflater.inflate(R.layout.list_item_amen, parent, false);
      row.setTag(R.id.user, row.findViewById(R.id.user));
      row.setTag(R.id.statement, row.findViewById(R.id.statement));
      row.setTag(R.id.user_image, row.findViewById(R.id.user_image));

    }

    TextView user = (TextView) row.getTag(R.id.user);
    String from = amen.getUser().getName();

    if (amen.isAmen()) {
      from = from + " amen'd " + amen.getReferringAmen().getUser().getName();
    }
    if (amen.isDispute() && amen.getReferringAmen() != null) {
      from = from + " disputes " + amen.getReferringAmen().getUser().getName();
    }
    user.setText(from);

    TextView statementView = (TextView) row.getTag(R.id.statement);

    statementView.setText(styleAmenWithColor(amen, getContext()));

    ImageView userImage = (ImageView) row.getTag(R.id.user_image);

    String pictureUrl = amen.getUser().getPhoto();
    if (TextUtils.isEmpty(pictureUrl)) {
      pictureUrl = amen.getUser().getPicture();
      if (!TextUtils.isEmpty(pictureUrl)) {
        pictureUrl = pictureUrl + "?type=normal";
      }
    }
//    Log.d("AmenListAdapter", "pictureUrl: " + pictureUrl);
    userImage.setImageResource(R.drawable.placeholder);
    userImage.setTag(pictureUrl);

//    BitmapManager.INSTANCE.loadBitmap(pictureUrl, userImage, 64,  64);

    return row;
  }

  public static CharSequence styleAmenWithColor(Statement stmt, boolean isDispute, String disputingName, Context context) {

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

    if (isDispute && disputingName != null) {

      statementBuilder.appendGray(" not ")
                      .appendGray(disputingName);

    }

    return statementBuilder;
  }

  public static CharSequence styleAmenWithColor(Amen amen, Context context) {

    Statement stmt = amen.getStatement();
    boolean isDispute = amen.isDispute();
    String disputingName = null;
    if (isDispute && amen.getReferringAmen() != null) {
      disputingName = amen.getReferringAmen().getStatement().getObjekt().getName();
    }
    return styleAmenWithColor(stmt, isDispute, disputingName, context);
  }


}
