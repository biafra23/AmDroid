package com.jaeckel.amenoid;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.api.model.DateSerializer;
import com.jaeckel.amenoid.api.model.Statement;
import com.jaeckel.amenoid.api.model.User;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.cwac.endless.EndlessAdapter;
import com.jaeckel.amenoid.cwac.thumbnail.ThumbnailAdapter;
import com.jaeckel.amenoid.statement.ChooseStatementTypeActivity;
import com.jaeckel.amenoid.util.AmenLibTask;
import com.jaeckel.amenoid.widget.PullToRefreshListView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.jaeckel.amenoid.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

//import com.jaeckel.amenoid.db.AmenDao;

//public class AmenListActivity extends FragmentActivity {
public class AmenListActivity extends SherlockListActivity {

  private static       String TAG                       = "AmenListActivity";
  final private static int    PROGRESS_DIALOG_ID        = 0;
  public static final  int    REQUEST_CODE_PREFERENCES  = 1001;
  public static final  int    REQUEST_CODE_AMEN_DETAILS = 1002;

  final int pageSize = 20;

  private AmenService service;
  private static final int[] IMAGE_IDS = {R.id.user_image, R.id.media_photo, R.id.objekt_photo};
  private AmenListAdapter   amenListAdapter;
  private SharedPreferences prefs;
  private Gson gson = new GsonBuilder()
    .registerTypeAdapter(Date.class, new DateSerializer())
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .serializeNulls()
    .create();
  private EndlessLoaderAsyncTask endlessTask;
  private        int     feedType      = AmenService.FEED_TYPE_FOLLOWING;
  private        boolean stopAppending = false;
  //  private AlertDialog enterCredentialsDialog;
  private static boolean shouldRefresh = false;

  //private AmenDao amenDao;

  @Override
  public boolean onSearchRequested() {
    return super.onSearchRequested();
  }


  /**
   * Called when the activity is first created.
   *
   * @param savedInstanceState If the activity is being re-initialized after
   *                           previously being shut down then this Bundle contains the data it most
   *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.v(TAG, "onCreate");

/*
 4102       AmenListActivity  D  id: 137120
  4102       AmenListActivity  D  created_at: 1326619879000
  4102       AmenListActivity  D  user_id: 22653
  4102       AmenListActivity  D  first_posted_at: null
  4102       AmenListActivity  D  topic_id: 67242
  4102       AmenListActivity  D  description: Radio Show
  4102       AmenListActivity  D  scope: for Sunday Mornings
  4102       AmenListActivity  D  name: Sunny Side Up on FM4

 */
//    AmenDBAdapter dbAdapter = new AmenDBAdapter(this);
//    amenDao = AmenoidApp.getInstance().getAmenDao();
//    Log.i(TAG, "amenDao: " + amenDao);
//
//
//    Cursor c = amenDao.currentAmen();
//
//    if (c != null) {
//      if (c.getCount() > 0) {
//        c.moveToFirst();
//        while (!c.isAfterLast()) {
//          final int idIndex = c.getColumnIndex(AmenDBHelper.KEY_ID);
//          if (idIndex >= 0) {
//
//          }
//          final int createdAtIndex = c.getColumnIndex(AmenDBHelper.KEY_CREATED_AT);
//
//          Log.d(TAG, c.getString(idIndex) + ": "
//                     + c.getLong(createdAtIndex) + " "
//                     + c.getString(c.getColumnIndex(AmenDBHelper.KEY_USER_NAME)) + " "
//                     + c.getLong(c.getColumnIndex(AmenDBHelper.KEY_BEST)) + " "
//                     + c.getLong(c.getColumnIndex(AmenDBHelper.KEY_TOTAL_AMEN_COUNT)) + " "
//                     + c.getString(c.getColumnIndex("objekt_name")) + " is "
//                     + c.getString(c.getColumnIndex(AmenDBHelper.KEY_DESCRIPTION)) + " "
//                     + c.getString(c.getColumnIndex(AmenDBHelper.KEY_SCOPE)) + " "
//
//               );
//
//
//          c.moveToNext();
//        }
//
//      } else {
//        Log.i(TAG, "--------------------- NOTHING FOUND ----------------------------");
//        Log.i(TAG, "Cursor.getCount(): " + c.getCount());
//      }
//    } else {
//      Log.i(TAG, "Cursor: " + c);
//
//    }


