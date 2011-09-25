package com.jaeckel.amdroid;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.AmenServiceImpl;
import com.jaeckel.amdroid.api.model.Amen;

import java.util.List;

public class AmenListActivity extends ListActivity {

  private static String TAG = "amdroid/AmenListActivity";
  private AmenListAdapter adapter;

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

  }

  @Override
  public void onResume() {
    super.onResume();
    AmenService service = new AmenServiceImpl();
    service.init("nbotvin@different.name", "foobar23");

    List<Amen> amens = service.getFeed();

    for (Amen a : amens) {
      Log.d(TAG, "Amen: " + a);
    }

    adapter = new AmenListAdapter(this, android.R.layout.simple_list_item_1, amens);
    setListAdapter(adapter);



  }

  @Override
  public void onPause() {
    super.onPause();
  }

}

