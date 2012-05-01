package com.jaeckel.amenoid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.cwac.thumbnail.ThumbnailAdapter;
import com.jaeckel.amenoid.util.AmenLibTask;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;


/**
 * User: biafra
 * Date: 11/16/11
 * Time: 10:17 PM
 */
public class SearchActivity extends ListActivity {
  private static String TAG = "SearchActivity";

  private Typeface        amenTypeThin;
  private Typeface        amenTypeBold;
  private AmenService     service;
  private AmenListAdapter adapter;
  private ProgressBar     progressBar;
  private ListView        list;
  private static final int[] IMAGE_IDS = {R.id.user_image, R.id.media_photo};

  private ProgressDialog progressDialog;

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    amenTypeThin = AmenoidApp.getInstance().getAmenTypeThin();
    amenTypeBold = AmenoidApp.getInstance().getAmenTypeBold();

//    handler = new Handler();
//    errorDialog = new AlertDialog.Builder(this);

    Log.d(TAG, "onCreate");

    service = AmenoidApp.getInstance().getService();

    setContentView(R.layout.search);


    setTitle("Search");
    progressBar = (ProgressBar) findViewById(R.id.progress_listview);

    list = (ListView) findViewById(android.R.id.list);
//    View header = getLayoutInflater().inflate(R.layout.search_header, null, false);
//    list.addHeaderView(header);

    adapter = new AmenListAdapter(this, R.layout.list_item_amen, new ArrayList<Amen>());
    setListAdapter(adapter);

//    final EditText searchField = (EditText) findViewById(R.id.search_field);

//    Button searchButton = (Button) findViewById(R.id.submit_search);
//    searchButton.setOnClickListener(new View.OnClickListener() {
//
//      public void onClick(View view) {
//        Log.d(TAG, "Such!");
////        Toast.makeText(SearchActivity.this, "Such: " + searchField.getText(), Toast.LENGTH_SHORT).show();
//
//        new LoaderAsyncTask(SearchActivity.this).execute(searchField.getText().toString());
//
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
//
//      }
//    });

    // Get the intent, verify the action and get the query
    Intent intent = getIntent();
    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
      String query = intent.getStringExtra(SearchManager.QUERY);

      new LoaderAsyncTask(SearchActivity.this).execute(query);
      setTitle("Search: " + query);

    }
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {

    if (position < getListAdapter().getCount()) {

      Amen amen = (Amen) getListAdapter().getItem(position);

      Log.d(TAG, "Selected Amen: " + amen);

      Intent intent = new Intent(this, AmenDetailActivity.class);
      intent.putExtra(Constants.EXTRA_AMEN, amen);
//      startActivityForResult(intent, REQUEST_CODE_AMEN_DETAILS);
      startActivity(intent);

    }
  }


  //
  // LoaderAsyncTask
  //
  class LoaderAsyncTask extends AmenLibTask<String, Integer, List<Amen>> {

    private boolean stopAppending = false;
    private ProgressDialog loadingProgressDialog;

    public LoaderAsyncTask(Activity context) {
      super(context);
    }

    @Override
    protected List<Amen> wrappedDoInBackground(String... queries) throws IOException {

      Log.d(TAG, "Loader executing");
      for (String q : queries) {
        List<Amen> amens = service.search(q);
        return amens;
      }
      return null;
    }

    @Override
    protected void onPreExecute() {

//      list.setVisibility(View.INVISIBLE);
      progressBar.setVisibility(View.VISIBLE);
      progressDialog = ProgressDialog.show(SearchActivity.this, "",
                                           "Searching...", true);

//      progressDialog.show();


    }

    @Override
    protected void wrappedOnPostExecute(List<Amen> amens) {

      if (amens != null) {

        adapter = new AmenListAdapter(SearchActivity.this, R.layout.list_item_amen, amens);
        ThumbnailAdapter thumbs = new ThumbnailAdapter(SearchActivity.this, adapter, AmenoidApp.getInstance().getCache(), IMAGE_IDS);
        setListAdapter(thumbs);
      }

//      progressBar.setVisibility(View.INVISIBLE);
//      list.setVisibility(View.VISIBLE);
      progressDialog.hide();
    }

    @Override
    protected void onCancelled() {
      Log.d(TAG, "loadingProgressDialog cancelled");
//      progressBar.setVisibility(View.INVISIBLE);
//      list.setVisibility(View.VISIBLE);

    }
  }
}