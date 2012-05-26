package com.jaeckel.amenoid.fragments;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jaeckel.amenoid.AmenDetailFragmentActivity;
import com.jaeckel.amenoid.AmenListAdapter;
import com.jaeckel.amenoid.Constants;
import com.jaeckel.amenoid.R;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.api.model.DateSerializer;
import com.jaeckel.amenoid.api.model.Statement;
import com.jaeckel.amenoid.api.model.User;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.cwac.endless.EndlessAdapter;
import com.jaeckel.amenoid.cwac.thumbnail.ThumbnailAdapter;
import com.jaeckel.amenoid.util.AmenLibTask;
import com.jaeckel.amenoid.util.Log;
import com.jaeckel.amenoid.widget.PullToRefreshListView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

/**
 * @author biafra
 * @date 5/22/12 11:13 AM
 */
public class AmenListFragment extends ListFragment {


  private static       String TAG                       = AmenListFragment.class.getSimpleName();
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
  private        Handler handler       = new Handler();

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_amen_list, container, false);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {

    if (position > 0 && position < getListAdapter().getCount()) {

      Amen amen = (Amen) getListAdapter().getItem(position - 1);

      Log.d(TAG, "Selected Amen: " + amen);

      Intent intent = new Intent(getActivity(), AmenDetailFragmentActivity.class);
      intent.putExtra(Constants.EXTRA_AMEN, amen);
      startActivityForResult(intent, REQUEST_CODE_AMEN_DETAILS);
    }
  }


  //
  // copied from ListActivity
  //


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

    getActivity().setContentView(R.layout.main);

    prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

    // default feedType is following
    feedType = getActivity().getIntent().getIntExtra(Constants.EXTRA_FEED_TYPE, AmenService.FEED_TYPE_FOLLOWING);

    if (!AmenoidApp.getInstance().isSignedIn() && feedType == AmenService.FEED_TYPE_FOLLOWING) {

      // if not signed in default to popular. new is gone
      feedType = AmenService.FEED_TYPE_POPULAR;
    }

    final String authToken = readAuthTokenFromPrefs();
    final User me = readMeFromPrefs();
    if (AmenoidApp.DEVELOPER_MODE) {
      Toast.makeText(getActivity(), "authToken: " + authToken, Toast.LENGTH_SHORT).show();
    }

    refreshWithCache();

