package com.jaeckel.amdroid;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.jaeckel.amdroid.api.model.Amen;

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

  public AmenListAdapter(Context context, int resource, int textViewResourceId, List<Amen> objects) {
    super(context, resource, textViewResourceId, objects);
    inflater = LayoutInflater.from(context);
    setNotifyOnChange(true);
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    View row = convertView;
    Amen amen = getItem(position);

    if (row == null) {
      row = inflater.inflate(R.layout.list_item_amen, parent, false);
      row.setTag(R.id.user, row.findViewById(R.id.user));
      row.setTag(R.id.statement, row.findViewById(R.id.statement));
      row.setTag(R.id.user_image, row.findViewById(R.id.user_image));

    }

    TextView user = (TextView) row.getTag(R.id.user);
    user.setText(amen.getUser().getName());

    TextView statement = (TextView) row.getTag(R.id.statement);

    Amen referingAmen = amen.getReferringAmen();
    String disputed = " NOT ";
    if (referingAmen != null) {
      disputed += referingAmen.getStatement().getObjekt().getName();

    }

    statement.setText(amen.getStatement().toDisplayString() + disputed);


    ImageView userImage = (ImageView) row.getTag(R.id.user_image);

    String pictureUrl = amen.getUser().getPicture();
    if (!TextUtils.isEmpty(pictureUrl)) {
      pictureUrl = pictureUrl + "?type=normal";
    }

    userImage.setImageResource(R.drawable.placeholder);
    userImage.setTag(pictureUrl);

//    BitmapManager.INSTANCE.loadBitmap(pictureUrl, userImage, 64,  64);

    return row;
  }


}
