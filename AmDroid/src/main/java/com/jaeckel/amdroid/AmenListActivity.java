package com.jaeckel.amdroid;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.model.Amen;
import com.jaeckel.amdroid.api.model.DateSerializer;
import com.jaeckel.amdroid.api.model.Statement;
import com.jaeckel.amdroid.api.model.User;
import com.jaeckel.amdroid.app.AmdroidApp;
import com.jaeckel.amdroid.cwac.endless.EndlessAdapter;
import com.jaeckel.amdroid.cwac.thumbnail.ThumbnailAdapter;
import com.jaeckel.amdroid.statement.ChooseStatementTypeActivity;
import com.jaeckel.amdroid.util.AmenLibTask;
import com.jaeckel.amdroid.widget.PullToRefreshListView;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AmenListActivity extends ListActivity {

  private static       String TAG                       = "AmenListActivity";
  final private static int    PROGRESS_DIALOG_ID        = 0;
  public static final  int    REQUEST_CODE_PREFERENCES  = 1001;
  public static final  int    REQUEST_CODE_AMEN_DETAILS = 1002;

  final int pageSize = 20;

  private ProgressDialog loginProgressDialog;
  private ProgressDialog loadingProgressDialog;
  private AmenService    service;
  private static final int[] IMAGE_IDS = {R.id.user_image};
  private AmenListAdapter   amenListAdapter;
  private SharedPreferences prefs;
  private Gson gson = new GsonBuilder()
    .registerTypeAdapter(Date.class, new DateSerializer())
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .serializeNulls()
    .create();
  private EndlessLoaderAsyncTask endlessTask;
  private int feedType = AmenService.FEED_TYPE_FOLLOWING;

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

    setContentView(R.layout.main);

    feedType = getIntent().getIntExtra(Constants.EXTRA_FEED_TYPE, AmenService.FEED_TYPE_FOLLOWING);

    String title = "";
    if (feedType == AmenService.FEED_TYPE_FOLLOWING) {
      title = "Following";
    } else {
      title = "New & Interesting";
    }
    setTitle("Amenoid/Timeline: " + title);

    prefs = PreferenceManager.getDefaultSharedPreferences(this);
    String username = prefs.getString("user_name", null);
    String password = prefs.getString("password", null);


    if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {

      new AlertDialog.Builder(this)
        .setMessage("Please enter your Amen credentials in the preferences.")
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

          public void onClick(DialogInterface dialogInterface, int i) {
            startActivityForResult(new Intent(AmenListActivity.this, EditPreferencesActivity.class), REQUEST_CODE_PREFERENCES);
          }
        }).create().show();

    } else {
//      Log.v(TAG, "username: " + username);
//      Log.v(TAG, "password: " + password);
      final String authToken = readAuthTokenFromPrefs();
      final User me = readMeFromPrefs();
      if (authToken != null && me != null) {
        service = AmdroidApp.getInstance().getService().init(authToken, me);
        refreshWithCache();
      } else {
         new LoginAsyncTask(this).execute();

      }
    }

    registerForContextMenu(getListView());

    ((PullToRefreshListView) getListView()).setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {

      public void onRefresh() {
        Log.v(TAG, "onRefresh()");
        // Do work to refresh the list here.
        new GetDataTask(AmenListActivity.this).execute();
      }
    });
  }


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

