package com.jaeckel.amdroid.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import com.jaeckel.amdroid.R;

/**
 * User: biafra
 * Date: 10/23/11
 * Time: 3:39 AM
 */

public abstract class AmenLibTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

  private Throwable lastException = null;
  private Context context;
  private static String TAG = "AmenLibTask";

  public AmenLibTask(Context context) {
    this.context = context;
  }

  @Override
  protected Result doInBackground(Params... params) {

    try {
      return wrappedDoInBackground(params);

    } catch (Error e) {
      lastException = e;
//      Log.d(TAG, "---------------------------------------");
//      e.printStackTrace();
//      Log.d(TAG, "---------------------------------------");

    }
    return null;
  }

  protected abstract Result wrappedDoInBackground(Params... params);

  protected void onPostExecute(Result result) {

    if (lastException != null) {

      Log.e(TAG, "ERROR occured");
      lastException.printStackTrace();

      String title = "Exception";
      String message = lastException.getMessage();
      if (lastException.getCause() != null) {
        message = lastException.getCause().getMessage();
      }
      if (lastException instanceof OutOfMemoryError) {
        title = "Error";
        message = "OutOfMemoryError";
      }

      new AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialogInterface, int i) {
            Log.e(TAG, "OK clicked");
          }
        })
        .show();

      Log.e(TAG, "Dialog shown!");
    }

    lastException = null;
    Log.e(TAG, "lastException reset!");
  }
}
