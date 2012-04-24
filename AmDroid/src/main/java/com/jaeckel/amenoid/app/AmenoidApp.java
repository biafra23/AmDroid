package com.jaeckel.amenoid.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;

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
import com.jaeckel.amenoid.db.AmenDBAdapter;
import com.jaeckel.amenoid.db.AmenDao;

import android.app.AlertDialog;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * User: biafra
 * Date: 9/22/11
 * Time: 1:05 AM
 */
public class AmenoidApp extends Application {

  //TODO: set to false before release
  public final static boolean DEVELOPER_MODE = false;

  public static final String TAG                           = "AmenoidApp";
  protected static    String SINGLE_LOCATION_UPDATE_ACTION = "com.jaeckel.amen.SINGLE_LOCATION_UPDATE_ACTION";

  private static AmenoidApp instance;

  //  public final static
  private String      authCookie;
  private AmenService service;

  private Location         lastLocation;
  private LocationManager  locationManager;
  private LocationListener locationListener;
  private PendingIntent    singleUpatePI;
  private Criteria         criteria;
  private AmenDBAdapter    dbAdapter;

  private AmenDao amenDao;

  //CWAC

  private ThumbnailBus                                        bus   = new ThumbnailBus();
  private SimpleWebImageCache<ThumbnailBus, ThumbnailMessage> cache = new SimpleWebImageCache<ThumbnailBus, ThumbnailMessage>(null, null, 101, bus);
  private Typeface amenTypeThin;
  private Typeface amenTypeBold;

  private Handler handler = new Handler();
  private AlertDialog.Builder builder;

  private SharedPreferences prefs;
  private DefaultHttpClient amenHttpClient;
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
    if (DEVELOPER_MODE) {
      StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                                   .detectDiskReads()
                                   .detectDiskWrites()
                                   .detectNetwork()
                                   .penaltyDropBox()
                                   .penaltyLog()
                                   .build());
    }
    super.onCreate();

    dbAdapter = new AmenDBAdapter(this);
    dbAdapter.open();

    amenDao = dbAdapter.getAmenDao();

    prefs = PreferenceManager.getDefaultSharedPreferences(this);

    amenTypeThin = Typeface.createFromAsset(getAssets(), "fonts/AmenTypeThin.ttf");
    amenTypeBold = Typeface.createFromAsset(getAssets(), "fonts/AmenTypeBold.ttf");

    instance = this;
    builder = new AlertDialog.Builder(this);

//    handler = new Handler();
    Log.v(TAG, "onCreate");

    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    // Coarse accuracy is specified here to get the fastest possible result.
    // The calling Activity will likely (or have already) request ongoing
    // updates using the Fine location provider.
    criteria = new Criteria();
    criteria.setAccuracy(Criteria.ACCURACY_COARSE);

    // Construct the Pending Intent that will be broadcast by the oneshot
    // location update.
    Intent updateIntent = new Intent(SINGLE_LOCATION_UPDATE_ACTION);
    singleUpatePI = PendingIntent.getBroadcast(this, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    lastLocation = getLastBestLocation(100, 100);
    Log.d(TAG, "lastLocation: " + lastLocation);


    final String authToken = readAuthTokenFromPrefs(prefs);
    final User me = readMeFromPrefs(prefs);

    final String username = prefs.getString(Constants.PREFS_USER_NAME, null);
    final String password = prefs.getString(Constants.PREFS_PASSWORD, null);

    if (authToken != null && me != null) {
      Log.d(TAG, "configureAmenService: " + lastLocation);
      configureAmenService();
      service.init(authToken, me);

    } else if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {

      getService(username, password);

    } else {

      getService();
    }


  }

  private void configureAmenService() {
    InputStream in = getResources().openRawResource(R.raw.amenkeystore);
    amenHttpClient = new AmenHttpClient(in, "mysecret", "BKS");

//    amenHttpClient = new DefaultHttpClient();

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
//      Log.v(TAG, "isSignedIn -> " + true);
      return true;
    }
//    Log.v(TAG, "isSignedIn -> " + false);

