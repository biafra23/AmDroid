package de.neofonie.mobile.unicorn.util;

import java.util.HashMap;

import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;

/**
 * 
 * @author tobias <fiebiger@neofonie.de
 * 
 */
public class LooperFactory {

  private static final int                      DEFAULT_BACKGROUND_PRIO = Process.THREAD_PRIORITY_LOWEST;

  private static HashMap<String, HandlerThread> handlerThreadPool;

  public static Looper getBackgroundLooper(String name) {
    return getLooper(name, DEFAULT_BACKGROUND_PRIO);
  }

  public synchronized static Looper getLooper(String name, int prio) {
    if (handlerThreadPool == null) {
      handlerThreadPool = new HashMap<String, HandlerThread>();
    }

    HandlerThread t = handlerThreadPool.get(name);
    if (t == null) {
      t = createHandlerThread(name, prio);
      handlerThreadPool.put(name, t);
    }

    return t.getLooper();
  }

  private static HandlerThread createHandlerThread(String name, int prio) {
    HandlerThread t = new HandlerThread(name, prio);
    t.setDaemon(true);
    t.start();
    return t;
  }

}
