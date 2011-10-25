package com.jaeckel.amdroid.util;

import android.content.Context;
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

public class ObjektsForQueryTask extends AmenLibTask<ObjektsForQueryTask.ObjektQuery, Integer, List<Objekt>> {

  private static final String TAG = "ObjektsForQueryTask";

  ReturnedObjektsHandler handler;
  AmenService            service;

  /**
   * @param service
   * @param handler needs to be a context! If its not use ObjektsForQueryTask(AmenService service, ReturnedObjektsHandler handler, Context context)
   */
  public ObjektsForQueryTask(AmenService service, ReturnedObjektsHandler handler) {
    super((Context) handler);
    this.handler = handler;
    this.service = service;

  }

  public ObjektsForQueryTask(AmenService service, ReturnedObjektsHandler handler, Context context) {
    super(context);
    this.handler = handler;
    this.service = service;

  }


  @Override
  protected List<Objekt> wrappedDoInBackground(ObjektsForQueryTask.ObjektQuery... objektQueries) {


    ObjektsForQueryTask.ObjektQuery oq = objektQueries[0];
    if (oq.delay > 0) {
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
  protected void wrappedOnPostExecute(List<Objekt> results) {
    if (results != null) {
      handler.handleObjektsResult(results);
    }

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
    long         delay;


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