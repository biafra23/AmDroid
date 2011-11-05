package com.jaeckel.amenoid;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.api.model.User;
import com.jaeckel.amenoid.api.model.UserInfo;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.cwac.cache.SimpleWebImageCache;
import com.jaeckel.amenoid.cwac.thumbnail.ThumbnailBus;
import com.jaeckel.amenoid.cwac.thumbnail.ThumbnailMessage;
import com.jaeckel.amenoid.statement.ChooseStatementTypeActivity;
import com.jaeckel.amenoid.util.AmenLibTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: biafra
 * Date: 10/15/11
 * Time: 5:39 AM
 */
public class UserDetailActivity extends ListActivity {

  private static String TAG = "UserDetailActivity";

  private User        currentUser;
  private AmenService service;
  private AmenAdapter adapter;
  private Drawable    userImage;
  private ListView    list;
  private ProgressBar progressBar;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    Log.d(TAG, "onCreate");

    service = AmenoidApp.getInstance().getService();

    setContentView(R.layout.user);
    setTitle("Amenoid/Userdetails");

    progressBar = (ProgressBar) findViewById(R.id.progress_listview);

    list = (ListView) findViewById(android.R.id.list);
    View header = getLayoutInflater().inflate(R.layout.user_header, null, false);
    list.addHeaderView(header);

    Intent startingIntent = getIntent();

    currentUser = startingIntent.getParcelableExtra(Constants.EXTRA_USER);

    new AmenForUserTask(this).execute(currentUser.getId());

    new UserInfoTask(this).execute(currentUser.getId());

    TextView userName = (TextView) findViewById(R.id.name);
    userName.setText(currentUser.getName());
    final TextView follow = (TextView) findViewById(R.id.follow);

    follow.setText("Following?");
    follow.setBackgroundColor(Color.GRAY);

    TextView followers = (TextView) findViewById(R.id.followers);
    followers.setText("? Followers");

    TextView following = (TextView) findViewById(R.id.following);
    following.setText("? Following");

    adapter = new AmenAdapter(UserDetailActivity.this, android.R.layout.simple_list_item_1, new ArrayList<Amen>());
    setListAdapter(adapter);


  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {

    Log.d(TAG, "Selected position: " + position);
    if (position > 0) {
      Amen amen = (Amen) getListAdapter().getItem(position - 1);

      Log.d(TAG, "Selected Amen: " + amen);

      Intent intent = new Intent(this, AmenDetailActivity.class);
      intent.putExtra(Constants.EXTRA_AMEN, amen);
      startActivity(intent);
    }
  }

  //
  // AmenForUserTask
  //
  private class AmenForUserTask extends AmenLibTask<Long, Integer, List<Amen>> {

    public AmenForUserTask(Context context) {
      super(context);
    }

    protected List<Amen> wrappedDoInBackground(Long... urls) throws IOException {

      List<Amen> amen = service.getAmenForUser(currentUser.getId());

      return amen;
    }

    @Override
    protected void onPreExecute() {
    }

    protected void wrappedOnPostExecute(List<Amen> result) {

      if (result != null) {
        adapter = new AmenAdapter(UserDetailActivity.this, android.R.layout.simple_list_item_1, result);
        setListAdapter(adapter);

      }

      progressBar.setVisibility(View.GONE);
      list.setVisibility(View.VISIBLE);
    }
  }

  //
  // UserInfoTask
  //
  private class UserInfoTask extends AmenLibTask<Long, Integer, UserInfo> {

    public UserInfoTask(Context context) {
      super(context);
    }

    protected UserInfo wrappedDoInBackground(Long... urls) throws IOException {

      final UserInfo userInfo = service.getUserInfo(currentUser.getId());

      final SimpleWebImageCache<ThumbnailBus, ThumbnailMessage> cache = AmenoidApp.getInstance().getCache();
      String pictureUrl = userInfo.getPhoto();
      if (TextUtils.isEmpty(pictureUrl)) {
        pictureUrl = userInfo.getPicture();
        if (pictureUrl != null) {
          pictureUrl += "?type=normal";
        }
      }
      Log.d("UserDetailActivity", "pictureUrl: " + pictureUrl);
      userImage = cache.get(pictureUrl);
      return userInfo;
    }

    protected void wrappedOnPostExecute(final UserInfo userInfo) {

      if (userInfo != null) {

        TextView userName = (TextView) findViewById(R.id.name);
        userName.setText(userInfo.getName());
        final TextView follow = (TextView) findViewById(R.id.follow);

        if (userInfo.getFollowing() != null && userInfo.getFollowing()) {
          follow.setBackgroundColor(Color.CYAN);
          follow.setText("Following");
        } else {
          follow.setBackgroundColor(Color.GRAY);
        }

        follow.setOnClickListener(new View.OnClickListener() {
          public void onClick(View view) {
            if (userInfo.getFollowing()) {
              new UnFollowTask(UserDetailActivity.this).execute(currentUser);

//              service.unfollow(currentUser);
              follow.setBackgroundColor(Color.GRAY);
            } else {
              new FollowTask(UserDetailActivity.this).execute(currentUser);
//              service.follow(currentUser);
              follow.setBackgroundColor(Color.CYAN);
            }
          }
        });

        TextView followers = (TextView) findViewById(R.id.followers);
        followers.setText(userInfo.getFollowersCount() + " Followers");

        TextView following = (TextView) findViewById(R.id.following);
        following.setText(userInfo.getFollowingCount() + " Following");


        ImageView userImageView = (ImageView) findViewById(R.id.user_image);
        userImageView.setImageDrawable(userImage);
      }

    }
  }

  //
  // FollowTask
  //
  private class FollowTask extends AmenLibTask<User, Integer, Boolean> {

    public FollowTask(Context context) {
      super(context);
    }

    protected Boolean wrappedDoInBackground(User... urls) throws IOException {

      return service.follow(currentUser);
    }

    protected void wrappedOnPostExecute(final Boolean success) {

      if (success != null && success) {
        final TextView follow = (TextView) findViewById(R.id.follow);
        follow.setBackgroundColor(Color.CYAN);
      }

    }
  }

  //
  // UnFollowTask
  //
  private class UnFollowTask extends AmenLibTask<User, Integer, Boolean> {

    public UnFollowTask(Context context) {
      super(context);
    }

    protected Boolean wrappedDoInBackground(User... urls) throws IOException {

      return service.unfollow(currentUser);
    }

    protected void wrappedOnPostExecute(final Boolean success) {

      if (success != null && success) {
        final TextView follow = (TextView) findViewById(R.id.follow);
        follow.setBackgroundColor(Color.GRAY);
      }
    }
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);

    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_user_detail, menu);
    return true;
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);

    switch (item.getItemId()) {

      case R.id.timeline:
        startActivity(new Intent(this, AmenListActivity.class));
        return true;

      case R.id.amen:
        startActivity(new Intent(this, ChooseStatementTypeActivity.class));
        return true;
    }

    return false;
  }
}