/*
 * (c) Neofonie Mobile GmbH
 * 
 * This computer program is the sole property of Neofonie Mobile GmbH (http://mobile.neofonie.de)
 * and is protected under the German Copyright Act (paragraph 69a UrhG).
 * 
 * All rights are reserved. Making copies, duplicating, modifying, using or distributing
 * this computer program in any form, without prior written consent of Neofonie Mobile GmbH, is prohibited.
 * Violation of copyright is punishable under the German Copyright Act (paragraph 106 UrhG).
 * 
 * Removing this copyright statement is also a violation.
 */
package de.neofonie.mobile.unicorn;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import de.neofonie.mobile.unicorn.util.IOUtil;
import de.neofonie.mobile.unicorn.util.LooperFactory;

/**
 * @author tobias <fiebiger@neofonie.de>
 * 
 */
public final class ImageCache {

  private final static String             TAG                 = "ImageCache";

  private static final int                MSG_NOTIFY_CALLBACK = 1001;
  private static final int                MSG_LOAD_IMAGE      = 1002;

  private static final long               NOTIFY_DELAY        = 100;

  private long                            retryThreshold      = 1000 * 3600 * 3; //three hours
  private int                             callbackInterval    = 3;
  private int                             loadCount           = 0;

  private int                             loadDelay           = 0;
  private int                             wipeLimit           = 0;

  private HashMap<String, ImageCacheItem> cache;
  private PersistantUrlCache              persistanceCache;
  private HttpClient                      client;

  private BackgroundHandler               backgroundHandler;
  private ForegroundHandler               foregroundHandler;

  private ArrayList<String>               blacklist;
  private ArrayList<ImageCacheCallback>   delegates;
  private BitmapFactory.Options           defaultOptions;

  public enum Status {
    CACHED, // get image using the getImage method
    LOADING, // show progress and potentially register a listener
    FAILED // downloading or recaching of the image failed, handle this
  }

  /**
   * Constructor
   */
  public ImageCache(HttpClient httpClient, PersistantUrlCache urlCache) {
    this.cache = new HashMap<String, ImageCacheItem>();
    this.client = httpClient;
    this.backgroundHandler = new BackgroundHandler(LooperFactory.getBackgroundLooper(TAG));
    this.foregroundHandler = new ForegroundHandler();
    this.persistanceCache = urlCache;
    this.blacklist = new ArrayList<String>();
    this.delegates = new ArrayList<ImageCacheCallback>();
    defaultOptions = new BitmapFactory.Options();
    defaultOptions.inInputShareable = true;
    defaultOptions.inPurgeable = true;
  }

  /**
   * get a image from the imagecache
   * 
   * @param url
   * @return
   */
  public Bitmap getImage(String url) {
    Bitmap image = null;

    ImageCacheItem item = cache.get(url);
    if (item != null && item.bitmapReference != null) {
      image = item.bitmapReference.get();
    }

    return image;
  }

  /**
   * load a image into the bitmap cache from the filesystem or network
   * 
   * @param url
   * @return
   */
  public Status loadImage(String url) {
    Status actualStatus = null;
    if (cache.containsKey(url)) {
      ImageCacheItem item = cache.get(url);
      if (item.bitmapReference == null) {
        if ((System.currentTimeMillis() - item.lastDownload) > retryThreshold) {
          actualStatus = Status.LOADING; // retry image downloading after threshold
        } else {
          actualStatus = Status.FAILED; // image could not be downloaded
        }
      } else {
        if (item.bitmapReference.get() == null) {
          actualStatus = Status.LOADING; // image was cached before but was flushed
        } else {
          actualStatus = Status.CACHED; // image is available in cache
        }
      }
    } else {
      // image was not cached before, try to load it
      actualStatus = Status.LOADING;
    }

    if (actualStatus == Status.LOADING) {
      Message fetchMessage = backgroundHandler.obtainMessage(MSG_LOAD_IMAGE, url);
      if (loadDelay > 0) {
        backgroundHandler.sendMessageDelayed(fetchMessage, loadDelay);
      } else {
        backgroundHandler.sendMessage(fetchMessage);
      }
    }

    return actualStatus;
  }

  /**
   * Loads a {@link Bitmap} from the url and notifies the handler after it finished.
   * 
   * @param url
   *          An url pointing to a {@link Bitmap}
   */
  private void load(String url) {
    Bitmap image = persistanceCache != null ? persistanceCache.getBitmap(url) : null;

    if (image == null) {
      Log.d(TAG, "image is null. Downloading a new version.");
      image = downloadImage(url);
    }

    ImageCacheItem cacheItem = cache.get(url);
    if (cacheItem == null) {
      Log.d(TAG, "cacheItem is null... creating a new one.");
      cacheItem = new ImageCacheItem(image);
    } else {
      Log.d(TAG, "Found a cacheItem... setting image");
      cacheItem.setImage(image);
    }
    cache.put(url, cacheItem);
    loadCount++;

    if (!backgroundHandler.hasMessages(MSG_LOAD_IMAGE) || loadCount >= callbackInterval) {
      Log.d(TAG, "Notifying handlers");
      foregroundHandler.sendEmptyMessageDelayed(MSG_NOTIFY_CALLBACK, NOTIFY_DELAY);
      loadCount = 0;
    }
  }

