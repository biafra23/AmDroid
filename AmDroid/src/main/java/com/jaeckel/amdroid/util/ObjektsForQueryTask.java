package com.jaeckel.amdroid.util;

import android.os.AsyncTask;
import android.util.Log;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.model.Objekt;

import java.util.ArrayList;
import java.util.List;

/**
 * User: biafra
 * Date: 10/16/11
 * Time: 2:50 PM
 */

public class ObjektsForQueryTask extends AsyncTask<ObjektsForQueryTask.ObjektQuery, Integer, List<Objekt>> {

  private static final String TAG = "ObjektsForQueryTask";

  ReturnedObjektsHandler handler;
  AmenService            service;

  public ObjektsForQueryTask(AmenService service, ReturnedObjektsHandler handler) {
    this.handler = handler;
    this.service = service;

  }


  @Override
  protected List<Objekt> doInBackground(ObjektsForQueryTask.ObjektQuery... objektQueries) {


    ObjektsForQueryTask.ObjektQuery oq = objektQueries[0];
    if (oq.delay  > 0) {
      try {
        Thread.sleep(oq.delay);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    List<Objekt> values = service.objektsForQuery(oq.charSequence, oq.kind, oq.lat, oq.lon);

    if (!isCancelled()) {
      ArrayList<Objekt> objekts = new ArrayList<Objekt>(values);

      if (!objektInList("" + oq.charSequence, objekts)) {
        objekts.add(0, new Objekt("" + oq.charSequence, oq.kind));
      }
    } else {
      Log.d(TAG, "-------------> doInBackground: Cancelled <-------------");
    }

    return values;
  }

  @Override
  protected void onCancelled() {
    Log.d(TAG, "-------------> onCancelled() <-------------");

  }

  @Override
  protected void onPostExecute(List<Objekt> results) {
    handler.handleObjektsResult(results);
  }

  private boolean objektInList(String name, List<Objekt> objekts) {
    if (objekts == null) {
      return false;
    }
    if (objekts.size() == 0) {
      return false;
    }
    for (Objekt o : objekts) {
      if (name.equalsIgnoreCase(o.getName())) {
        return true;
      }
    }
    return false;
  }

  public class ObjektQuery {

    CharSequence charSequence;
    int          kind;
    Double       lat;
    Double       lon;
    long      delay;


    public ObjektQuery(CharSequence charSequence, int kind, Double lat, Double lon) {
      this.charSequence = charSequence;
      this.kind = kind;
      this.lat = lat;
      this.lon = lon;
      this.delay = 0;
    }

    public ObjektQuery(CharSequence charSequence, int kind, Double lat, Double lon, long delay) {
      this.charSequence = charSequence;
      this.kind = kind;
      this.lat = lat;
      this.lon = lon;
      this.delay = delay;
    }
  }

  public interface ReturnedObjektsHandler {
    public void handleObjektsResult(List<Objekt> result);
  }
}