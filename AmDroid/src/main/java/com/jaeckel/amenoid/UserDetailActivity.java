package com.jaeckel.amenoid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.api.model.User;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.cwac.cache.SimpleWebImageCache;
import com.jaeckel.amenoid.cwac.endless.EndlessAdapter;
import com.jaeckel.amenoid.cwac.thumbnail.ThumbnailAdapter;
import com.jaeckel.amenoid.cwac.thumbnail.ThumbnailBus;
import com.jaeckel.amenoid.cwac.thumbnail.ThumbnailMessage;
import com.jaeckel.amenoid.statement.ChooseStatementTypeActivity;
import com.jaeckel.amenoid.util.AmenLibTask;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import com.jaeckel.amenoid.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * User: biafra
 * Date: 10/15/11
 * Time: 5:39 AM
 */
public class UserDetailActivity extends SherlockListActivity {

  private static String TAG = "UserDetailActivity";

  private User                   currentUser;
  private AmenService            service;
  private AmenListAdapter        adapter;
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
  private TextView follow;
  private TextView following;
  private TextView followers;
  private TextView amenReceived;
  private TextView originalAmen;
  private TextView amenGiven;
  private TextView amenScore;
  private TextView accountCreated;
  private static final int[] IMAGE_IDS = {R.id.media_photo};


  @Override
  public boolean onSearchRequested() {
    return super.onSearchRequested();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

  }

  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    amenTypeThin = AmenoidApp.getInstance().getAmenTypeThin();
    amenTypeBold = AmenoidApp.getInstance().getAmenTypeBold();

    service = AmenoidApp.getInstance().getService();

    setContentView(R.layout.user);

    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle("Userdetails");


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
      String currentUserName = startingIntent.getStringExtra(Constants.EXTRA_USER_ID_STRING);

      if (currentUserId != -1L) {
        new AmenForUserTask(this).execute(currentUserId);
        new UserInfoTask(this).execute(currentUserId);
      } else if (!TextUtils.isEmpty(currentUserName)) {

        new AmenForUserNameTask(this).execute(currentUserName);
        new UserNameInfoTask(this).execute(currentUserName);

      } else {
        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
      }
    }

    TextView userName = (TextView) findViewById(R.id.name);
    userName.setTypeface(amenTypeBold);
    if (currentUser != null) {
      userName.setText(currentUser.getName());
      actionBar.setSubtitle(currentUser.getName());
    }
    follow = (TextView) findViewById(R.id.follow_indicator);

    follow.setText("Following?");
    follow.setTypeface(amenTypeThin);
    follow.setBackgroundColor(Color.GRAY);

    followers = (TextView) findViewById(R.id.followers);
    followers.setTypeface(amenTypeThin);
    followers.setText("? Followers");

    following = (TextView) findViewById(R.id.following);
    following.setTypeface(amenTypeThin);
    following.setText("? Following");

    amenReceived = (TextView) findViewById(R.id.amen_received);
    amenReceived.setTypeface(amenTypeThin);

    originalAmen = (TextView) findViewById(R.id.original_amen);
    originalAmen.setTypeface(amenTypeThin);

    amenGiven = (TextView) findViewById(R.id.amen_given);
    amenGiven.setTypeface(amenTypeThin);

    amenScore = (TextView) findViewById(R.id.amen_score);
    amenScore.setTypeface(amenTypeThin);

    accountCreated = (TextView) findViewById(R.id.account_created);
    accountCreated.setTypeface(amenTypeThin);

    adapter = new AmenListAdapter(UserDetailActivity.this, R.layout.list_item_amen_no_pic, new ArrayList<Amen>());
    ThumbnailAdapter thumbs = new ThumbnailAdapter(UserDetailActivity.this, adapter, AmenoidApp.getInstance().getCache(), IMAGE_IDS);

    setListAdapter(thumbs);


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
          adapter = new AmenListAdapter(UserDetailActivity.this, R.layout.list_item_amen_no_pic, result);
          ThumbnailAdapter thumbs = new ThumbnailAdapter(UserDetailActivity.this, adapter, AmenoidApp.getInstance().getCache(), IMAGE_IDS);

          EndlessWrapperAdapter endless = new EndlessWrapperAdapter(thumbs);

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
  // AmenForUserNameTask
  //
  private class AmenForUserNameTask extends AmenLibTask<String, Integer, List<Amen>> {
    Activity context;

    public AmenForUserNameTask(Activity context) {
      super(context);
      this.context = context;
    }

    protected List<Amen> wrappedDoInBackground(String... ids) throws IOException {

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
          adapter = new AmenListAdapter(UserDetailActivity.this, R.layout.list_item_amen_no_pic, result);
          ThumbnailAdapter thumbs = new ThumbnailAdapter(UserDetailActivity.this, adapter, AmenoidApp.getInstance().getCache(), IMAGE_IDS);

          EndlessWrapperAdapter endless = new EndlessWrapperAdapter(thumbs);

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
        final TextView follow = (TextView) findViewById(R.id.follow_indicator);

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
            toggleFollowing(user, follow);
          }
        });

        followers.setText(user.getFollowersCount() + " Followers");
        following.setText(user.getFollowingCount() + " Following");

        amenReceived.setText("Amen received: " + user.getReceivedAmenCount());

        amenGiven.setText("Amen given: " + user.getGivenAmenCount());

        originalAmen.setText("Original Amen: " + user.getCreatedStatementsCount());

        amenScore.setText("Amen Score: " + ((float) user.getReceivedAmenCount() / (float) user.getCreatedStatementsCount()));

        accountCreated.setText("Account created: " + AmenDetailActivity.format(user.getCreatedAt()));

        ImageView userImageView = (ImageView) findViewById(R.id.user_image);
        userImageView.setImageDrawable(userImage);

        currentUser = user;

        unfollowMenu.setVisible(meIsFollowing);
        followMenu.setVisible(!meIsFollowing);


      }

    }
  }

  private void toggleFollowing(User user, View follow) {
    if (user.getFollowing()) {
      new UnFollowTask(this).execute(user);

//              service.unfollow(currentUser);
      follow.setBackgroundColor(Color.GRAY);
    } else {
      new FollowTask(this).execute(user);
//              service.follow(currentUser);
      follow.setBackgroundColor(Color.CYAN);
    }
  }

  //