    setContentView(R.layout.main);

    prefs = PreferenceManager.getDefaultSharedPreferences(this);

    // default feedType is following
    feedType = getIntent().getIntExtra(Constants.EXTRA_FEED_TYPE, AmenService.FEED_TYPE_FOLLOWING);

    if (!AmenoidApp.getInstance().isSignedIn() && feedType == AmenService.FEED_TYPE_FOLLOWING) {

      // if not signed in default to popular. new is gone
      feedType = AmenService.FEED_TYPE_POPULAR;
    }

    final String authToken = readAuthTokenFromPrefs();
    final User me = readMeFromPrefs();
    if (AmenoidApp.DEVELOPER_MODE) {
      Toast.makeText(this, "authToken: " + authToken, Toast.LENGTH_SHORT).show();
    }

    refreshWithCache();


    String title = "";
    if (feedType == AmenService.FEED_TYPE_FOLLOWING) {
      title = "Following";
    } else if (feedType == AmenService.FEED_TYPE_RECENT) {
      title = "New";
    } else if (feedType == AmenService.FEED_TYPE_POPULAR) {
      title = "Popular";
    }
//    setTitle("Timeline: " + title);

    ActionBar bar = getSupportActionBar();
    bar.setSubtitle(title);
    bar.setTitle("Timeline");

    registerForContextMenu(getListView());

//    if (feedType != AmenService.FEED_TYPE_POPULAR) {
    ((PullToRefreshListView) getListView()).setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {

      public void onRefresh() {
        Log.v(TAG, "onRefresh()");
        // Do work to refresh the list here.
        new GetDataTask(AmenListActivity.this).execute();
      }
    });
//    }
  }

//  private AlertDialog createEnterCredentialsDialog() {
//    return new AlertDialog.Builder(this)
//      .setMessage("Please enter your Amen credentials and sign in!")
//      .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//
//        public void onClick(DialogInterface dialogInterface, int i) {
//          startActivityForResult(new Intent(AmenListActivity.this, SettingsActivity.class), REQUEST_CODE_PREFERENCES);
//        }
//      }).create();
//  }

