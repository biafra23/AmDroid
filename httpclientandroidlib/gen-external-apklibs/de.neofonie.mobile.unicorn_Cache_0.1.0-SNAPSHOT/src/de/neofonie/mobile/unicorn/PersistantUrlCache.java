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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.ref.SoftReference;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.protocol.HTTP;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.webkit.WebResourceResponse;
import de.neofonie.mobile.unicorn.download.DownloadToCache;
import de.neofonie.mobile.unicorn.download.DownloadToCacheListener;
import de.neofonie.mobile.unicorn.download.FileNotCachableException;
import de.neofonie.mobile.unicorn.handler.CacheHandlerListener;
import de.neofonie.mobile.unicorn.model.CacheEntry;
import de.neofonie.mobile.unicorn.parser.AFCacheParser;
import de.neofonie.mobile.unicorn.parser.AFCacheParserListener;

/**
 * Unicorn / PersistantUrlCache <br>
 * <br>
 * 
 * Enables caching of data on the filesystem. This class originates from the Android-Utils project.
 * 
 * @author fiebiger@neofonie.de
 * @author georgi@neofonie.de
 * @author weiss@neofonie.de
 * @author tim.messerschmidt@neofonie.de
 * 
 */
public class PersistantUrlCache implements AFCacheParserListener, DownloadToCacheListener {
  private static final String         TAG = PersistantUrlCache.class.getSimpleName();
  private static PersistantUrlCache   INSTANCE;

  private String                      basePath;
  private BitmapFactory.Options       defaultOptions;
  private HashMap<String, CacheEntry> cacheEntries;
  private CacheHandlerListener        listener;

  /**
   * Returns a single instance of {@link PersistantUrlCache}
   * 
   * @param basePath
   *          the cache's path
   * @return The {@link PersistantUrlCache}'s instance
   */
  public static PersistantUrlCache getInstance(String basePath, CacheHandlerListener listener) {
    if (INSTANCE == null) {
      INSTANCE = new PersistantUrlCache(basePath, listener);
    }
    return INSTANCE;
  }

  /**
   * Creates a {@link PersistantUrlCache} for a provided path.
   * 
   * @param basePath
   *          The path you want to use for caching.
   */
  private PersistantUrlCache(String basePath, CacheHandlerListener listener) {
    this.basePath = basePath;
    this.defaultOptions = new BitmapFactory.Options();
    this.defaultOptions.inInputShareable = true;
    this.defaultOptions.inPurgeable = true;
    this.listener = listener;
    initCacheEntries();
  }

  /**
   * Initializes the HashMap holding the entries
   */
  private void initCacheEntries() {
    cacheEntries = new HashMap<String, CacheEntry>();
  }

  /**
   * This method shall be used to check if a url got cached alredy
   * 
   * @param url
   *          The url to get from cache
   */
  public void addToCache(String url) {
    new DownloadToCache(basePath, url, this).start();
  }

  /**
   * This method starts parsing a specific AFCache file
   * 
   * @param pathToCacheFile
   */
  public void parseAFCache(String pathToCacheFile) {
    new AFCacheParser(pathToCacheFile, this).start();
  }

  /**
   * Adds a single {@link CacheEntry} to the HashMap
   * 
   * @param entry
   *          The {@link CacheEntry} to add
   */
  public void addToCache(CacheEntry entry) {
    cacheEntries.put(entry.getFilename(), entry);
  }

  /**
   * Adds a whole {@link HashMap} to the existant HashMap
   * 
   * @param cacheEntries
   *          The {@link HashMap} to add
   */
  public void addToCache(HashMap<String, CacheEntry> cacheEntries) {
    this.cacheEntries.putAll(cacheEntries);
  }

  /**
   * Adds data to the cache.
   * 
   * @param url
   *          The url associated with this entry.
   * @param data
   *          The data you want to store.
   */
  public void addData(String url, byte[] data) {
    addData(url, data, System.currentTimeMillis());
  }

