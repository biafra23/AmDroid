package com.jaeckel.amenoid;

import java.io.IOException;
import java.util.List;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.api.model.Objekt;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.cwac.thumbnail.ThumbnailAdapter;
import com.jaeckel.amenoid.util.AmenLibTask;
import com.jaeckel.amenoid.util.Log;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * User: biafra
 * Date: 11/12/11
 * Time: 12:02 AM
 */
public class SubjectPageActivity extends SherlockListActivity {
  private AmenListAdapter amenListAdapter;
  private AmenService     service;
  private static final String TAG       = "SubjectPageActivity";
  private static final int[]  IMAGE_IDS = {R.id.media_photo};

  private Objekt currentObjekt;

  private ActionBar           actionBar;
  private ShareActionProvider mShareActionProvider;

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


    setContentView(R.layout.subject_page);

    actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle("Thing card");


    service = AmenoidApp.getInstance().getService();

    Intent trigger = getIntent();

    Long objectId = trigger.getLongExtra(Constants.EXTRA_OBJEKT_ID, -1L);

    Log.d(TAG, "currentObjektId: " + objectId);

    new GetDataTask(this).executeOnThreadPool(objectId);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {

    if (position < getListAdapter().getCount()) {

      Amen amen = (Amen) getListAdapter().getItem(position);

      Log.d(TAG, "Selected Amen: " + amen);

      //TODO: make AmenDetailActivity show statements as well
      Intent intent = new Intent(this, AmenDetailFragmentActivity.class);
      intent.putExtra(Constants.EXTRA_STATEMENT, amen.getStatement());
      startActivity(intent);
    }
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);

    Log.d(TAG, "onOptionsItemSelected -> item.getItemId(): " + item.getItemId());

    final Intent amenListIntent = new Intent(this, AmenListFragmentActivity.class);
    amenListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

    switch (item.getItemId()) {

      case android.R.id.home: {
        startActivity(amenListIntent);
        return true;
      }
    }
    return false;
  }
  //
  // GetDataTask
  //

  private class GetDataTask extends AmenLibTask<Long, Void, List<Amen>> {

    public GetDataTask(Activity context) {
      super(context);
    }

    @Override
    protected List<Amen> wrappedDoInBackground(Long... ids) throws IOException {
      Log.v(TAG, "doInBackground:" + ids[0]);

      List<Amen> amens = service.getAmenForObjekt(ids[0]);

      return amens;
    }


    @Override
    protected void wrappedOnPostExecute(List<Amen> result) {

      if (result != null) {

        if (result.size() > 0) {

          amenListAdapter = new AmenListAdapter(SubjectPageActivity.this, R.layout.list_item_amen_no_pic, result);
          currentObjekt = result.get(0).getStatement().getObjekt();
          createShareIntent(currentObjekt);

          actionBar.setSubtitle(currentObjekt.getName());

          ThumbnailAdapter thumbs = new ThumbnailAdapter(SubjectPageActivity.this,
                                                         amenListAdapter, AmenoidApp.getInstance().getCache(), IMAGE_IDS);


          setListAdapter(thumbs);
          amenListAdapter.notifyDataSetChanged();
        }

      }
    }
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);

    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.menu_subject_page, menu);
//    if (!AmenoidApp.getInstance().isSignedIn()) {
//      MenuItem amenSth = menu.findItem(R.id.amen);
//      amenSth.setEnabled(false);
//    }

    MenuItem shareMenuItem = menu.findItem(R.id.share_subject);
    mShareActionProvider = (ShareActionProvider) shareMenuItem.getActionProvider();

    if (currentObjekt != null) {
      createShareIntent(currentObjekt);
    }

    return true;
  }

  private void createShareIntent(Objekt objekt) {
    if (objekt == null) {
      return;
    }
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, objekt.getName() + " #getamen https://getamen.com/things/" + objekt.getSlug());

    if (mShareActionProvider != null) {
      mShareActionProvider.setShareIntent(shareIntent);
    }
  }

}