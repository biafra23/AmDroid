package com.jaeckel.amenoid.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import com.jaeckel.amenoid.R;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * User: biafra
 * Date: 10/23/11
 * Time: 3:39 AM
 */

public abstract class AmenLibTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

  private Throwable lastException = null;
  private Activity context;
  protected static String TAG = "AmenLibTask";

  public AmenLibTask(Activity context) {
    this.context = context;
  }

  @Override
  final protected Result doInBackground(Params... params) {

    try {
      return wrappedDoInBackground(params);

    } catch (Throwable e) {

      lastException = e;
      Log.e(TAG, "ERROR occured: ", e);
    }
    return null;
  }

  protected abstract Result wrappedDoInBackground(Params... params) throws IOException;

  protected abstract void wrappedOnPostExecute(Result result);

  final protected void onPostExecute(Result result) {

    if (lastException != null) {
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
      if (lastException instanceof UnknownHostException) {
        title = "Unknown  Host";
        message = lastException.getMessage();
      }
      if (context.hasWindowFocus()) {

        new AlertDialog.Builder(context)
          .setTitle(title)
          .setMessage(message)
          .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
              Log.e(TAG, "OK clicked");
            }
          }).setNegativeButton("Crash", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialogInterface, int i) {
            throw new RuntimeException(lastException);
          }
        })
          .show();

        Log.e(TAG, "Dialog shown!");

      }
    }

    wrappedOnPostExecute(result);

    lastException = null;
//    Log.e(TAG, "lastException reset!");


  }


}