  /**
   * download an image which is stored in the persistant cache if available and return it as bitmap
   * 
   * @param url
   *          The url pointing to the {@link Bitmap}.
   * @return The {@link Bitmap} created.
   */
  private synchronized Bitmap downloadImage(String url) {
    Bitmap image = null;
    Log.d(TAG, "Downloading new image...");
    if (!TextUtils.isEmpty(url) && !blacklist.contains(url)) {
      Log.d(TAG, "url is ok.");
      InputStream in = null;
      try {
        HttpGet getUrl = new HttpGet(url);
        AndroidHttpClient.modifyRequestToAcceptGzipResponse(getUrl);
        HttpResponse resp = client.execute(getUrl);
        in = AndroidHttpClient.getUngzippedContent(resp.getEntity());

        if (in != null) {
          Log.d(TAG, "input is ok.");
          ByteArrayOutputStream out = new ByteArrayOutputStream(8 * 1024);
          IOUtil.copy(in, out);
          image = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size(), defaultOptions);
          if (persistanceCache != null) {
            Log.d(TAG, "adding image to persistanceCache.");
            persistanceCache.addData(url, out.toByteArray());
          }
        }

      } catch (Exception e) {
        Log.d(TAG, "Error while downloading Bitmap", e);
      } finally {
        if (in != null) {
          try {
            in.close();
          } catch (IOException e) {
            Log.w(TAG, "Could not close connection.", e);
          }
        }
      }
    }
    return image;
  }

  /**
   * perform a cleanup of the image cache
   * 
   * @param withGc
   *          indicate if you want a System.GC() after the cleanup
   */
  public void cleanup(boolean withGc) {
    Log.e(TAG, "performing cleanup");
    cache.clear();
    if (withGc) {
      System.gc();
    }
  }

  /**
   * remove all future downloads from the queue
   */
  public void cancelAllDownloads() {
    backgroundHandler.removeMessages(MSG_LOAD_IMAGE);
  }

  /**
   * remove an item from the cache manually
   * 
   * @param url
   *          The item to remove
   */
  public void removeItem(String url) {
    cache.remove(url);
    // if (item != null && item.bitmapReference != null) {
    // Bitmap b = item.bitmapReference.get();
    // if (b != null) {
    // b.recycle();
    // item = null;
    // }
    // }
  }

  /**
   * notify all callbacks that the image cache loaded a new
   */
  private void notifyDelegates() {
    try {
      for (ImageCacheCallback callback : delegates) {
        if (callback != null) {
          callback.imageCacheChanged();
        }
      }
    } catch (ConcurrentModificationException cme) {
      Log.d(TAG, "a callback may have been removed while notify in progress", cme);
    }
  }

  /**
   * add a callback which get's notyfied if the cache loads or reloads an image
   * 
   * @param callback
   */
  public void addCallback(ImageCacheCallback callback) {
    delegates.add(callback);
  }

  /**
   * remove a callback which get's notyfied if the cache loads or reloads an image
   * 
   * @param callback
   *          The {@link ImageCacheCallback} you want to remove.
   */
  public void removeCallback(ImageCacheCallback callback) {
    delegates.remove(callback);
  }

  /**
   * Sets the Time Span after which the Image cache try's to download an image from the Network again
   * 
   * @param threshold
   *          The threshold in ms.
   */
  public void setDownloadRetryThreshold(long threshold) {
    this.retryThreshold = threshold;
  }

  /**
   * adds the given url so that the url is not downloaded from the network
   * 
   * @param url
   *          The url you want to add to the blacklist.
   */
  public void addUrlToBlacklist(String url) {
    blacklist.add(url);
  }

  /**
   * removes the given url from the blacklist
   * 
   * @param url
   *          The url you want to remove from the blacklist.
   */
  public void removeUrlFromBlacklist(String url) {
    blacklist.remove(url);
  }

  /**
   * Put a Bitmap with the given key in the imageCache. The Bitmap is not cached on the Filesystem
   * 
   * @param key
   *          The key you want to use (usually the url it origined from)
   * @param value
   *          The {@link Bitmap} you want to put.
   */
  public void putBitmap(String key, Bitmap value) {
    cache.put(key, new ImageCacheItem(value));
  }

  /**
   * gets a bitmap directly from the cache without recaching and downloading
   * 
   * @param key
   *          The key you want to look for.
   * @return Either a {@link Bitmap} if it exists or null.
   */
  public Bitmap getBitmap(String key) {
    ImageCacheItem item = cache.get(key);

    if (item != null && item.bitmapReference != null) {
      return item.bitmapReference.get();
    }
    return null;
  }

  /**
   * the interval after how much image loads the callback is notyfied
   * 
   * @param interval
   */
  public void setCallbackInterval(int interval) {
    callbackInterval = interval;
  }

  public int getLoadDelay() {
    return loadDelay;
  }

  public void setLoadDelay(int delay) {
    this.loadDelay = delay;
  }

  public int getWipeLimit() {
    return wipeLimit;
  }

  public void setWipeLimit(int wipeLimit) {
    this.wipeLimit = wipeLimit;
  }

  public void wipe() {
    if (wipeLimit > 0 && cache.size() > wipeLimit) {
      cleanup(false);
    }
  }

  protected void _handleMessage(Message msg) {
    if (msg != null) {
      switch (msg.what) {
        case MSG_LOAD_IMAGE:
          backgroundHandler.removeMessages(MSG_LOAD_IMAGE, msg.obj);
          load((String) msg.obj);
          break;
        case MSG_NOTIFY_CALLBACK:
          foregroundHandler.removeMessages(MSG_NOTIFY_CALLBACK);
          notifyDelegates();
          break;
      }
    }
  }

  private class BackgroundHandler extends Handler {

    public BackgroundHandler(Looper looper) {
      super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
      _handleMessage(msg);
    }
  }

  private class ForegroundHandler extends Handler {

    public ForegroundHandler() {
      super(Looper.getMainLooper());
    }

    @Override
    public void handleMessage(Message msg) {
      _handleMessage(msg);
    }
  }

}
