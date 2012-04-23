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

/**
 * Simple {@link android.util.Log} wrapper that enables setting of log-levels via {@link Constants}.LOG_LEVEL
 * for Unicorn.
 * 
 * @author weiss@neofonie.de
 * 
 */
public class Log {

  /*////////////////////////////////////////////////////////////
   * WTF
   *////////////////////////////////////////////////////////////*

  public static void wtf(String tag, String msg) {
    android.util.Log.wtf(tag, msg);
  }

  public static void wtf(String tag, Throwable tr) {
    android.util.Log.wtf(tag, tr);
  }

  public static void wtf(String tag, String msg, Throwable tr) {
    android.util.Log.wtf(tag, msg, tr);
  }

  /*////////////////////////////////////////////////////////////
   * Verbose
   *////////////////////////////////////////////////////////////*

  public static void v(String tag, String msg) {
    if (isLoggable(android.util.Log.VERBOSE)) {
      android.util.Log.v(tag, msg);
    }
  }

  public static void v(String tag, String msg, Throwable tr) {
    if (isLoggable(android.util.Log.VERBOSE)) {
      android.util.Log.v(tag, msg, tr);
    }
  }

  public static void v(String tag, String msgFormat, Object... args) {
    if (isLoggable(android.util.Log.VERBOSE)) {
      android.util.Log.v(tag, String.format(msgFormat, args));
    }
  }

  public static void v(String tag, Throwable t, String msgFormat, Object... args) {
    if (isLoggable(android.util.Log.VERBOSE)) {
      android.util.Log.v(tag, String.format(msgFormat, args), t);
    }
  }

  /*////////////////////////////////////////////////////////////
   * Debug
   *////////////////////////////////////////////////////////////*

  public static void d(String tag, String msg) {
    if (isLoggable(android.util.Log.DEBUG)) {
      android.util.Log.d(tag, msg);
    }
  }

  public static void d(String tag, String msg, Throwable tr) {
    if (isLoggable(android.util.Log.DEBUG)) {
      android.util.Log.d(tag, msg, tr);
    }
  }

  public static void d(String tag, String msgFormat, Object... args) {
    if (isLoggable(android.util.Log.DEBUG)) {
      android.util.Log.d(tag, String.format(msgFormat, args));
    }
  }

  public static void d(String tag, Throwable t, String msgFormat, Object... args) {
    if (isLoggable(android.util.Log.DEBUG)) {
      android.util.Log.d(tag, String.format(msgFormat, args), t);
    }
  }

  /*////////////////////////////////////////////////////////////
   * Info
   *////////////////////////////////////////////////////////////*
  public static void i(String tag, String msg) {
    if (isLoggable(android.util.Log.INFO)) {
      android.util.Log.i(tag, msg);
    }
  }

  public static void i(String tag, String msg, Throwable tr) {
    if (isLoggable(android.util.Log.INFO)) {
      android.util.Log.i(tag, msg, tr);
    }
  }

  public static void i(String tag, String msgFormat, Object... args) {
    if (isLoggable(android.util.Log.INFO)) {
      android.util.Log.i(tag, String.format(msgFormat, args));
    }
  }

  public static void i(String tag, Throwable t, String msgFormat, Object... args) {
    if (isLoggable(android.util.Log.INFO)) {
      android.util.Log.i(tag, String.format(msgFormat, args), t);
    }
  }

  /*////////////////////////////////////////////////////////////
   * Warn
   *////////////////////////////////////////////////////////////*

  public static void w(String tag, String msg) {
    if (isLoggable(android.util.Log.WARN)) {
      android.util.Log.w(tag, msg);
    }
  }

  public static void w(String tag, Throwable tr) {
    if (isLoggable(android.util.Log.WARN)) {
      android.util.Log.w(tag, tr);
    }
  }

  public static void w(String tag, String msg, Throwable tr) {
    if (isLoggable(android.util.Log.WARN)) {
      android.util.Log.w(tag, msg, tr);
    }
  }

  public static void w(String tag, String msgFormat, Object... args) {
    if (isLoggable(android.util.Log.WARN)) {
      android.util.Log.w(tag, String.format(msgFormat, args));
    }
  }

  public static void w(String tag, Throwable t, String msgFormat, Object... args) {
    if (isLoggable(android.util.Log.WARN)) {
      android.util.Log.w(tag, String.format(msgFormat, args), t);
    }
  }

  /*////////////////////////////////////////////////////////////
   * Error
   *////////////////////////////////////////////////////////////*
  public static void e(String tag, String msg) {
    if (isLoggable(android.util.Log.ERROR)) {
      android.util.Log.e(tag, msg);
    }
  }

  public static void e(String tag, String msg, Throwable tr) {
    if (isLoggable(android.util.Log.ERROR)) {
      android.util.Log.e(tag, msg, tr);
    }
  }

  public static void e(String tag, Throwable t, String msgFormat, Object... args) {
    if (isLoggable(android.util.Log.ERROR)) {
      android.util.Log.e(tag, String.format(msgFormat, args), t);
    }
  }

  private static boolean isLoggable(int currentLogLevel) {
    return Constants.LOG_LEVEL <= currentLogLevel;
  }

}