//  private void redirectOnMissingCredentials(String username, String password) {
//    if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
//
//      enterCredentialsDialog = new AlertDialog.Builder(this)
//        .setMessage("Please enter your Amen credentials in the preferences.")
//        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//
//          public void onClick(DialogInterface dialogInterface, int i) {
//            startActivityForResult(new Intent(AmenListActivity.this, SettingsActivity.class), REQUEST_CODE_PREFERENCES);
//          }
//        }).create();
//      enterCredentialsDialog.show();
//    }
//  }


  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent result) {


    if (requestCode == REQUEST_CODE_AMEN_DETAILS) {
      // receive the new currentObjekt
      Log.d(TAG, "onActivityResult | requestCode: REQUEST_CODE_AMEN_DETAILS");

      final Long statementId = result.getLongExtra(Constants.EXTRA_STATEMENT_ID, 0L);

      Log.d(TAG, "onActivityResult | statementId: " + statementId);

      refreshAmenWithStatementId(statementId);

    }
//    refreshWithCache();

  }

  private void refreshAmenWithStatementId(Long statementId) {

    new AmenRefreshTask(this).execute(statementId);
  }

  @Override
  public void onResume() {
    super.onResume();

    if (AmenoidApp.getInstance().isSignedIn()) {
      // signed in

    } else {
      //not signed in

    }


    if (shouldRefresh) {
      refresh();
      shouldRefresh = false;
    }
  }

  private void refresh() {

    service = AmenoidApp.getInstance().getService();

    if (service != null) {
      LoaderAsyncTask loader = new LoaderAsyncTask(this);
      loader.execute();
    } else {
      Log.e(TAG, "Service still " + service);
    }

  }

  private void refreshWithCache() {

    service = AmenoidApp.getInstance().getService();
    if (service != null) {
      CachedLoaderAsyncTask loader = new CachedLoaderAsyncTask(this);
      loader.execute();
    } else {
      if (AmenoidApp.DEVELOPER_MODE) {
        Toast.makeText(this, "refreshWithCache() AmenLibService was null", Toast.LENGTH_SHORT).show();
      }
    }
  }


  @Override
  public void onPause() {
    super.onPause();
  }

  public boolean onPrepareOptionsMenu(Menu menu) {
    Log.d(TAG, "onPrepareOptionsMenu");
    MenuItem following = menu.findItem(R.id.following_menu);
//    MenuItem recent = menu.findItem(R.id.recent_menu);
    MenuItem popular = menu.findItem(R.id.popular_menu);
    MenuItem amenSth = menu.findItem(R.id.amen);
    MenuItem signInOut = menu.findItem(R.id.signin);
//    MenuItem search = menu.findItem(R.id.search_menu_item);

    if (feedType == AmenService.FEED_TYPE_FOLLOWING) {
//      recent.setVisible(true);
      following.setVisible(false);
      popular.setVisible(true);

    } else if (feedType == AmenService.FEED_TYPE_RECENT) {
//      recent.setVisible(false);
      following.setVisible(true);
      popular.setVisible(true);

    } else if (feedType == AmenService.FEED_TYPE_POPULAR) {
//      recent.setVisible(true);
      following.setVisible(true);
      popular.setVisible(false);

    }
    if (!AmenoidApp.getInstance().isSignedIn()) {

      amenSth.setEnabled(false);
      following.setEnabled(false);
//      search.setEnabled(false);
      signInOut.setTitle("Sign in");

    } else {
      amenSth.setEnabled(true);
      following.setEnabled(true);
//      search.setEnabled(true);
      signInOut.setTitle("Sign out");
    }

    return true;
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    Log.d(TAG, "onCreateOptionsMenu");

    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.menu_main, menu);


    return true;
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);

    switch (item.getItemId()) {

      case R.id.signin:
        startActivity(new Intent(this, SettingsActivity.class));

        return true;

      case R.id.refresh:
        refresh();

        return true;
      case R.id.following_menu: {
        Intent intent = new Intent(this, AmenListActivity.class);
        intent.putExtra(Constants.EXTRA_FEED_TYPE, AmenService.FEED_TYPE_FOLLOWING);
        startActivity(intent);
        return true;
      }
//      case R.id.recent_menu: {
//        Intent intent = new Intent(this, AmenListActivity.class);
//        intent.putExtra(Constants.EXTRA_FEED_TYPE, AmenService.FEED_TYPE_RECENT);
//        startActivity(intent);
//
//        return true;
//      }
      case R.id.popular_menu: {
        Intent intent = new Intent(this, AmenListActivity.class);
        intent.putExtra(Constants.EXTRA_FEED_TYPE, AmenService.FEED_TYPE_POPULAR);
        startActivity(intent);

        return true;
      }
      case R.id.amen: {
//        Toast.makeText(this, "Refreshing Amens", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ChooseStatementTypeActivity.class);
        startActivity(intent);
        return true;
      }
      case R.id.about_menu_item: {
        if (AmenoidApp.DEVELOPER_MODE) {
          Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
        return true;
      }
      case R.id.search_menu_item: {
        Log.d(TAG, "R.id.search");

        onSearchRequested();
        return true;
      }
    }

    return false;
  }


  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {

    if (position > 0 && position < getListAdapter().getCount()) {

      Amen amen = (Amen) getListAdapter().getItem(position - 1);

      Log.d(TAG, "Selected Amen: " + amen);

      Intent intent = new Intent(this, AmenDetailActivity.class);
      intent.putExtra(Constants.EXTRA_AMEN, amen);
      startActivityForResult(intent, REQUEST_CODE_AMEN_DETAILS);
    }
  }


  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