  /**
   * Adds data to the cache and sets the timestamp of last modification.
   * 
   * @param url
   *          The url associated with this entry.
   * @param data
   *          The data you want to store.
   * @param lastModified
   *          The timestamp of last modification.
   */
  public void addData(String url, byte[] data, long lastModified) {
    try {
      File f = new File(CacheUtils.getFullCachePath(getBasePath(), url), CacheUtils.getFilename(url));
      f.setLastModified(lastModified);
      Log.d(TAG, "Path created was: " + f.getParentFile().mkdirs());
      Log.d(TAG, "Tried to create path: " + f.getPath());
      Log.d(TAG, "Can be written is: " + f.canWrite());
      FileOutputStream fos = new FileOutputStream(f);
      fos.write(data);
      fos.close();
    } catch (FileNotFoundException e) {
      Log.e(TAG, "Could not write to Filesystem, the URL was: " + url, e);
    } catch (IOException e) {
      Log.e(TAG, "Could not add Data to local cache, the URL was: " + url);
    }
  }

  /**
   * Tells you whether data for an url exists in the cache.
   * 
   * @param url
   *          The url you want to check.
   * @return <code>true</code> if the cache contains data for this entry, else <code>false</code>.
   */
  public boolean contains(String url) {
    File file = new File(CacheUtils.getFullCachePath(getBasePath(), url), CacheUtils.getFilename(url));
    boolean fileExists = file.exists();
    boolean inHashMap = cacheEntries.containsKey(url);
    return fileExists || inHashMap;
  }

  /**
   * Creates a new {@link WebResourceResponse} from a given url by analyzing it's MimeType.
   * 
   * @param url
   *          the file's url
   * @return a new {@link WebResourceResponse}
   */
  public WebResourceResponse getCachedFile(String url) {
    String mimeType = null;
    if (CacheUtils.isHTML(url) || CacheUtils.isDefault(url)) {
      mimeType = Constants.MIME_TYPE_HTML;
    } else if (CacheUtils.isCSS(url)) {
      mimeType = Constants.MIME_TYPE_CSS;
    } else if (CacheUtils.isJPEG(url)) {
      mimeType = Constants.MIME_TYPE_JPEG;
    } else if (CacheUtils.isPNG(url)) {
      mimeType = Constants.MIME_TYPE_PNG;
    } else if (CacheUtils.isJS(url)) {
      mimeType = Constants.MIME_TYPE_JS;
    } else if (CacheUtils.isMPEG(url)) {
      mimeType = Constants.MIME_TYPE_MPEG;
    }
    return getWebResourceResponse(mimeType, url);
  }

  /**
   * Creates a new {@link WebResourceResponse} from a given url and MimeType.
   * 
   * @param mime
   *          The file's MimeType
   * @param url
   *          The file's url
   * @return a new {@link WebResourceResponse} or <code>null</code> if no MimeType provided
   */
  private static WebResourceResponse getWebResourceResponse(String mime, String url) {
    if (mime == null) {
      return null;
    }
    return new WebResourceResponse(mime, HTTP.UTF_8, INSTANCE.getStream(url));
  }

  /**
   * Gets a cached {@link Bitmap}.
   * 
   * @param url
   *          The url associated with this {@link Bitmap}.
   * @return The cached {@link Bitmap}. If none was found or an {@link OutOfMemoryError} occurred
   *         <code>null</code> will be returned.
   */
  public Bitmap getBitmap(String url) {
    try {
      return BitmapFactory.decodeFile(url, defaultOptions);
    } catch (OutOfMemoryError oome) {
      Log.e(TAG, "got OutOfMemoryError");
      defaultOptions.requestCancelDecode();
    }
    return null;
  }

  /**
   * Gets a cached {@link Bitmap} as {@link SoftReference} in order to avoid {@link OutOfMemoryError}.
   * 
   * @param url
   *          The url associated with this {@link Bitmap}.
   * @return The cached {@link Bitmap} as {@link SoftReference}.
   */
  public SoftReference<Bitmap> getSoftReferencedBitmap(String url) {
    return new SoftReference<Bitmap>(getBitmap(url));
  }

