package com.jaeckel.amdroid.app;

import android.app.Application;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.AmenServiceImpl;
import com.jaeckel.amdroid.api.model.User;
import com.jaeckel.amdroid.cwac.cache.SimpleWebImageCache;
import com.jaeckel.amdroid.cwac.thumbnail.ThumbnailBus;
import com.jaeckel.amdroid.cwac.thumbnail.ThumbnailMessage;

/**
 * User: biafra
 * Date: 9/22/11
 * Time: 1:05 AM
 */
public class AmdroidApp extends Application {

  //TODO: set to false before release
  public final static boolean DEVELOPER_MODE = false;

  public static final String TAG = "amdroid/AmdroidApp";
  private static AmdroidApp instance;

  //  public final static
  private String      authCookie;
  private AmenService service;

  private Location        currentLocation;
  private LocationManager locationManager;

  //CWAC

  private ThumbnailBus                                        bus   = new ThumbnailBus();
  private SimpleWebImageCache<ThumbnailBus, ThumbnailMessage> cache = new SimpleWebImageCache<ThumbnailBus, ThumbnailMessage>(null, null, 101, bus);
  private User me;

  private Handler handler;

//  public AmdroidApp() {
//
//    Thread.setDefaultUncaughtExceptionHandler(onBlooey);
//
//
//  }

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

    handler = new Handler();

    Log.v(TAG, "onCreate");

//    Location bestResult;
//    long bestAccuracy = 0;
//    long bestTime = 0;
//    long minTime = 0;
//    locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//    List<String> matchingProviders = locationManager.getAllProviders();
//    for (String provider: matchingProviders) {
//      Location location = locationManager.getLastKnownLocation(provider);
//      if (location != null) {
//        float accuracy = location.getAccuracy();
//        long time = location.getTime();
//
//        if ((time > minTime && accuracy < bestAccuracy)) {
//          bestResult = location;
//          bestAccuracy = accuracy;
//          bestTime = time;
//        }
//        else if (time < minTime &&
//                 bestAccuracy == Float.MAX_VALUE && time > bestTime){
//          bestResult = location;
//          bestTime = time;
//        }
//      }
//    }
//
//    currentLocation = bestResult;

  }

  public static AmdroidApp getInstance() {
    if (instance == null) {
      instance = new AmdroidApp();
    }
    return instance;
  }

  public String getAuthCookie() {
    return authCookie;
  }

  public void setAuthCookie(String authCookie) {
    this.authCookie = authCookie;
  }

  public AmenService getService(String username, String password) {
    if (service == null) {
      service = new AmenServiceImpl();

      service.init(username, password);
      me = service.getMe();
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

//  private Thread.UncaughtExceptionHandler onBlooey =
//    new Thread.UncaughtExceptionHandler() {
//      public void uncaughtException(Thread thread, final Throwable ex) {
//        Log.e(TAG, "Uncaught exception", ex);
//
//        handler.post(new Runnable() {
//          public void run() {
//            AlertDialog.Builder builder = new AlertDialog.Builder(AmdroidApp.this);
//            Log.e(TAG, "Creating dialog");
//            builder
//              .setTitle(R.string.exception)
//              .setMessage(ex.getMessage())
//              .setPositiveButton(R.string.ok, null)
//              .show();
//            Log.e(TAG, "Created dialog");
//          }
//        });
//      }
//    };

  public User getMe() {
    return me;
  }
}
