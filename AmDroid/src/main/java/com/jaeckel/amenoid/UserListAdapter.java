package com.jaeckel.amenoid;

import java.util.List;

import com.jaeckel.amenoid.api.model.User;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.util.Log;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * User: biafra
 * Date: 10/15/11
 * Time: 4:39 AM
 */
public class UserListAdapter extends ArrayAdapter<User> {

  private LayoutInflater inflater;
  private Typeface       amenTypeThin;
  private Typeface       amenTypeBold;

  private              int    USER_ID = 112343253;
  private static final String TAG     = UserListAdapter.class.getSimpleName();

  public UserListAdapter(Context context, int textViewResourceId, List<User> objects) {
    super(context, textViewResourceId, objects);
    inflater = LayoutInflater.from(context);
    amenTypeThin = AmenoidApp.getInstance().getAmenTypeThin();
    amenTypeBold = AmenoidApp.getInstance().getAmenTypeBold();
  }


  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    View row = convertView;
    final User user = getItem(position);

    if (row == null) {
      row = inflater.inflate(R.layout.list_item_user, parent, false);
      row.setTag(R.id.user, row.findViewById(R.id.user));
      row.setTag(R.id.user_image, row.findViewById(R.id.user_image));
//      row.setTag(R.id.follow_button, row.findViewById(R.id.follow_button));

    }

//    row.setTag(USER_ID, user.getId());
//    final long userId = user.getId();
//
//    Button followButton = (Button) row.getTag(R.id.follow_button);
//    followButton.setVisibility(View.GONE);
//    if (AmenoidApp.getInstance().isSignedIn()) {
//
//      followButton.setVisibility(View.VISIBLE);
//      if (user.getFollowing()) {
//        followButton.setText("unfollow");
//        followButton.setOnClickListener(new View.OnClickListener() {
//          @Override public void onClick(View view) {
//            Log.d(TAG, "unfollow clicked for user: " + userId);
//          }
//        });
//      } else {
//        followButton.setText("follow");
//        followButton.setOnClickListener(new View.OnClickListener() {
//          @Override public void onClick(View view) {
//            Log.d(TAG, "follow clicked for user: " + userId);
//          }
//        });
//      }
//
//    }
//    row.setClickable(true);

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
