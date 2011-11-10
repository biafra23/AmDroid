package com.jaeckel.amenoid.app;

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
import android.util.Log;
import android.widget.Toast;
import com.jaeckel.amenoid.R;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.AmenServiceImpl;
import com.jaeckel.amenoid.cwac.cache.SimpleWebImageCache;
import com.jaeckel.amenoid.cwac.thumbnail.ThumbnailBus;
import com.jaeckel.amenoid.cwac.thumbnail.ThumbnailMessage;

import java.io.IOException;
import java.util.List;

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


  //CWAC

  private ThumbnailBus                                        bus   = new ThumbnailBus();
  private SimpleWebImageCache<ThumbnailBus, ThumbnailMessage> cache = new SimpleWebImageCache<ThumbnailBus, ThumbnailMessage>(null, null, 101, bus);
  private Typeface amenTypeThin;
  private Typeface amenTypeBold;

  private Handler handler = new Handler();
  private AlertDialog.Builder builder;

  private SharedPreferences prefs;

  public AmenoidApp() {
    super();
//    Thread.setDefaultUncaughtExceptionHandler(onBlooey);
//    this.context = context;

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

  }

  public static AmenoidApp getInstance() {
    if (instance == null) {
      instance = new AmenoidApp();
    }
    return instance;
  }

  public AmenService
  getService(String username, String password) {

    if (service == null || service.getAuthToken() == null) {

      service = new AmenServiceImpl();
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
      service = new AmenServiceImpl();

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




}