//    registerForContextMenu(getListView());


  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {

    super.onActivityCreated(savedInstanceState);

    final PullToRefreshListView listView = (PullToRefreshListView) getListView();


    listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {

      public void onRefresh() {
        Log.v(TAG, "onRefresh()");
        // Do work to refresh the list here.
        new GetDataTask(getActivity()).execute();
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

    new AmenRefreshTask(getActivity()).execute(statementId);
  }

  @Override
  public void onResume() {
    super.onResume();

    if (shouldRefresh) {
      refresh();
      shouldRefresh = false;
    }
  }

  public void refresh() {

    service = AmenoidApp.getInstance().getService();

    if (service != null) {
      LoaderAsyncTask loader = new LoaderAsyncTask(getActivity());
      loader.execute();
    } else {
      Log.e(TAG, "Service still " + service);
    }

  }

  public void refreshWithCache() {

    service = AmenoidApp.getInstance().getService();
    if (service != null) {
      CachedLoaderAsyncTask loader = new CachedLoaderAsyncTask(getActivity());
      loader.execute();
    } else {
      if (AmenoidApp.DEVELOPER_MODE) {
        Toast.makeText(getActivity(), "refreshWithCache() AmenLibService was null", Toast.LENGTH_SHORT).show();
      }
    }
  }


  @Override
  public void onPause() {
    super.onPause();
  }

//  @Override
//  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//    super.onCreateContextMenu(menu, v, menuInfo);
//
//  }


  class EndlessWrapperAdapter extends EndlessAdapter {

    EndlessWrapperAdapter(ThumbnailAdapter thumbnailAdapter) {

      super(getActivity(), thumbnailAdapter, R.layout.pending);
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
              endlessTask = new EndlessLoaderAsyncTask(getActivity());
              endlessTask.execute(lastId);
            }
          } else {
            endlessTask = new EndlessLoaderAsyncTask(getActivity());
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

      final String prefsKeyNewAmen = Constants.PREFS_LAST_NEW_AMENS + ":" + feedType;

      String newAmensJSON = prefs.getString(prefsKeyNewAmen, null);

      final String prefsKeyLastAmen = Constants.PREFS_LAST_AMENS + ":" + feedType;

      String amensJSON = prefs.getString(prefsKeyLastAmen, null);

      if (amensJSON != null && !"[]".equals(amensJSON)) {

        Type collectionType = new TypeToken<List<Amen>>() {
        }.getType();

        if (newAmensJSON != null && !"[]".equals(newAmensJSON)) {
          Log.v(TAG, "Found new amens in prefs: " + newAmensJSON);


          final List<Amen> newAmens = gson.fromJson(newAmensJSON, collectionType);
          handler.post(new Runnable() {
            @Override
            public void run() {
              Toast.makeText(getActivity(), "Read " + newAmens.size() + " amens from prefsKey: " + prefsKeyNewAmen, Toast.LENGTH_SHORT).show();
            }
          });
          if (newAmens != null) {
            amens.addAll(newAmens);
          }

        }
        Log.v(TAG, "Found amens in prefs: " + amensJSON);

        final List<Amen> amenList = gson.fromJson(amensJSON, collectionType);
        handler.post(new Runnable() {
          @Override
          public void run() {
            Toast.makeText(getActivity(), "Read " + amenList.size() + " amens from prefsKey: " + prefsKeyLastAmen, Toast.LENGTH_SHORT).show();
          }
        });
        if (amenList != null) {
          amens.addAll(amenList);
        }

      } else {
        // no cached AMens found. Load from network
        Log.d(TAG, "Loader executing");
        amens = service.getFeed(0, 20, feedType);
//        amenDao.insertOrUpdate(amens);

        saveAmensToPrefs(amens, prefsKeyLastAmen);

      }
      return amens;
    }

    @Override
    protected void onPreExecute() {

      cachedLoadingProgressDialog = ProgressDialog.show(getActivity(), "",
                                                        "Loading Amens. Please wait...", true);

      cachedLoadingProgressDialog.show();

    }

    @Override
    protected void wrappedOnPostExecute(List<Amen> amens) {
      Log.d(TAG, "wrappedOnPostExecute");
      if (amens != null) {
        amenListAdapter = new AmenListAdapter(getActivity(), R.layout.list_item_amen, amens);
        ThumbnailAdapter thumbs = new ThumbnailAdapter(getActivity(), amenListAdapter, AmenoidApp.getInstance().getCache(), IMAGE_IDS);

        EndlessWrapperAdapter endless = new EndlessWrapperAdapter(thumbs);

        setListAdapter(endless);

      }

      Log.v(TAG, "hasWindowFocus(): " + getActivity().hasWindowFocus());

      cachedLoadingProgressDialog.hide();
    }

    @Override
    protected void onCancelled() {
      if (getActivity().hasWindowFocus() && cachedLoadingProgressDialog != null) {
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
      saveAmensToPrefs(new ArrayList<Amen>(), Constants.PREFS_LAST_NEW_AMENS + ":" + feedType);

      return amens;
    }

    @Override
    protected void onPreExecute() {
      loadingProgressDialog = ProgressDialog.show(getActivity(), "",
                                                  "Loading Amen from Server...", true);
      loadingProgressDialog.show();

    }

    @Override
    protected void wrappedOnPostExecute(List<Amen> amens) {

      if (amens != null) {

        amenListAdapter = new AmenListAdapter(getActivity(), R.layout.list_item_amen, amens);
        ThumbnailAdapter thumbs = new ThumbnailAdapter(getActivity(), amenListAdapter, AmenoidApp.getInstance().getCache(), IMAGE_IDS);

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

      List<Amen> amens = service.getFeed(0, pageSize, feedType);

      filteredAmens = filterNewAmens(oldAmens, amens);
      newAmens.addAll(filteredAmens);
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

  private void saveAmensToPrefs(final List<Amen> amens, final String prefsKey) {
    SharedPreferences.Editor editor = prefs.edit();

    String newAmensJSON = gson.toJson(amens);
    Log.v(TAG, "prefsKey: " + prefsKey + ": " + newAmensJSON);

    editor.putString(prefsKey, newAmensJSON);
    boolean result = editor.commit();

    if (result) {
      if (getResources().getBoolean(R.bool.debuggable)) {

        handler.post(new Runnable() {
          @Override public void run() {
            Toast.makeText(getActivity(), "Saved " + amens.size() + " amens to prefsKey: " + prefsKey + " sucessfully", Toast.LENGTH_SHORT).show();
          }
        });
      }
    } else {
      handler.post(new Runnable() {
        @Override public void run() {
          Toast.makeText(getActivity(), "Saving amens to prefsKey: " + prefsKey + " failed", Toast.LENGTH_LONG).show();
        }
      });
    }
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
      return result;
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


  public boolean isShouldRefresh() {
    return shouldRefresh;
  }

  public static void setShouldRefresh(boolean refresh) {

    shouldRefresh = refresh;
  }


}
