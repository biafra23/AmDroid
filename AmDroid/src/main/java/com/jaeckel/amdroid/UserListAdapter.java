package com.jaeckel.amdroid;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.jaeckel.amdroid.api.model.User;

import java.util.List;

/**
 * User: biafra
 * Date: 10/15/11
 * Time: 4:39 AM
 */
public class UserListAdapter extends ArrayAdapter<User> {

  private LayoutInflater inflater;

  public UserListAdapter(Context context, int textViewResourceId, List<User> objects) {
    super(context, textViewResourceId, objects);
    inflater = LayoutInflater.from(context);

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

    ImageView userImage = (ImageView) row.getTag(R.id.user_image);

    String pictureUrl;
    if (TextUtils.isEmpty(user.getPhoto())) {
      pictureUrl = user.getPicture();
    } else {
      pictureUrl = user.getPhoto();
    }
    if (!TextUtils.isEmpty(pictureUrl)) {
      pictureUrl = pictureUrl + "?type=normal";
    }

    userImage.setImageResource(R.drawable.placeholder);
    userImage.setTag(pictureUrl);

    return row;
  }
}