//    Log.d(TAG, "onCreateContextMenu");
//
//    MenuInflater inflater = getSupportMenuInflater();
//    inflater.inflate(R.menu.amen_item_menu, menu);
  }

  @Override
  public boolean onContextItemSelected(android.view.MenuItem item) {
    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();


    Amen amen = (Amen) this.getListAdapter().getItem(info.position - 1);
    switch (item.getItemId()) {
      case R.id.open_item: {
        Log.d(TAG, "R.id.open_item");
        Intent intent = new Intent(this, AmenDetailActivity.class);
        intent.putExtra(Constants.EXTRA_AMEN, amen);
        startActivity(intent);
        return true;
      }
//      case R.id.amen_item: {
//        Log.d(TAG, "R.id.amen_item");
//        service.amen(amen.getId());
//        Toast.makeText(this, "Amen'd " + amen.getStatement().getObjekt().getName(), Toast.LENGTH_SHORT).show();
//        return true;
//      }
      case R.id.dispute_item: {
        Log.d(TAG, "R.id.dispute_item");
        Intent intent = new Intent(this, DisputeActivity.class);
        intent.putExtra(Constants.EXTRA_AMEN, amen);
        startActivity(intent);
        return true;
      }
      case R.id.share_item: {
        Log.d(TAG, "R.id.share_item");
        String amenText = amen.getStatement().toDisplayString();

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, amenText + " #getamen https://getamen.com/statements/" + amen.getStatement().getId());
        startActivity(Intent.createChooser(sharingIntent, "Share using"));


        return true;
      }
      case R.id.scoreboard_for_item: {
        Log.d(TAG, "R.id.scoreboard_for_item");

        Intent intent = new Intent(this, ScoreBoardActivity.class);
        intent.putExtra(Constants.EXTRA_TOPIC, amen.getStatement().getTopic());
        startActivity(intent);

        return true;
      }

      case R.id.subject_page: {
        Log.d(TAG, "R.id.subject_page");

        Intent intent = new Intent(this, SubjectPageActivity.class);
        intent.putExtra(Constants.EXTRA_OBJEKT_ID, amen.getStatement().getObjekt().getId());
        startActivity(intent);

        return true;
      }

      default:
        return super.onContextItemSelected(item);
    }

  }

  class EndlessWrapperAdapter extends EndlessAdapter {

    EndlessWrapperAdapter(ThumbnailAdapter thumbnailAdapter) {

      super(AmenListActivity.this, thumbnailAdapter, R.layout.pending);
    }

    @Override
    protected boolean cacheInBackground() throws Exception {

      return !stopAppending && (feedType != AmenService.FEED_TYPE_POPULAR);
    }

    @Override
    protected void appendCachedData() {
      if (!stopAppending && (feedType != AmenService.FEED_TYPE_POPULAR)) {
        if (amenListAdapter.getCount() > 0) {
          final Long lastId = amenListAdapter.getItem(amenListAdapter.getCount() - 1).getId();
          if (endlessTask != null) {
            AsyncTask.Status status = endlessTask.getStatus();
            if (status == AsyncTask.Status.FINISHED) {
              endlessTask = new EndlessLoaderAsyncTask(AmenListActivity.this);
              endlessTask.execute(lastId);
            }
          } else {
            endlessTask = new EndlessLoaderAsyncTask(AmenListActivity.this);
            endlessTask.execute(lastId);

          }
        }
      }
    }
  }

  //
  // EndlessLoaderAsyncTask
  //

  private class EndlessLoaderAsyncTask extends AmenLibTask<Long, Integer, List<Amen>> {

    public EndlessLoaderAsyncTask(Activity context) {
      super(context);
    }

    @Override
    protected List<Amen> wrappedDoInBackground(Long... longs) throws IOException {

      Long lastAmenId = longs[0];
      Log.d(TAG, "Running on Thread: " + Thread.currentThread().getName());
      Log.d(TAG, "       lastAmenId: " + lastAmenId);

      if (!isCancelled()) {
        List<Amen> amens = service.getFeed(lastAmenId, 20, feedType);

//        amenDao.insertOrUpdate(amens);

        return amens;  //To change body of implemented methods use File | Settings | File Templates.
      }

      return null;
    }

    @Override
    protected void wrappedOnPostExecute(List<Amen> amens) {

      if (amens != null) {
        for (Amen amen : amens) {
//        Log.d(TAG, "Adding amen: " + amen);
          amenListAdapter.add(amen);
        }
        if (amens.size() == 0) {
          stopAppending = true;
        }
      }
    }

    @Override
    protected void onCancelled() {
      Log.d(TAG, "cancelled");
    }


  }

  //
  // CachedLoaderAsyncTask
  //
  private class CachedLoaderAsyncTask extends AmenLibTask<Void, Integer, List<Amen>> {

    private ProgressDialog cachedLoadingProgressDialog;

    public CachedLoaderAsyncTask(Activity context) {
      super(context);
    }

    @Override
    protected List<Amen> wrappedDoInBackground(Void... voids) throws IOException {

      List<Amen> amens = new ArrayList<Amen>();

      String newAmensJSON = prefs.getString(Constants.PREFS_LAST_NEW_AMENS + ":" + feedType, null);

      String amensJSON = prefs.getString(Constants.PREFS_LAST_AMENS + ":" + feedType, null);

      if (amensJSON != null && !"[]".equals(amensJSON)) {

        Type collectionType = new TypeToken<List<Amen>>() {
        }.getType();

        if (newAmensJSON != null && !"[]".equals(newAmensJSON)) {
          Log.v(TAG, "Found new amens in prefs: " + newAmensJSON);
          List<Amen> newAmens = gson.fromJson(newAmensJSON, collectionType);

          if (newAmens != null) {
            amens.addAll(newAmens);
          }

        }
        Log.v(TAG, "Found amens in prefs: " + amensJSON);

        final List<Amen> amenList = gson.fromJson(amensJSON, collectionType);
        if (amenList != null) {
          amens.addAll(amenList);
        }

      } else {
        // no cached AMens found. Load from network
        Log.d(TAG, "Loader executing");
        amens = service.getFeed(0, 20, feedType);
//        amenDao.insertOrUpdate(amens);

        saveAmensToPrefs(amens, Constants.PREFS_LAST_AMENS + ":" + feedType);

      }
      return amens;
    }

    @Override
    protected void onPreExecute() {

      cachedLoadingProgressDialog = ProgressDialog.show(AmenListActivity.this, "",
                                                        "Loading Amens. Please wait...", true);

      cachedLoadingProgressDialog.show();

    }

    @Override
    protected void wrappedOnPostExecute(List<Amen> amens) {
      Log.d(TAG, "wrappedOnPostExecute");
      if (amens != null) {
        amenListAdapter = new AmenListAdapter(AmenListActivity.this, R.layout.list_item_amen, amens);
        ThumbnailAdapter thumbs = new ThumbnailAdapter(AmenListActivity.this, amenListAdapter, AmenoidApp.getInstance().getCache(), IMAGE_IDS);

        EndlessWrapperAdapter endless = new EndlessWrapperAdapter(thumbs);

        setListAdapter(endless);

      }

      Log.v(TAG, "hasWindowFocus(): " + hasWindowFocus());

      cachedLoadingProgressDialog.hide();
    }

    @Override
    protected void onCancelled() {
      if (hasWindowFocus() && cachedLoadingProgressDialog != null) {
        cachedLoadingProgressDialog.hide();
      }
    }
  }


  //
  // LoaderAsyncTask
  //
  private class LoaderAsyncTask extends AmenLibTask<Void, Integer, List<Amen>> {

    private boolean stopAppending = false;
    private ProgressDialog loadingProgressDialog;

    public LoaderAsyncTask(Activity context) {
      super(context);
    }

    @Override
    protected List<Amen> wrappedDoInBackground(Void... voids) throws IOException {

      Log.d(TAG, "Loader executing");

      List<Amen> amens = service.getFeed(0, 20, feedType);
//      amenDao.insertOrUpdate(amens);

      saveAmensToPrefs(amens, Constants.PREFS_LAST_AMENS + ":" + feedType);

      return amens;
    }

    @Override
    protected void onPreExecute() {
      loadingProgressDialog = ProgressDialog.show(AmenListActivity.this, "",
                                                  "Loading Amen from Server...", true);
      loadingProgressDialog.show();

    }

    @Override
    protected void wrappedOnPostExecute(List<Amen> amens) {

      if (amens != null) {

        amenListAdapter = new AmenListAdapter(AmenListActivity.this, R.layout.list_item_amen, amens);
        ThumbnailAdapter thumbs = new ThumbnailAdapter(AmenListActivity.this, amenListAdapter, AmenoidApp.getInstance().getCache(), IMAGE_IDS);

        EndlessWrapperAdapter endless = new EndlessWrapperAdapter(thumbs);

        setListAdapter(endless);

      }

      loadingProgressDialog.hide();
    }

    @Override
    protected void onCancelled() {
      Log.d(TAG, "loadingProgressDialog cancelled");
      if (loadingProgressDialog != null) {
        loadingProgressDialog.hide();
      }
    }
  }

