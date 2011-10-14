package com.jaeckel.amdroid;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.model.Amen;
import com.jaeckel.amdroid.app.AmdroidApp;
import com.jaeckel.amdroid.cwac.endless.EndlessAdapter;
import com.jaeckel.amdroid.cwac.thumbnail.ThumbnailAdapter;
import com.jaeckel.amdroid.statement.ChooseStatementTypeActivity;
import com.jaeckel.amdroid.widget.PullToRefreshListView;

import java.util.List;

public class AmenListActivity extends ListActivity {

  private static       String TAG                = "amdroid/AmenListActivity";
  final private static int    PROGRESS_DIALOG_ID = 0;
  public static final  int    REQUEST_CODE       = 1001;

  private ProgressDialog loginProgressDialog;
  private ProgressDialog loadingProgressDialog;
  private AmenService    service;
  private static final int[] IMAGE_IDS = {R.id.user_image};
  private AmenListAdapter amenListAdapter;

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

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    String username = prefs.getString("user_name", null);
    String password = prefs.getString("password", null);


    if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {

      new AlertDialog.Builder(this)
        .setMessage("Please enter your Amen credentials in the preferences.")
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

          public void onClick(DialogInterface dialogInterface, int i) {
            startActivityForResult(new Intent(AmenListActivity.this, EditPreferencesActivity.class), REQUEST_CODE);
          }
        }).create().show();

    } else {
//      Log.v(TAG, "username: " + username);
//      Log.v(TAG, "password: " + password);

      LoginAsyncTask task = new LoginAsyncTask();
      task.execute();
    }

    registerForContextMenu(getListView());

    ((PullToRefreshListView) getListView()).setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
      @Override
      public void onRefresh() {
        Log.v(TAG, "onRefresh()");
        // Do work to refresh the list here.
        new GetDataTask().execute();
      }
    });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent result) {

    refresh();

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
      LoaderAsyncTask loader = new LoaderAsyncTask();
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
    return true;
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);

    switch (item.getItemId()) {

      case R.id.preference:
//        Toast.makeText(this, "Prefereces", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, EditPreferencesActivity.class));

        return true;

      case R.id.refresh:
//        Toast.makeText(this, "Refreshing Amens", Toast.LENGTH_SHORT).show();
        refresh();

        return true;

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

    Amen amen = (Amen) getListAdapter().getItem(position - 1);

    Log.d(TAG, "Selected Amen: " + amen);

    Intent intent = new Intent(this, AmenDetailActivity.class);
    intent.putExtra(Constants.EXTRA_AMEN, amen);
    startActivity(intent);
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


      if (getWrappedAdapter().getCount() < 10000) {
        return (true);
      }

      throw new Exception("Gadzooks!");
    }

    @Override
    protected void appendCachedData() {
      if (getWrappedAdapter().getCount() < 10000) {
        @SuppressWarnings("unchecked")
        ThumbnailAdapter a = (ThumbnailAdapter) getWrappedAdapter();
        AmenListAdapter amenListAdapter = (AmenListAdapter) a.getWrappedAdapter();

        List<Amen> amens = service.getFeed(amenListAdapter.getItem(amenListAdapter.getCount() - 1).getId(), 20);

        for (Amen amen : amens) {
          amenListAdapter.add(amen);
        }
      }
    }
  }


  private class LoaderAsyncTask extends AsyncTask<Void, Integer, List<Amen>> {

    @Override
    protected void onPreExecute() {
      loadingProgressDialog = ProgressDialog.show(AmenListActivity.this, "",
                                                  "Loading Amens. Please wait...", true);
      if (AmdroidApp.DEVELOPER_MODE) {
        Toast.makeText(AmenListActivity.this, "LoaderAsyncTask.onPreExecute", Toast.LENGTH_SHORT).show();
      }
      loadingProgressDialog.show();
    }

    @Override
    protected List<Amen> doInBackground(Void... voids) {

//      showDialog() progress

      Log.d(TAG, "Loader executing");
      List<Amen> amens = service.getFeed(0, 20);

      return amens;
    }

    @Override
    protected void onPostExecute(List<Amen> amens) {

      amenListAdapter = new AmenListAdapter(AmenListActivity.this, android.R.layout.activity_list_item, amens);
      ThumbnailAdapter thumbs = new ThumbnailAdapter(AmenListActivity.this, amenListAdapter, AmdroidApp.getInstance().getCache(), IMAGE_IDS);

      EndlessWrapperAdapter endless = new EndlessWrapperAdapter(thumbs);

      setListAdapter(endless);

      loadingProgressDialog.hide();
      if (AmdroidApp.DEVELOPER_MODE) {
        Toast.makeText(AmenListActivity.this, "LoaderAsyncTask.onPostExecute", Toast.LENGTH_SHORT).show();
      }
    }
  }

  private class LoginAsyncTask extends AsyncTask<Void, Integer, AmenService> {

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
    protected AmenService doInBackground(Void... voids) {

      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AmenListActivity.this);
      String username = prefs.getString("user_name", null);
      String password = prefs.getString("password", null);

      return AmdroidApp.getInstance().getService(username, password);
    }

    @Override
    protected void onPostExecute(AmenService service) {
      if (AmdroidApp.DEVELOPER_MODE) {
        Toast.makeText(AmenListActivity.this, "LoginAsyncTask.onPostExecute", Toast.LENGTH_SHORT).show();
      }
      AmenListActivity.this.service = service;
      Log.d(TAG, "Service set: " + service);

      loginProgressDialog.hide();

      refresh();

    }
  }

  private class GetDataTask extends AsyncTask<Void, Void, List<Amen>> {

    @Override
    protected List<Amen> doInBackground(Void... voids) {
      Log.v(TAG, "doInBackground");

      List<Amen> amens = service.getFeed(0, 20);

      return amens;
    }

    @Override
    protected void onPostExecute(List<Amen> result) {
      Log.v(TAG, "onPostExecute");

      Amen lastAmen = amenListAdapter.getItem(amenListAdapter.getCount() - 1);
      Amen firstAmen = amenListAdapter.getItem(0);
      Log.v(TAG, "firstAmen: " + firstAmen.getId() + " name: " + firstAmen.getStatement().getObjekt().getName());
      Log.v(TAG, "lastAmen: " + lastAmen.getId() + " name: " + lastAmen.getStatement().getObjekt().getName());

      for (Amen amen : result) {
        Log.v(TAG, "result -> amen: " + amen.getId() + " name: " + amen.getStatement().getObjekt().getName());
        if (firstAmen.equals(amen)) {

          break;

        } else {
          Log.v(TAG, "Adding amen: " + amen.getId() + " name: " + amen.getStatement().getObjekt().getName());
          amenListAdapter.insert(amen, 0);
          amenListAdapter.notifyDataSetChanged();
        }

      }
      // Call onRefreshComplete when the list has been refreshed.
      ((PullToRefreshListView) getListView()).onRefreshComplete();
      super.onPostExecute(result);
    }
  }
}

