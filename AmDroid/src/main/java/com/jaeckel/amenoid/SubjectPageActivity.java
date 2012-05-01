package com.jaeckel.amenoid;

import java.io.IOException;
import java.util.List;

import com.actionbarsherlock.app.SherlockListActivity;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.cwac.thumbnail.ThumbnailAdapter;
import com.jaeckel.amenoid.util.AmenLibTask;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
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
  private static final String TAG = "SubjectPageActivity";
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

    this.setTitle("SubjectPage");

    setContentView(R.layout.subject_page);

    service = AmenoidApp.getInstance().getService();

    Intent trigger = getIntent();

    Long objectId = trigger.getLongExtra(Constants.EXTRA_OBJEKT_ID, -1L);

    Log.d(TAG, "currentObjektId: " + objectId);

    new GetDataTask(this).execute(objectId);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {

    if (position < getListAdapter().getCount()) {

      Amen amen = (Amen) getListAdapter().getItem(position);

      Log.d(TAG, "Selected Amen: " + amen);

      //TODO: make AmenDetailActivity show statements as well
      Intent intent = new Intent(this, AmenDetailActivity.class);
      intent.putExtra(Constants.EXTRA_STATEMENT, amen.getStatement());
      startActivity(intent);
    }
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

          
          ThumbnailAdapter thumbs = new ThumbnailAdapter(SubjectPageActivity.this, amenListAdapter, AmenoidApp.getInstance().getCache(), IMAGE_IDS);
          
          
          setListAdapter(thumbs);
          amenListAdapter.notifyDataSetChanged();
        }

      }
    }
  }
}