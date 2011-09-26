package com.jaeckel.amdroid.app;

import android.app.AlertDialog;
import android.app.Application;
import android.util.Log;
import com.jaeckel.amdroid.R;
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

  public static final String TAG = "amdroid/AmdroidApp";
  private static AmdroidApp instance;

  //  public final static
  private String      authCookie;
  private AmenService service;

  //CWAC

  private        ThumbnailBus                                        bus   = new ThumbnailBus();
  private        SimpleWebImageCache<ThumbnailBus, ThumbnailMessage> cache = new SimpleWebImageCache<ThumbnailBus, ThumbnailMessage>(null, null, 101, bus);
  private User me;


  public AmdroidApp() {

    Thread.setDefaultUncaughtExceptionHandler(onBlooey);


  }

  @Override
  public void onCreate() {

    Log.v(TAG, "onCreate");


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

  //CWAC
  void goBlooey(Throwable t) {
 		AlertDialog.Builder builder=new AlertDialog.Builder(this);

 		builder
 			.setTitle(R.string.exception)
 			.setMessage(t.toString())
 			.setPositiveButton(R.string.ok, null)
 			.show();
 	}

 	ThumbnailBus getBus() {
 		return(bus);
 	}

 	public SimpleWebImageCache<ThumbnailBus, ThumbnailMessage> getCache() {
 		return(cache);
 	}

 	private Thread.UncaughtExceptionHandler onBlooey=
 		new Thread.UncaughtExceptionHandler() {
 		public void uncaughtException(Thread thread, Throwable ex) {
 			Log.e(TAG, "Uncaught exception", ex);
 			goBlooey(ex);
 		}
 	};

  public User getMe() {
    return me;
  }
}
