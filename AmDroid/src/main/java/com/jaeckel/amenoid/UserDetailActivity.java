package com.jaeckel.amenoid;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import android.widget.Toast;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.api.model.User;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.cwac.cache.SimpleWebImageCache;
import com.jaeckel.amenoid.cwac.endless.EndlessAdapter;
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

  private User                   currentUser;
  private AmenService            service;
  private AmenAdapter            adapter;
  private Drawable               userImage;
  private ListView               list;
  private ProgressBar            progressBar;
  private Typeface               amenTypeThin;
  private Typeface               amenTypeBold;
  private EndlessLoaderAsyncTask endlessTask;
  private boolean stopAppending = false;
  private MenuItem followMenu;
  private MenuItem unfollowMenu;
  private boolean meIsFollowing = false;

  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    Log.d(TAG, "onCreate");

    amenTypeThin = AmenoidApp.getInstance().getAmenTypeThin();
    amenTypeBold = AmenoidApp.getInstance().getAmenTypeBold();

    service = AmenoidApp.getInstance().getService();

    setContentView(R.layout.user);
    setTitle("Amenoid/Userdetails");

    progressBar = (ProgressBar) findViewById(R.id.progress_listview);

    list = (ListView) findViewById(android.R.id.list);
    View header = getLayoutInflater().inflate(R.layout.user_header, null, false);
    list.addHeaderView(header);


    Intent startingIntent = getIntent();

    currentUser = startingIntent.getParcelableExtra(Constants.EXTRA_USER);
    if (currentUser != null) {
      new AmenForUserTask(this).execute(currentUser.getId());
      new UserInfoTask(this).execute(currentUser.getId());
    } else {
      Long currentUserId = startingIntent.getLongExtra(Constants.EXTRA_USER_ID, -1L);
      if (currentUserId != -1L) {
        new AmenForUserTask(this).execute(currentUserId);
        new UserInfoTask(this).execute(currentUserId);
      } else {
        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
      }
    }

    TextView userName = (TextView) findViewById(R.id.name);
    userName.setTypeface(amenTypeBold);
    if (currentUser != null) {
      userName.setText(currentUser.getName());

    }
    final TextView follow = (TextView) findViewById(R.id.follow);

    follow.setText("Following?");
    follow.setTypeface(amenTypeThin);
    follow.setBackgroundColor(Color.GRAY);

    TextView followers = (TextView) findViewById(R.id.followers);
    followers.setTypeface(amenTypeThin);
    followers.setText("? Followers");

    TextView following = (TextView) findViewById(R.id.following);
    following.setTypeface(amenTypeThin);
    following.setText("? Following");

    adapter = new AmenAdapter(UserDetailActivity.this, android.R.layout.simple_list_item_1, new ArrayList<Amen>());
    setListAdapter(adapter);


  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {

    Log.d(TAG, "Selected position: " + position);
    if (position > 0 && position < getListAdapter().getCount()) {

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
    Activity context;

    public AmenForUserTask(Activity context) {
      super(context);
      this.context = context;
    }

    protected List<Amen> wrappedDoInBackground(Long... ids) throws IOException {

      List<Amen> amen = service.getAmenForUser(ids[0], 0L);

      return amen;
    }

    @Override
    protected void onPreExecute() {
    }

    protected void wrappedOnPostExecute(List<Amen> result) {
      if (context.hasWindowFocus())
        if (result != null) {
          if (result.size() == 0) {
            stopAppending = true;
          }
          adapter = new AmenAdapter(UserDetailActivity.this, android.R.layout.simple_list_item_1, result);

          EndlessWrapperAdapter endless = new EndlessWrapperAdapter(adapter);

          setListAdapter(endless);
//        setListAdapter(adapter);

        } else {
          stopAppending = true;
        }

      progressBar.setVisibility(View.GONE);
      list.setVisibility(View.VISIBLE);
    }
  }


  //
// UserInfoTask
//
  private class UserInfoTask extends AmenLibTask<Long, Integer, User> {

    public UserInfoTask(Activity context) {
      super(context);
    }

    protected User wrappedDoInBackground(Long... ids) throws IOException {

      final User user = service.getUserForId(ids[0]);

      final SimpleWebImageCache<ThumbnailBus, ThumbnailMessage> cache = AmenoidApp.getInstance().getCache();
      String pictureUrl = user.getPhoto();
      if (TextUtils.isEmpty(pictureUrl)) {
        pictureUrl = user.getPicture();
        if (pictureUrl != null) {
          pictureUrl += "?type=normal";
        }
      }
      Log.d("UserDetailActivity", "pictureUrl: " + pictureUrl);
      userImage = cache.get(pictureUrl);
      currentUser = user;

      return user;
    }

    protected void wrappedOnPostExecute(final User user) {

      if (user != null) {

        TextView userName = (TextView) findViewById(R.id.name);
        userName.setText(user.getName());
        final TextView follow = (TextView) findViewById(R.id.follow);

        if (user.getFollowing() != null && user.getFollowing()) {
          follow.setBackgroundColor(Color.CYAN);
          follow.setText("Following");
          meIsFollowing = true;

        } else {
          follow.setBackgroundColor(Color.GRAY);
          meIsFollowing = false;

        }

        follow.setOnClickListener(new View.OnClickListener() {
          public void onClick(View view) {
            if (user.getFollowing()) {
              new UnFollowTask(UserDetailActivity.this).execute(user);

//              service.unfollow(currentUser);
              follow.setBackgroundColor(Color.GRAY);
            } else {
              new FollowTask(UserDetailActivity.this).execute(user);
//              service.follow(currentUser);
              follow.setBackgroundColor(Color.CYAN);
            }
          }
        });

        TextView followers = (TextView) findViewById(R.id.followers);
        followers.setText(user.getFollowersCount() + " Followers");

        TextView following = (TextView) findViewById(R.id.following);
        following.setText(user.getFollowingCount() + " Following");


        ImageView userImageView = (ImageView) findViewById(R.id.user_image);
        userImageView.setImageDrawable(userImage);
      }

    }
  }

  //
// FollowTask
//
  private class FollowTask extends AmenLibTask<User, Integer, Boolean> {

    public FollowTask(Activity context) {
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

    public UnFollowTask(Activity context) {
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
    followMenu = menu.findItem(R.id.follow);
    unfollowMenu = menu.findItem(R.id.unfollow);

    if (!AmenoidApp.getInstance().isSignedIn()) {
      MenuItem amenSth = menu.findItem(R.id.amen);
      amenSth.setEnabled(false);

      followMenu.setEnabled(false);
      unfollowMenu.setEnabled(false);

      followMenu.setVisible(!meIsFollowing);
      unfollowMenu.setVisible(meIsFollowing);

    } else {

      followMenu.setEnabled(true);
      unfollowMenu.setEnabled(true);

      unfollowMenu.setVisible(meIsFollowing);
      followMenu.setVisible(!meIsFollowing);

    }
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


  class EndlessWrapperAdapter extends EndlessAdapter {

    EndlessWrapperAdapter(AmenAdapter amenAdapter) {

      super(UserDetailActivity.this, amenAdapter, R.layout.pending);
    }

    @Override
    protected boolean cacheInBackground() throws Exception {
      return !stopAppending;
    }


    @Override
    protected void appendCachedData() {
      if (!stopAppending && adapter.getCount() > 0) {

        if (endlessTask != null) {
          AsyncTask.Status status = endlessTask.getStatus();
          if (status == AsyncTask.Status.FINISHED) {
            endlessTask = new EndlessLoaderAsyncTask(UserDetailActivity.this);
            endlessTask.execute(adapter.getItem(adapter.getCount() - 1).getId());
          }
        } else {
          endlessTask = new EndlessLoaderAsyncTask(UserDetailActivity.this);
          endlessTask.execute(adapter.getItem(adapter.getCount() - 1).getId());

        }
      }
    }


  }

//
// EndlessLoaderAsyncTask
//

  private class EndlessLoaderAsyncTask extends AmenLibTask<Long, Integer, List<Amen>> {
    Activity context;

    public EndlessLoaderAsyncTask(Activity context) {
      super(context);
    }

    @Override
    protected List<Amen> wrappedDoInBackground(Long... longs) throws IOException {

      Long lastAmenId = longs[0];
      Log.d(TAG, "Running on Thread: " + Thread.currentThread().getName());
      Log.d(TAG, "       lastAmenId: " + lastAmenId);

      if (!isCancelled()) {
        if (currentUser != null) {
          List<Amen> amens = service.getAmenForUser(currentUser.getId(), lastAmenId);
          return amens;  //To change body of implemented methods use File | Settings | File Templates.
        }
      }
      return null;
    }

    @Override
    protected void wrappedOnPostExecute(List<Amen> amens) {

      if (amens != null) {
        for (Amen amen : amens) {
//        Log.d(TAG, "Adding amen: " + amen);
          adapter.add(amen);
        }
        Log.d(TAG, "Amens.size: " + amens.size());
      } else {
        Log.d(TAG, "Amens: " + amens);
      }

      if (amens == null || amens.size() == 0) {
        stopAppending = true;
      }

    }

    @Override
    protected void onCancelled() {
      Log.d(TAG, "cancelled");
    }


  }
}
