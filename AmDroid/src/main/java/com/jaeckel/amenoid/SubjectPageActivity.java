package com.jaeckel.amenoid;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.util.AmenLibTask;

import java.io.IOException;
import java.util.List;

/**
 * User: biafra
 * Date: 11/12/11
 * Time: 12:02 AM
 */
public class SubjectPageActivity extends ListActivity {
  private AmenListAdapter amenListAdapter;
  private AmenService     service;
  private static final String TAG = "SubjectPageActivity";

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.subject_page);

    service = AmenoidApp.getInstance().getService();

    Intent trigger = getIntent();

    Long objectId = trigger.getLongExtra(Constants.EXTRA_OBJEKT_ID, -1L);

    Log.d(TAG, "currentObjektId: " + objectId);

    new GetDataTask(this).execute(objectId);
  }


  //
  // GetDataTask
  //

  private class GetDataTask extends AmenLibTask<Long, Void, List<Amen>> {

    public GetDataTask(Context context) {
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

          amenListAdapter = new AmenListAdapter(SubjectPageActivity.this, android.R.layout.activity_list_item, result);

          setListAdapter(amenListAdapter);
          amenListAdapter.notifyDataSetChanged();
        }

      }
    }
  }
}