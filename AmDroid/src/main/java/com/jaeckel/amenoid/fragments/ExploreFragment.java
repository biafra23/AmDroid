package com.jaeckel.amenoid.fragments;

import java.io.IOException;
import java.util.List;

import com.jaeckel.amenoid.AmenDetailFragmentActivity;
import com.jaeckel.amenoid.AmenListFragmentActivity;
import com.jaeckel.amenoid.CategoryAmenListFragmentActivity;
import com.jaeckel.amenoid.CategoryListAdapter;
import com.jaeckel.amenoid.Constants;
import com.jaeckel.amenoid.R;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.api.model.Category;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.util.AmenLibTask;
import com.jaeckel.amenoid.util.Log;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * @author biafra
 * @date 6/15/12 10:09 PM
 */
public class ExploreFragment extends ListFragment {

  private static String TAG = ExploreFragment.class.getSimpleName();
  private AmenService         service;
  private CategoryListAdapter adapter;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Log.d(TAG, "onCreateView()");
    return inflater.inflate(R.layout.fragment_category_list, container, false);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    Log.d(TAG, "onListItemClick");
    if (position >= 0 && position < getListAdapter().getCount()) {

      Category category = (Category) getListAdapter().getItem(position);

      Log.d(TAG, "---> Selected Category: " + category);

      Intent intent = new Intent(getActivity(), CategoryAmenListFragmentActivity.class);
      intent.putExtra(Constants.EXTRA_CATEGORY, category);
      startActivity(intent);


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
    Log.d(TAG, "onCreate()");

//    getActivity().setContentView(R.layout.main);
    service = AmenoidApp.getInstance().getService();

    // hier den LoaderAsyncTask starten

    Log.d(TAG, "Loading Categories...");
    new CategoriesLoaderAsyncTask(getActivity()).execute();

  }


  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Log.d(TAG, "onActivityCreated()");
  }

//  @Override
//  public void onActivityResult(int requestCode, int resultCode, Intent result) {
//
//    if (requestCode == REQUEST_CODE_AMEN_DETAILS) {
//      // receive the new currentObjekt
//      Log.d(TAG, "onActivityResult | requestCode: REQUEST_CODE_AMEN_DETAILS");
//
//      final Long statementId = result.getLongExtra(Constants.EXTRA_STATEMENT_ID, 0L);
//
//      Log.d(TAG, "onActivityResult | statementId: " + statementId);
//
//      refreshAmenWithStatementId(statementId);
//
//    }
////    refreshWithCache();
//
//  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume()");

  }


  @Override
  public void onPause() {
    super.onPause();
    Log.v(TAG, "onPause()");
  }


  private class CategoriesLoaderAsyncTask extends AmenLibTask<Void, Integer, List<Category>> {

    private ProgressDialog progressDialog;

    private final String TAG = CategoriesLoaderAsyncTask.class.getSimpleName();

    public CategoriesLoaderAsyncTask(Activity context) {
      super(context);
    }

    @Override
    protected void onPreExecute() {

      progressDialog = ProgressDialog.show(getActivity(), "",
                                           "Loading Categories. Please wait...", true);
      progressDialog.show();

    }

    @Override
    protected List<Category> wrappedDoInBackground(Void... params) throws IOException {
      return service.getCategories();
    }

    @Override
    protected void wrappedOnPostExecute(List<Category> categories) {
      Log.d(TAG, "... Loading Categories. Done.");

      if (categories != null) {
        for (Category category : categories) {
          Log.d(TAG, "category: " + category);
        }
        adapter = new CategoryListAdapter(getActivity(), R.layout.list_item_category, categories);
        setListAdapter(adapter);
      }
      progressDialog.hide();
    }
  }
}
