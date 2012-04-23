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
package de.neofonie.mobile.unicorn.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

import de.neofonie.mobile.unicorn.CacheUtils;

/**
 * A Thread that is able to handle the requests
 * 
 * @author tim.messerschmidt@neofonie.de
 * 
 */
public class CacheHandler extends Thread {
  private static Queue<String>            requestUrls;
  private static ArrayList<CacheHandler>  requests;
  private ArrayList<CacheHandlerListener> listeners;
  private String                          resourceUrl;
  private String                          storagePath;

  /**
   * @param storagePath
   * 
   * @param resourceUrl
   * 
   * @param listener
   *          the {@link CacheHandlerListener}
   */
  public CacheHandler(String storagePath, String resourceUrl, CacheHandlerListener listener) {
    if (requestUrls == null) {
      requestUrls = new PriorityQueue<String>();
    }
    if (requests == null) {
      requests = new ArrayList<CacheHandler>();
    }
    if (listeners == null) {
      listeners = new ArrayList<CacheHandlerListener>();
    }
    this.storagePath = storagePath;
    this.resourceUrl = resourceUrl;
    if (!requestUrls.contains(resourceUrl)) {
      requestUrls.add(resourceUrl);
      requests.add(this);
    } else {
      listeners.add(listener);
    }
  }

  @Override
  public void run() {
    File storageDir = new File(storagePath);
    if (!storageDir.exists()) {
      for (CacheHandlerListener listener : listeners) {
        listener.cachingError(new CacheNotFoundException("Storage does not exist: " + storagePath,
                                                         resourceUrl));
      }
    } else {
      String folder = CacheUtils.getPath(resourceUrl);
      File cacheDir = new File(storageDir, folder);
      if (!cacheDir.exists()) {
        for (CacheHandlerListener listener : listeners) {
          listener.cachingError(new CacheNotFoundException("Folder not in cache: " + folder, resourceUrl));
        }
      } else {
        String filename = CacheUtils.getFilename(resourceUrl);
        File cacheFile = new File(cacheDir, filename);
        if (!cacheFile.exists()) {
          for (CacheHandlerListener listener : listeners) {
            listener.cachingError(new CacheNotFoundException("File not in cache: " + filename, resourceUrl));
          }
        } else {
          for (CacheHandlerListener listener : listeners) {
            listener.cachingDone(cacheFile.getAbsolutePath());
          }
        }
      }
    }
    listeners.clear();
  }
}
