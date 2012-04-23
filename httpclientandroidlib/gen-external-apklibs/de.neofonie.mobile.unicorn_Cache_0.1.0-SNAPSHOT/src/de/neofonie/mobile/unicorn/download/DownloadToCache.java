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
package de.neofonie.mobile.unicorn.download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import de.neofonie.mobile.unicorn.CacheUtils;

/**
 * This Thread can be used to download a file into the cache.
 * 
 * @author tim.messerschmidt@neofonie.de
 * 
 */
public class DownloadToCache extends Thread {
  private String                  folder;
  private String                  resourceUrl;
  private DownloadToCacheListener listener;

  /*
   * Streams
   */
  private InputStream             is  = null;
  private BufferedInputStream     bis = null;
  private FileOutputStream        fos = null;
  private BufferedOutputStream    bos = null;

  /**
   * Creates a new instance of {@link DownloadToCache}
   * 
   * @param folder
   *          the cache's folder
   * @param resourceUrl
   *          the url of the resource (will be the path on sdcard)
   * @param listener
   *          the {@link DownloadToCacheListener} to be notified on events
   */
  public DownloadToCache(String folder, String resourceUrl, DownloadToCacheListener listener) {
    this.folder = folder;
    this.resourceUrl = resourceUrl;
    this.listener = listener;
  }

  @Override
  public void run() {
    File cacheDir = new File(folder);
    if (!cacheDir.exists()) {
      cacheDir.mkdirs();
    }

    String cachePath = CacheUtils.getPath(resourceUrl);
    File path = new File(cacheDir, cachePath);
    if (!path.exists()) {
      path.mkdirs();
    }

    String filename = CacheUtils.getFilename(resourceUrl);
    File cacheFile = new File(path, filename);

    try {
      URL url = new URL(resourceUrl);
      URLConnection connection = url.openConnection();
      is = connection.getInputStream();
      bis = new BufferedInputStream(is);
      fos = new FileOutputStream(cacheFile);
      bos = new BufferedOutputStream(fos);
      int i;
      while ((i = bis.read()) != -1) {
        bos.write(i);
      }
      listener.downloadDone(cacheFile.getAbsolutePath());
    } catch (MalformedURLException e) {
      e.printStackTrace();
      listener.downloadError(new FileNotCachableException("MalformedURLException", resourceUrl));
    } catch (IOException e) {
      e.printStackTrace();
      listener.downloadError(new FileNotCachableException("IOException", resourceUrl));
    }

    closeStreams();
  }

  /**
   * Used to close open streams. Does not rely on the finally statement which is said to be incalculably.
   */
  private void closeStreams() {
    if (bos != null) {
      try {
        bos.flush();
        bos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (fos != null) {
      try {
        fos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (bis != null) {
      try {
        bis.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (is != null) {
      try {
        is.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
