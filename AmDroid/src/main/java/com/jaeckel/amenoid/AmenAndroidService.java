package com.jaeckel.amenoid;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.jaeckel.amenoid.api.AmenService;

/**
 * User: biafra
 * Date: 11/8/11
 * Time: 1:26 AM
 */
public class AmenAndroidService extends Service {
  private AmenService service;

  public IBinder onBind(Intent intent) {
    return null;
  }
}