//    if (amenListAdapter == null || amenListAdapter.getCount() == 0) {
//      Log.d(TAG, "refresh();");
//      refresh();
//    }

  }

  private void refresh() {


    if (service != null) {
      LoaderAsyncTask loader = new LoaderAsyncTask(this);
      loader.execute();
    }
  }

  private void refreshWithCache() {


    if (service != null) {
      CachedLoaderAsyncTask loader = new CachedLoaderAsyncTask(this);
      loader.execute();
    }
  }


  @Override
  public void onPause() {
    super.onPause();
  }


  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);

    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_main, menu);
    MenuItem following = menu.findItem(R.id.following_menu);
    MenuItem interesting = menu.findItem(R.id.interesting);
    if (feedType == AmenService.FEED_TYPE_FOLLOWING) {
      interesting.setVisible(true);
      following.setVisible(false);
    } else {
      interesting.setVisible(false);
      following.setVisible(true);
    }
    return true;
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);

    switch (item.getItemId()) {

      case R.id.preference:
        startActivity(new Intent(this, EditPreferencesActivity.class));

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
      case R.id.interesting: {
        Intent intent = new Intent(this, AmenListActivity.class);
        intent.putExtra(Constants.EXTRA_FEED_TYPE, AmenService.FEED_TYPE_INTERESTING);
        startActivity(intent);

        return true;
      }
      case R.id.amen:
//        Toast.makeText(this, "Refreshing Amens", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ChooseStatementTypeActivity.class);
        startActivity(intent);
        return true;
    }

    return false;
  }


  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {

    if (position > 0) {

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
    Log.d(TAG, "onCreateContextMenu");

    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.amen_item_menu, menu);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
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


      if (getWrappedAdapter().getCount() < 1000) {
        return (true);
      }

      throw new Exception("Gadzooks!");
    }

    @Override
    protected void appendCachedData() {
      if (getWrappedAdapter().getCount() < 1000) {

        if (endlessTask != null) {
          AsyncTask.Status status = endlessTask.getStatus();
          if (status == AsyncTask.Status.FINISHED) {
            endlessTask = new EndlessLoaderAsyncTask(AmenListActivity.this);
            endlessTask.execute(amenListAdapter.getItem(amenListAdapter.getCount() - 1).getId());
          }
        } else {
          endlessTask = new EndlessLoaderAsyncTask(AmenListActivity.this);
          endlessTask.execute(amenListAdapter.getItem(amenListAdapter.getCount() - 1).getId());

        }
      }
    }
  }

  //
  // EndlessLoaderAsyncTask
  //

  private class EndlessLoaderAsyncTask extends AmenLibTask<Long, Integer, List<Amen>> {

    public EndlessLoaderAsyncTask(Context context) {
      super(context);
    }

    @Override
    protected List<Amen> wrappedDoInBackground(Long... longs) throws IOException {

      Long lastAmenId = longs[0];
      Log.d(TAG, "Running on Thread: " + Thread.currentThread().getName());
      Log.d(TAG, "       lastAmenId: " + lastAmenId);

      if (!isCancelled()) {
        List<Amen> amens = service.getFeed(lastAmenId, 20, feedType);
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

    public CachedLoaderAsyncTask(Context context) {
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
          amens.addAll(newAmens);
        }
        Log.v(TAG, "Found amens in prefs: " + amensJSON);

        final List<Amen> amenList = gson.fromJson(amensJSON, collectionType);
        amens.addAll(amenList);

      } else {
        // no cached AMens found. Load from network
        Log.d(TAG, "Loader executing");
        amens = service.getFeed(0, 20, feedType);

        saveAmensToPrefs(amens, Constants.PREFS_LAST_AMENS + ":" + feedType);

      }
      return amens;
    }

    @Override
    protected void onPreExecute() {
      loadingProgressDialog = ProgressDialog.show(AmenListActivity.this, "",
                                                  "Loading Amens. Please wait...", true);

//      loadingProgressDialog.show();
    }

    @Override
    protected void wrappedOnPostExecute(List<Amen> amens) {

      if (amens != null) {
        amenListAdapter = new AmenListAdapter(AmenListActivity.this, android.R.layout.activity_list_item, amens);
        ThumbnailAdapter thumbs = new ThumbnailAdapter(AmenListActivity.this, amenListAdapter, AmdroidApp.getInstance().getCache(), IMAGE_IDS);

        EndlessWrapperAdapter endless = new EndlessWrapperAdapter(thumbs);

        setListAdapter(endless);

      }
      loadingProgressDialog.hide();

    }
  }

  //
  // LoaderAsyncTask
  //
  private class LoaderAsyncTask extends AmenLibTask<Void, Integer, List<Amen>> {

    public LoaderAsyncTask(Context context) {
      super(context);
    }

    @Override
    protected List<Amen> wrappedDoInBackground(Void... voids)  throws IOException  {

      Log.d(TAG, "Loader executing");

      List<Amen> amens = service.getFeed(0, 20, feedType);

      saveAmensToPrefs(amens, Constants.PREFS_LAST_AMENS + ":" + feedType);

      return amens;
    }

    @Override
    protected void onPreExecute() {
      loadingProgressDialog = ProgressDialog.show(AmenListActivity.this, "",
                                                  "Loading Amen from Server...", true);
//      loadingProgressDialog.show();
    }

    @Override
    protected void wrappedOnPostExecute(List<Amen> amens) {

      if (amens != null) {

        amenListAdapter = new AmenListAdapter(AmenListActivity.this, android.R.layout.activity_list_item, amens);
        ThumbnailAdapter thumbs = new ThumbnailAdapter(AmenListActivity.this, amenListAdapter, AmdroidApp.getInstance().getCache(), IMAGE_IDS);

        EndlessWrapperAdapter endless = new EndlessWrapperAdapter(thumbs);

        setListAdapter(endless);

      }

      loadingProgressDialog.hide();
    }
  }

  //
  // LoginAsyncTask
  //

  private class LoginAsyncTask extends AmenLibTask<Void, Integer, AmenService> {

    public LoginAsyncTask(Context context) {
      super(context);
    }
    
    @Override
    protected void onPreExecute() {
      if (AmdroidApp.DEVELOPER_MODE) {
        Toast.makeText(AmenListActivity.this, "LoginAsyncTask.onPreExecute", Toast.LENGTH_SHORT).show();
      }
      loginProgressDialog = ProgressDialog.show(AmenListActivity.this, "",
                                                "Logging in. Please wait...", true);
      loginProgressDialog.show();
    }

    @Override
    protected AmenService wrappedDoInBackground(Void... voids) {

      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AmenListActivity.this);
      String username = prefs.getString("user_name", null);
      String password = prefs.getString("password", null);

      final AmenService amenService = AmdroidApp.getInstance().getService(username, password);
      saveAuthTokenToPrefs(amenService.getAuthToken());
      saveMeToPrefs(amenService.getMe());
      return amenService;
    }

    @Override
    protected void wrappedOnPostExecute(AmenService service) {

      if (service != null) {

        AmenListActivity.this.service = service;
        
        loginProgressDialog.hide();

        refresh();
      }

    }
  }

  //
  // GetDataTask
  //

  private class GetDataTask extends AmenLibTask<Void, Void, List<Amen>> {

    public GetDataTask(Context context) {
      super(context);
    }

    @Override
    protected List<Amen> wrappedDoInBackground(Void... voids)  throws IOException {
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
        if (result.size() == pageSize) {
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

    public AmenRefreshTask(Context context) {
      super(context);
    }

    @Override
    protected Amen wrappedDoInBackground(Long... statementIds)  throws IOException {
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
    Log.i(TAG, "onConfigurationChanged(): " + newConfig);
//    setContentView(R.layout.myLayout);
  }

}