  /**
   * Gets the {@link Object} associated with the provided url.
   * 
   * @param url
   *          The url corresponding to the cached {@link Object}.
   * @return The cached {@link Object} or null.
   */
  public Object getObject(String url) {
    try {
      return new ObjectInputStream(new FileInputStream(new File(CacheUtils.getFullCachePath(getBasePath(),
                                                                                            url),
                                                                CacheUtils.getFilename(url)))).readObject();
    } catch (FileNotFoundException fnfe) {
      Log.i(TAG, "File not Found");
    } catch (IOException ioe) {
      Log.e(TAG, "Error while reading", ioe);
    } catch (ClassNotFoundException cnfe) {
      Log.e(TAG, "Didn't find class", cnfe);
    }
    return null;
  }

  /**
   * Gets the {@link Object} associated with the provided url as a {@link SoftReference}.
   * 
   * @param url
   *          The url corresponding to the cached {@link Object}.
   * @return The cached {@link Object} as {@link SoftReference}.
   */
  public SoftReference<Object> getSoftReferencedObject(String url) {
    return new SoftReference<Object>(getObject(url));
  }

  /**
   * Gets the stored data for an provided url.
   * 
   * @param url
   *          The url associated with the data you want to get.
   * @return An {@link InputStream} if any was found, else <code>null</code>. <b>You'll need to close that
   *         stream yourself</b>.
   */
  @SuppressWarnings("deprecation")
  public InputStream getStream(String url) {
    try {
      File cacheFile = new File(CacheUtils.getFullCachePath(getBasePath(), url), CacheUtils.getFilename(url));
      CacheEntry cacheEntry = cacheEntries.get(url);
      if (cacheEntry != null && cacheEntry.isEntryValid(cacheFile.lastModified())) {
        System.out.println("File is not valid anymore");
      }
      if (cacheFile.exists()) {
        System.out.println("Last modified: " + new Date(cacheFile.lastModified()).toLocaleString());
      }
      return new FileInputStream(cacheFile);
    } catch (FileNotFoundException fnfe) {
      Log.i(TAG, "File not Found");
    }
    return null;
  }

  /**
   * Convenience method for calling <code>cleanup(System.currentTimeMillis());</code>
   */
  public void cleanup() {
    cleanup(System.currentTimeMillis());
  }

  /**
   * Removes files from the cache that are older than <code>thresholdTimeStamp</code> from the cache.
   * 
   * @param thresholdTimeStamp
   *          The maximum age of a cached file you want to keep.
   */
  public void cleanup(long thresholdTimeStamp) {
    File f = new File(basePath);
    if (f.listFiles() == null) {
      throw new NullPointerException("No cache existing for " + basePath);
    }
    for (File entry : f.listFiles()) {
      if (entry.lastModified() < thresholdTimeStamp) {
        entry.delete();
      }
    }
    for (CacheEntry e : cacheEntries.entrySet().toArray(new CacheEntry[cacheEntries.size()])) {
      if (!e.isEntryValid(thresholdTimeStamp)) {
        cacheEntries.remove(e.getFilename());
      }
    }
  }

  /**
   * Removes the file associated with this url from the cache if it exists. Else it fails silently.
   * 
   * @param url
   *          The url of the file you want to remove.
   */
  public void remove(String url) {
    File f = new File(CacheUtils.getFullCachePath(getBasePath(), url), CacheUtils.getFilename(url));
    if (f.exists() && f.canWrite()) {
      f.delete();
      cacheEntries.remove(url);
    }
  }

  /**
   * Returns the Cache's BasePath
   * 
   * @return basePath The BasePath of the Cache
   */
  public String getBasePath() {
    return basePath;
  }

  /*///////////////////////////////////////////////////////////////
   * Listener implementations
   *///////////////////////////////////////////////////////////////

  /**
   * Get's called after all the entries from a single AFCache file got parsed
   */
  public void parserDone(HashMap<String, CacheEntry> result) {
    addToCache(result);
  }

  /**
   * Gets called if an error happens while parsing
   */
  public void parserError(Exception e) {
    e.printStackTrace();
  }

  public void downloadDone(String result) {
    if (listener != null) {
      listener.cachingDone(result);
    }
  }

  public void downloadError(FileNotCachableException e) {
    e.printStackTrace();
  }
}