//  //
//  // LoginAsyncTask
//  //
//
//  private class LoginAsyncTask extends AmenLibTask<Void, Integer, AmenService> {
//
//    private ProgressDialog loginProgressDialog;
//
//    public LoginAsyncTask(Context context) {
//      super(context);
//    }
//
//    @Override
//    protected void onPreExecute() {
//      if (AmenoidApp.DEVELOPER_MODE) {
//        Toast.makeText(AmenListActivity.this, "LoginAsyncTask.onPreExecute", Toast.LENGTH_SHORT).show();
//      }
//      loginProgressDialog = ProgressDialog.show(AmenListActivity.this, "",
//                                                "Logging in. Please wait...", true);
//      loginProgressDialog.show();
//    }
//
//    @Override
//    protected AmenService wrappedDoInBackground(Void... voids) {
//
//
//      String username = prefs.getString(Constants.PREFS_USER_NAME, null);
//      String password = prefs.getString(Constants.PREFS_PASSWORD, null);
//
//      final AmenService amenService = AmenoidApp.getInstance().getService(username, password);
//      saveAuthTokenToPrefs(amenService.getAuthToken());
//      saveMeToPrefs(amenService.getMe());
//      return amenService;
//    }
//
//    @Override
//    protected void wrappedOnPostExecute(AmenService service) {
//
//      Log.d(TAG, "wrappedOnPostExecute()");
//
//      if (service != null) {
//
//        AmenListActivity.this.service = service;
//
//        refresh();
//      }
//      if (loginProgressDialog != null) {
//        loginProgressDialog.hide();
//      }
//    }
//
//    @Override
//    protected void onCancelled() {
//      Log.d(TAG, "cancelled");
//      if (loginProgressDialog != null) {
//        loginProgressDialog.hide();
//      }
//    }
//  }

  //
  // GetDataTask
  //

  private class GetDataTask extends AmenLibTask<Void, Void, List<Amen>> {

    public GetDataTask(Activity context) {
      super(context);
    }

    @Override
    protected List<Amen> wrappedDoInBackground(Void... voids) throws IOException {
      Log.v(TAG, "doInBackground");
      List<Amen> oldAmens = new ArrayList<Amen>();

      for (int i = 0; i < amenListAdapter.getCount(); i++) {
        oldAmens.add(amenListAdapter.getItem(i));
        Log.v(TAG, "amenListAdapter: "
                   + amenListAdapter.getItem(i).getId() + ": "
                   + amenListAdapter.getItem(i).getStatement().toDisplayString());
      }

      saveAmensToPrefs(oldAmens, Constants.PREFS_LAST_AMENS + ":" + feedType);

      List<Amen> filteredAmens;
      List<Amen> newAmens = new ArrayList<Amen>();

//      do {
      List<Amen> amens = service.getFeed(0, pageSize, feedType);

      filteredAmens = filterNewAmens(oldAmens, amens);
      newAmens.addAll(filteredAmens);

//      } while (filteredAmens.size() == pageSize);

      // amenDao.insertOrUpdate(newAmens);
      saveAmensToPrefs(newAmens, Constants.PREFS_LAST_NEW_AMENS + ":" + feedType);
      return newAmens;
    }


    private List<Amen> filterNewAmens(List<Amen> oldAmens, List<Amen> amens) {

      ArrayList<Amen> result = new ArrayList<Amen>();

      for (Amen amen : amens) {
        boolean foundAmen = false;
        for (Amen oldAmen : oldAmens) {
          if (amen.getId().equals(oldAmen.getId())) {

            foundAmen = true;
          }
        }
        if (!foundAmen) {
          Log.v(TAG, "new results -> amen: " + amen.getId() + ": " + amen.getStatement().toDisplayString());
          result.add(amen);
        }
      }
      return result;
    }

    @Override
    protected void wrappedOnPostExecute(List<Amen> result) {

      if (result != null) {
        if (result.size() == pageSize || feedType == AmenService.FEED_TYPE_POPULAR) {
          // clear adapter
          amenListAdapter.clear();
        }
        if (result.size() > 0) {

          for (int i = result.size() - 1; i >= 0; i--) {
            Log.v(TAG, "new results -> amen: " + result.get(i).getId() + " name: " + result.get(i).getStatement().getObjekt().getName());
            amenListAdapter.insert(result.get(i), 0);
          }
          amenListAdapter.notifyDataSetChanged();
        }

        // Call onRefreshComplete when the list has been refreshed.
        ((PullToRefreshListView) getListView()).onRefreshComplete();
      }
    }
  }

  private void saveAmensToPrefs(List<Amen> amens, String prefsKey) {
    SharedPreferences.Editor editor = prefs.edit();

//    if (amens.size() < maxSave || maxSave == 0) {
//      maxSave = amens.size();
//    }
    String newAmensJSON = gson.toJson(amens);
    Log.v(TAG, "prefsKey: " + prefsKey + ": " + newAmensJSON);
    editor.putString(prefsKey, newAmensJSON);
    editor.commit();
  }

  private void saveAuthTokenToPrefs(String authToken) {
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString(Constants.PREFS_AUTH_TOKEN, authToken);
    editor.commit();
  }

  private String readAuthTokenFromPrefs() {
    return prefs.getString(Constants.PREFS_AUTH_TOKEN, null);
  }

  private void saveMeToPrefs(User me) {
    SharedPreferences.Editor editor = prefs.edit();
    String meString = gson.toJson(me);
    editor.putString(Constants.PREFS_ME, meString);
    editor.commit();
  }

  private User readMeFromPrefs() {
    final String prefsString = prefs.getString(Constants.PREFS_ME, null);
    User u = null;
    if (prefsString != null) {
      u = gson.fromJson(prefsString, User.class);
    }

    return u;
  }

  private class AmenRefreshTask extends AmenLibTask<Long, Integer, Amen> {

    public AmenRefreshTask(Activity context) {
      super(context);
    }

    @Override
    protected Amen wrappedDoInBackground(Long... statementIds) throws IOException {
      //1. find statement in adapter

      //2. reload statement froms server

      //3. update statement in Adapter

      Amen result = null;
      for (Long statementId : statementIds) {


        for (int i = 0; i < amenListAdapter.getCount(); i++) {

          Amen amen = amenListAdapter.getItem(i);

          if (amen.getStatement().getId().longValue() == statementId.longValue()) {

            Log.d(TAG, "Reloading Statement: " + amen.getStatement().getId());

            Statement statement = service.getStatementForId(amen.getStatement().getId());
            if (statement != null) {
              Log.d(TAG, "Found Statement: " + statement);

              amen.setStatement(statement);
            }

          }

        }

      }
      return result;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void wrappedOnPostExecute(Amen result) {
    }

  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    Log.d(TAG, "onConfigurationChanged(): " + newConfig);
//    setContentView(R.layout.myLayout);
  }


//  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
//
////    Log.d(TAG, "Preferences changed!: " + s);
//
////    Toast.makeText(this, "Preferences change detected", Toast.LENGTH_SHORT).show();
//
//    if (s.equals(Constants.PREFS_USER_NAME) || s.equals(Constants.PREFS_PASSWORD)) {
//      Toast.makeText(this, "Removing auth_token", Toast.LENGTH_SHORT).show();
//
//      resetAccount = true;
//
//
//    }
//  }

//  private void cleanPrefs(SharedPreferences prefs) {
//
//    SharedPreferences.Editor editor = prefs.edit();
//    editor.remove(Constants.PREFS_AUTH_TOKEN)
//          .remove(Constants.PREFS_LAST_AMENS + ":" + AmenService.FEED_TYPE_FOLLOWING)
//          .remove(Constants.PREFS_LAST_AMENS + ":" + AmenService.FEED_TYPE_INTERESTING)
//          .remove(Constants.PREFS_LAST_NEW_AMENS + ":" + AmenService.FEED_TYPE_FOLLOWING)
//          .remove(Constants.PREFS_LAST_NEW_AMENS + ":" + AmenService.FEED_TYPE_INTERESTING);
//    Toast.makeText(this, "onSharedPreferenceChanged.commit()", Toast.LENGTH_SHORT).show();
//    editor.commit();
//
//    String authKey = prefs.getString(Constants.PREFS_AUTH_TOKEN, null);
//    if (authKey != null) {
//      Toast.makeText(this, "authKey should be null", Toast.LENGTH_SHORT).show();
////      } else {
////        Toast.makeText(this, "authKey == " + authKey, Toast.LENGTH_SHORT).show();
//    }
//
//    if (!TextUtils.isEmpty(prefs.getString(Constants.PREFS_PASSWORD, null))) {
//      if (loginTask != null) {
//        loginTask.cancel(true);
//      }
//      loginTask = new LoginAsyncTask(this);
//      loginTask.execute();
//    }
//  }

  public boolean isShouldRefresh() {
    return shouldRefresh;
  }

  public static void setShouldRefresh(boolean shouldRefresh) {
    AmenListActivity.shouldRefresh = shouldRefresh;
  }
}


