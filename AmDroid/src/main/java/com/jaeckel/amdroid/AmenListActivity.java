package com.jaeckel.amdroid;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.model.Amen;
import com.jaeckel.amdroid.app.AmdroidApp;
import com.jaeckel.amdroid.cwac.endless.EndlessAdapter;
import com.jaeckel.amdroid.cwac.thumbnail.ThumbnailAdapter;

import java.util.List;

import static com.jaeckel.amdroid.Constants.AMEN_ID;

public class AmenListActivity extends ListActivity {

  private static       String TAG                = "amdroid/AmenListActivity";
  final private static int    PROGRESS_DIALOG_ID = 0;
  public static final  int    REQUEST_CODE       = 1001;

  private ProgressDialog progressDialog;
  private AmenService    service;
  private static final int[] IMAGE_IDS = {R.id.user_image};

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
//    Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();

    progressDialog = ProgressDialog.show(AmenListActivity.this, "",
                                         "Loading. Please wait...", true);
    progressDialog.show();
    // Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
    setContentView(R.layout.main);


    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    String username = prefs.getString("user_name", null);
    String password = prefs.getString("password", null);

    if (username == null || password == null) {
      progressDialog.hide();
      startActivityForResult(new Intent(this, EditPreferencesActivity.class), REQUEST_CODE);
    } else {
      service = AmdroidApp.getInstance().getService(username, password);
    }
    Log.d(TAG, "progressDialog.hide();");
    progressDialog.hide();
    Log.d(TAG, "refresh();");
    refresh();

    Log.v(TAG, "onCreate... done.");
//    Toast.makeText(this, "onCreate done", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent result) {

//    if (requestCode == REQUEST_CODE) {

//      Toast.makeText(this, "onActivityResult", Toast.LENGTH_SHORT).show();

      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
      String username = prefs.getString("user_name", null);
      String password = prefs.getString("password", null);

      service = AmdroidApp.getInstance().getService(username, password);
      refresh();
//    }

  }

  @Override
  public void onResume() {
    super.onResume();
//    Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
    if (service == null) {
//      Toast.makeText(this, "service was null -> login", Toast.LENGTH_SHORT).show();
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
      String username = prefs.getString("user_name", null);
      String password = prefs.getString("password", null);

      service = AmdroidApp.getInstance().getService(username, password);
      refresh();
    }


//    refresh();
//    Toast.makeText(this, "onResume done", Toast.LENGTH_SHORT).show();
  }

  private void refresh() {
    LoaderAsyncTask loader = new LoaderAsyncTask();
    loader.execute();
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
        Intent intent = new Intent(this, MakeStatementActivity.class);
        startActivity(intent);
        return true;
    }

    return false;
  }


  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {

    Amen amen = (Amen) getListAdapter().getItem(position);

    Log.d(TAG, "Selected Amen: " + amen);

    Intent intent = new Intent(this, AmenDetailActivity.class);
    intent.putExtra(AMEN_ID, amen.getId());
    startActivity(intent);
  }

  class EndlessWrapperAdapter extends EndlessAdapter {

    EndlessWrapperAdapter(ThumbnailAdapter thumbnailAdapter) {

      super(AmenListActivity.this, thumbnailAdapter, R.layout.pending);
    }

    @Override
    protected boolean cacheInBackground() throws Exception {
//  			SystemClock.sleep(5000);				// pretend to do work

      //ends at 74
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
      progressDialog = ProgressDialog.show(AmenListActivity.this, "",
                                           "Loading. Please wait...", true);

      progressDialog.show();
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

//      adapter = new AmenListAdapter(AmenListActivity.this, android.R.layout.simple_list_item_1, amens);
      ThumbnailAdapter thumbs = new ThumbnailAdapter(AmenListActivity.this, new AmenListAdapter(AmenListActivity.this, android.R.layout.activity_list_item, amens), AmdroidApp.getInstance().getCache(), IMAGE_IDS);

      EndlessWrapperAdapter endless = new EndlessWrapperAdapter(thumbs);
//      setListAdapter(thumbs);
      setListAdapter(endless);

      //hide progress
      progressDialog.hide();

//      for (Amen a : amens) {
//        Log.d(TAG, "Amen: " + a);
//      }

    }
  }

}