//    Log.v(TAG, "service: " + service);
//    if (service != null) {
//      Log.v(TAG, "service.getAuthToken(): " + service.getAuthToken());
//
//    }

    return false;
  }

  public static AmenoidApp getInstance() {
    if (instance == null) {
      instance = new AmenoidApp();
    }
    return instance;
  }

  public AmenService getService(String username, String password) {
//    Log.d(TAG, "getService(" + username + ", " + password + ")");

    if (service == null || TextUtils.isEmpty(service.getAuthToken())) {
      Log.d(TAG, "service was null || no authToken");
      configureAmenService();
      try {

        service.init(username, password);

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

  //CWAC
//  void goBlooey(Throwable t) {
//
//    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//    Log.e(TAG, "Creating dialog");
//    builder
//      .setTitle(R.string.exception)
//      .setMessage(t.getMessage())
//      .setPositiveButton(R.string.ok, null)
//      .show();
//  }

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
    lastLocation = getLastBestLocation(100, 100);
    Log.d(TAG, "getLastLocation: " + lastLocation);
    return lastLocation;
  }

  public void setLastLocation(Location lastLocation) {
    this.lastLocation = lastLocation;
  }

  /**
   * Returns the most accurate and timely previously detected location.
   * Where the last result is beyond the specified maximum distance or
   * latency a one-off location update is returned via the {@link LocationListener}
   * specified in {@link }.
   *
   * @param minDistance Minimum distance before we require a location update.
   * @param minTime     Minimum time required between location updates.
   * @return The most accurate and / or timely previously detected location.
   */
  public Location getLastBestLocation(int minDistance, long minTime) {

    Log.v(TAG, "getLastBestLocation");
    Location bestResult = null;
    float bestAccuracy = Float.MAX_VALUE;
    long bestTime = Long.MIN_VALUE;

    // Iterate through all the providers on the system, keeping
    // note of the most accurate result within the acceptable time limit.
    // If no result is found within maxTime, return the newest Location.
    List<String> matchingProviders = locationManager.getAllProviders();
    for (String provider : matchingProviders) {
      Location location = locationManager.getLastKnownLocation(provider);
      if (location != null) {
        float accuracy = location.getAccuracy();
        long time = location.getTime();

        if ((time > minTime && accuracy < bestAccuracy)) {
          bestResult = location;
          bestAccuracy = accuracy;
          bestTime = time;
        } else if (time < minTime && bestAccuracy == Float.MAX_VALUE && time > bestTime) {
          bestResult = location;
          bestTime = time;
        }
      }
    }

    // If the best result is beyond the allowed time limit, or the accuracy of the
    // best result is wider than the acceptable maximum distance, request a single update.
    // This check simply implements the same conditions we set when requesting regular
    // location updates every [minTime] and [minDistance].
    if (locationListener != null && (bestTime < minTime || bestAccuracy > minDistance)) {
      IntentFilter locIntentFilter = new IntentFilter(SINGLE_LOCATION_UPDATE_ACTION);
      this.registerReceiver(singleUpdateReceiver, locIntentFilter);
      locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10 * 60 * 1000, 50.0F, singleUpatePI);
    }

    return bestResult;
  }

  /**
   * This {@link BroadcastReceiver} listens for a single location
   * update before unregistering itself.
   * The oneshot location update is returned via the {@link LocationListener}
   * specified in {@link }.
   */
  protected BroadcastReceiver singleUpdateReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {

      context.unregisterReceiver(singleUpdateReceiver);

      String key = LocationManager.KEY_LOCATION_CHANGED;
      Location location = (Location) intent.getExtras().get(key);

      Toast.makeText(AmenoidApp.this, "Receve Location update: " + location, Toast.LENGTH_LONG).show();

      if (locationListener != null && location != null)
        locationListener.onLocationChanged(location);

      locationManager.removeUpdates(singleUpatePI);
    }
  };

  /**
   * {@inheritDoc}
   */
  public void setChangedLocationListener(LocationListener l) {
    locationListener = l;
  }

  /**
   * {@inheritDoc}
   */
  public void cancel() {
    locationManager.removeUpdates(singleUpatePI);
  }

  public Typeface getAmenTypeThin() {
    return amenTypeThin;
  }

  public Typeface getAmenTypeBold() {
    return amenTypeBold;
  }

  public AmenDao getAmenDao() {
    return amenDao;
  }

  public void setAmenDao(AmenDao amenDao) {
    this.amenDao = amenDao;
  }
}
