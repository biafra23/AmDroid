package com.jaeckel.amenoid.c2dm;

import com.jaeckel.amenoid.util.Log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author biafra
 * @date 6/5/12 4:20 PM
 */
public class C2DMMessageReceiver extends BroadcastReceiver {


  public void onReceive(Context context, Intent intent) {

    String action = intent.getAction();
    Log.w("C2DM", "Message Receiver called");
    if ("com.google.android.c2dm.intent.RECEIVE".equals(action)) {
      Log.w("C2DM", "Received message");
      final String payload = intent.getStringExtra("text");
      Log.d("C2DM", "dmControl: text = " + payload);
      // Send this to my application server
    }
  }
}