// UserInfoTask
//
  private class UserNameInfoTask extends AmenLibTask<String, Integer, User> {

    public UserNameInfoTask(Activity context) {
      super(context);
    }

    protected User wrappedDoInBackground(String... ids) throws IOException {

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
        final TextView follow = (TextView) findViewById(R.id.follow_indicator);

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
            toggleFollowing(user, follow);
          }
        });

        followers.setText(user.getFollowersCount() + " Followers");
        following.setText(user.getFollowingCount() + " Following");

        amenReceived.setText("Amen received: " + user.getReceivedAmenCount());

        amenGiven.setText("Amen given: " + user.getGivenAmenCount());

        originalAmen.setText("Original Amen: " + user.getCreatedStatementsCount());
        if (user.getReceivedAmenCount() != null && user.getCreatedStatementsCount() != null) {
          amenScore.setText("Amen Score: " + ((float) user.getReceivedAmenCount() / (float) user.getCreatedStatementsCount()));
        } else {
          amenScore.setText("Amen Score: 0");
        }

        accountCreated.setText("Account created: " + AmenDetailActivity.format(user.getCreatedAt()));

        ImageView userImageView = (ImageView) findViewById(R.id.user_image);
        userImageView.setImageDrawable(userImage);

        currentUser = user;
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

    protected Boolean wrappedDoInBackground(User... users) throws IOException {

      if (users != null && users.length > 0 && users[0] != null) {

        boolean result = service.follow(users[0]);
        if (result) {
          meIsFollowing = true;
          return result;
        }
      }
      return false;
    }

    protected void wrappedOnPostExecute(final Boolean success) {

      if (success != null && success) {
        final TextView follow = (TextView) findViewById(R.id.follow_indicator);
        if (meIsFollowing) {
          follow.setBackgroundColor(Color.CYAN);

        } else {
          follow.setBackgroundColor(Color.GRAY);

        }

        unfollowMenu.setVisible(meIsFollowing);
        followMenu.setVisible(!meIsFollowing);

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

    protected Boolean wrappedDoInBackground(User... users) throws IOException {
      if (users != null && users.length > 0 && users[0] != null) {

        boolean result = service.unfollow(users[0]);
        if (result) {
          meIsFollowing = false;
          return result;
        }
      }
      return false;
    }

    protected void wrappedOnPostExecute(final Boolean success) {

      if (success != null && success) {
        final TextView follow = (TextView) findViewById(R.id.follow_indicator);
        if (meIsFollowing) {
          follow.setBackgroundColor(Color.CYAN);
        } else {
          follow.setBackgroundColor(Color.GRAY);

        }

        unfollowMenu.setVisible(meIsFollowing);
        followMenu.setVisible(!meIsFollowing);


      }
    }

  }

  public boolean onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);
    Log.d(TAG, "onPrepareOptionsMenu");

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

  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    Log.d(TAG, "onCreateOptionsMenu");

    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.menu_user_detail, menu);

    return true;
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);

    switch (item.getItemId()) {

      case android.R.id.home: {
        final Intent amenListIntent = new Intent(this, AmenListActivity.class);
        amenListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(amenListIntent);
        return true;
      }

      case R.id.amen:
        startActivity(new Intent(this, ChooseStatementTypeActivity.class));
        return true;
      case R.id.follow:
        new FollowTask(UserDetailActivity.this).execute(currentUser);
        return true;
      case R.id.unfollow:
        new UnFollowTask(UserDetailActivity.this).execute(currentUser);
        return true;
    }

    return false;
  }


  class EndlessWrapperAdapter extends EndlessAdapter {

    EndlessWrapperAdapter(ThumbnailAdapter amenAdapter) {

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
