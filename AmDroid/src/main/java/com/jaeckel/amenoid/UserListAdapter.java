package com.jaeckel.amenoid;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.jaeckel.amenoid.api.model.User;
import com.jaeckel.amenoid.app.AmenoidApp;

import java.util.List;

/**
 * User: biafra
 * Date: 10/15/11
 * Time: 4:39 AM
 */
public class UserListAdapter extends ArrayAdapter<User> {

  private LayoutInflater inflater;
  private Typeface       amenTypeThin;
  private Typeface       amenTypeBold;

  public UserListAdapter(Context context, int textViewResourceId, List<User> objects) {
    super(context, textViewResourceId, objects);
    inflater = LayoutInflater.from(context);
    amenTypeThin = AmenoidApp.getInstance().getAmenTypeThin();
    amenTypeBold = AmenoidApp.getInstance().getAmenTypeBold();
  }


  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    View row = convertView;
    User user = getItem(position);

    if (row == null) {
      row = inflater.inflate(R.layout.list_item_user, parent, false);
      row.setTag(R.id.user, row.findViewById(R.id.user));
      row.setTag(R.id.user_image, row.findViewById(R.id.user_image));
    }

    TextView userView = (TextView) row.getTag(R.id.user);

    userView.setText(user.getName());
    userView.setTypeface(amenTypeThin);
    ImageView userImage = (ImageView) row.getTag(R.id.user_image);

    String pictureUrl = user.getPhoto();
    if (TextUtils.isEmpty(pictureUrl)) {
      pictureUrl = user.getPicture();
      if (!TextUtils.isEmpty(pictureUrl)) {
        pictureUrl = pictureUrl + "?type=normal";
      }
    }
    Log.d("UserListAdapter", "pictureUrl: " + pictureUrl);
    userImage.setImageResource(R.drawable.placeholder);
    userImage.setTag(pictureUrl);

    return row;
  }
}
