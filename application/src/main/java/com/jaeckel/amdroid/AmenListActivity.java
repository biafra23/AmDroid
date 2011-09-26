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
import com.jaeckel.amdroid.cwac.thumbnail.ThumbnailAdapter;

import java.util.List;

import static com.jaeckel.amdroid.AmenDetailActivity.AMEN_ID;

public class AmenListActivity extends ListActivity {

  private static       String TAG                = "amdroid/AmenListActivity";
  final private static int    PROGRESS_DIALOG_ID = 0;

  private ProgressDialog   progressDialog;
  private AmenListAdapter  adapter;
  private ThumbnailAdapter thumbs;
  private AmenService      service;
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

    progressDialog = ProgressDialog.show(AmenListActivity.this, "",
                                         "Loading. Please wait...", true);
    progressDialog.show();
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    String username = prefs.getString("user_name", null);
    String password = prefs.getString("password", null);

    if (username == null || password == null) {
      startActivity(new Intent(this, EditPreferencesActivity.class));
    }
    username = prefs.getString("user_name", null);
    password = prefs.getString("password", null);

    service = AmdroidApp.getInstance().getService(username, password);

    setContentView(R.layout.main);

    progressDialog.hide();
    refresh();
  }

  @Override
  public void onResume() {
    super.onResume();
//    refresh();
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
      List<Amen> amens = service.getFeed(0, 50);

      return amens;
    }

    @Override
    protected void onPostExecute(List<Amen> amens) {

      adapter = new AmenListAdapter(AmenListActivity.this, android.R.layout.simple_list_item_1, amens);
      thumbs = new ThumbnailAdapter(AmenListActivity.this, new AmenListAdapter(AmenListActivity.this, android.R.layout.activity_list_item, amens), AmdroidApp.getInstance().getCache(), IMAGE_IDS);
      setListAdapter(thumbs);

      //hide progress
      progressDialog.hide();

//      for (Amen a : amens) {
//        Log.d(TAG, "Amen: " + a);
//      }

    }
  }

}

