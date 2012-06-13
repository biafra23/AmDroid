package com.jaeckel.amenoid.c2dm;

import com.jaeckel.amenoid.util.Log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author biafra
 * @date 6/5/12 4:17 PM
 */
public class C2DMRegistrationReceiver extends BroadcastReceiver {

  public void onReceive(Context context, Intent intent) {

    Log.w("C2DM", "onReceive() called");

    String action = intent.getAction();
    Log.w("C2DM", "Registration Receiver called");
    if ("com.google.android.c2dm.intent.REGISTRATION".equals(action)) {
      Log.w("C2DM", "Received registration ID");
      final String registrationId = intent
        .getStringExtra("registration_id");
      String error = intent.getStringExtra("error");

      Log.d("C2DM", "dmControl: registrationId = " + registrationId
                    + ", error = " + error);
      // TODO Send this to my application server
    }
  }
}
