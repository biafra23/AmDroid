package com.jaeckel.amenoid.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaeckel.amenoid.Constants;
import com.jaeckel.amenoid.R;
import com.jaeckel.amenoid.api.AmenHttpClient;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.AmenServiceImpl;
import com.jaeckel.amenoid.api.model.DateSerializer;
import com.jaeckel.amenoid.api.model.User;
import com.jaeckel.amenoid.cwac.cache.SimpleWebImageCache;
import com.jaeckel.amenoid.cwac.thumbnail.ThumbnailBus;
import com.jaeckel.amenoid.cwac.thumbnail.ThumbnailMessage;

import com.jaeckel.amenoid.util.Config;
import com.jaeckel.amenoid.util.Log;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;

import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;

/**
 * User: biafra
 * Date: 9/22/11
 * Time: 1:05 AM
 */
public class AmenoidApp extends Application {

  public static boolean DEVELOPER_MODE = false;

  public static final String TAG                           = "AmenoidApp";
  protected static    String SINGLE_LOCATION_UPDATE_ACTION = "com.jaeckel.amen.SINGLE_LOCATION_UPDATE_ACTION";

  private static AmenoidApp instance;

  //  public final static
  private String      authCookie;
  private AmenService service;

  //CWAC

  private ThumbnailBus bus = new ThumbnailBus();
  private SimpleWebImageCache<ThumbnailBus, ThumbnailMessage> cache;
  private Typeface                                            amenTypeThin;
  private Typeface                                            amenTypeBold;

  private Handler handler = new Handler();
  private AlertDialog.Builder builder;

  private SharedPreferences prefs;
  private DefaultHttpClient amenHttpClient;

  private Location lastLocation;

  private Gson gson = new GsonBuilder()
    .registerTypeAdapter(Date.class, new DateSerializer())
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .serializeNulls()
    .create();

  public AmenoidApp() {
    super();
  }

  public DefaultHttpClient getAmenHttpClient() {
    return amenHttpClient;
  }

  public void setAmenHttpClient(DefaultHttpClient amenHttpClient) {
    this.amenHttpClient = amenHttpClient;
  }

  @Override
  public void onCreate() {

    this.cache = new SimpleWebImageCache<ThumbnailBus, ThumbnailMessage>(null, null, 101, bus);
    Config.init(getApplicationContext());

    DEVELOPER_MODE = getResources().getBoolean(R.bool.debuggable);

    if (getResources().getBoolean(R.bool.strict)) {
      StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                                   .detectDiskReads()
                                   .detectDiskWrites()
                                   .detectNetwork()
                                   .penaltyDropBox()
                                   .penaltyLog()
//                                   .penaltyDialog()
                                   .build());
    }

    super.onCreate();

    prefs = PreferenceManager.getDefaultSharedPreferences(this);

    amenTypeThin = Typeface.createFromAsset(getAssets(), "fonts/AmenTypeThin.ttf");
    amenTypeBold = Typeface.createFromAsset(getAssets(), "fonts/AmenTypeBold.ttf");

    instance = this;
    builder = new AlertDialog.Builder(this);

//    handler = new Handler();
    Log.v(TAG, "onCreate");

    final String authToken = readAuthTokenFromPrefs(prefs);
    final User me = readMeFromPrefs(prefs);

    String email = prefs.getString(Constants.PREFS_EMAIL, null);
    if (TextUtils.isEmpty(email)) {
      //For legacy users that have the email in the username field
      email = prefs.getString(Constants.PREFS_USER_NAME, "");
      if (!TextUtils.isEmpty(email) && email.contains("@")) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PREFS_EMAIL, email);
        editor.commit();
      }
    }


    final String password = prefs.getString(Constants.PREFS_PASSWORD, null);

    if (authToken != null && me != null) {
      configureAmenService();
      service.init(authToken, me);

    } else if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

      getService(email, password);

    } else {

      getService();
    }


  }

  private void configureAmenService() {
    InputStream in = getResources().openRawResource(R.raw.amenkeystore);
    amenHttpClient = new AmenHttpClient(in, "mysecret", "BKS");
    service = new AmenServiceImpl(amenHttpClient);
  }

  private String readAuthTokenFromPrefs(SharedPreferences preferences) {
    return preferences.getString(Constants.PREFS_AUTH_TOKEN, null);
  }

  private User readMeFromPrefs(SharedPreferences preferences) {
    final String prefsString = preferences.getString(Constants.PREFS_ME, null);
    User u = null;
    if (prefsString != null) {
      u = gson.fromJson(prefsString, User.class);
    }

    return u;
  }

  public boolean isSignedIn() {

    if (service != null && !TextUtils.isEmpty(service.getAuthToken())) {
      return true;
    }
    return false;
  }

  public static AmenoidApp getInstance() {
    if (instance == null) {
      instance = new AmenoidApp();
    }
    return instance;
  }

  public AmenService getService(String email, String password) {
//    Log.d(TAG, "getService(" + email + ", " + password + ")");

    if (service == null || TextUtils.isEmpty(service.getAuthToken())) {
      Log.d(TAG, "service was null || no authToken");
      configureAmenService();
      try {

        service.init(email, password);

      } catch (IOException e) {
        e.printStackTrace();
      }


    }
    return service;
  }

  public AmenService getService() {
    if (service == null) {

      final String authToken = readAuthTokenFromPrefs(prefs);
      final User me = readMeFromPrefs(prefs);
      configureAmenService();

      if (!TextUtils.isEmpty(authToken) && me != null) {
        service.init(authToken, me);

      } else {
        //use amen anonymously
        service.init("", (User) null);
      }
    }
    return service;
  }


  ThumbnailBus getBus() {
    return (bus);
  }

  public SimpleWebImageCache<ThumbnailBus, ThumbnailMessage> getCache() {

    return (cache);
  }

  private Thread.UncaughtExceptionHandler onBlooey =

    new Thread.UncaughtExceptionHandler() {

      public void uncaughtException(Thread thread, final Throwable ex) {
        Log.e(TAG, "Uncaught exception", ex);
        ex.printStackTrace();
        if (ex.getCause() != null) {
          Log.e(TAG, "CAUSE", ex.getCause());
          ex.getCause().printStackTrace();
        }
        handler.post(new Runnable() {
          public void run() {

            Log.e(TAG, "Creating dialog");
            builder
              .setTitle(R.string.exception)
              .setMessage(ex.getMessage())
              .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                  Log.d(TAG, "OK clicked");
                }
              })
              .show();
            Log.e(TAG, "Created dialog");
          }
        });
      }
    };


  public Location getLastLocation() {
    return lastLocation;
  }

  public void setLastLocation(Location lastLocation) {
    this.lastLocation = lastLocation;
  }


  public Typeface getAmenTypeThin() {
    return amenTypeThin;
  }

  public Typeface getAmenTypeBold() {
    return amenTypeBold;
  }

}
