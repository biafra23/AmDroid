package com.jaeckel.amenoid.util;

public class Log {
  /*////////////////////////////
   * VERBOSE
   *////////////////////////////
  public static void v(String msg) {
    v(Config.LOG_TAG, msg);
  }

  public static void v(String msg, Throwable tr) {
    v(Config.LOG_TAG, msg, tr);
  }

  public static void v(String msgFormat, Object... args) {
    v(Config.LOG_TAG, msgFormat, args);
  }

  public static void v(Throwable t, String msgFormat, Object... args) {
    v(Config.LOG_TAG, t, msgFormat, args);
  }

  public static void v(String tag, String msg) {
    if (Config.LOG_LEVEL <= android.util.Log.VERBOSE) {
      android.util.Log.v(tag, msg);
    }
  }

  public static void v(String tag, String msg, Throwable tr) {
    if (Config.LOG_LEVEL <= android.util.Log.VERBOSE) {
      android.util.Log.v(tag, msg, tr);
    }
  }

  public static void v(String tag, String msgFormat, Object... args) {
    if (Config.LOG_LEVEL <= android.util.Log.VERBOSE) {
      android.util.Log.v(tag, String.format(msgFormat, args));
    }
  }

  public static void v(String tag, Throwable t, String msgFormat, Object... args) {
    if (Config.LOG_LEVEL <= android.util.Log.VERBOSE) {
      android.util.Log.v(tag, String.format(msgFormat, args), t);
    }
  }

  /*////////////////////////////
   * DEBUG
   *////////////////////////////
  public static void d(String msg) {
    d(Config.LOG_TAG, msg);
  }

  public static void d(String msg, Throwable tr) {
    d(Config.LOG_TAG, msg, tr);
  }

  public static void d(String msgFormat, Object... args) {
    d(Config.LOG_TAG, msgFormat, args);
  }

  public static void d(Throwable t, String msgFormat, Object... args) {
    d(Config.LOG_TAG, t, msgFormat, args);
  }

  public static void d(String tag, String msg) {
    if (Config.LOG_LEVEL <= android.util.Log.DEBUG) {
      android.util.Log.d(tag, msg);
    }
  }

  public static void d(String tag, String msg, Throwable tr) {
    if (Config.LOG_LEVEL <= android.util.Log.DEBUG) {
      android.util.Log.d(tag, msg, tr);
    }
  }

  public static void d(String tag, String msgFormat, Object... args) {
    if (Config.LOG_LEVEL <= android.util.Log.DEBUG) {
      android.util.Log.d(tag, String.format(msgFormat, args));
    }
  }

  public static void d(String tag, Throwable t, String msgFormat, Object... args) {
    if (Config.LOG_LEVEL <= android.util.Log.DEBUG) {
      android.util.Log.d(tag, String.format(msgFormat, args), t);
    }
  }

  /*////////////////////////////
   * INFO
   *////////////////////////////
  public static void i(String msg) {
    i(Config.LOG_TAG, msg);
  }

  public static void i(String msg, Throwable tr) {
    i(Config.LOG_TAG, msg, tr);
  }

  public static void i(String msgFormat, Object... args) {
    i(Config.LOG_TAG, msgFormat, args);
  }

  public static void i(Throwable t, String msgFormat, Object... args) {
    i(Config.LOG_TAG, msgFormat, args);
  }

  public static void i(String tag, String msg) {
    if (Config.LOG_LEVEL <= android.util.Log.INFO) {
      android.util.Log.i(tag, msg);
    }
  }

  public static void i(String tag, String msg, Throwable tr) {
    if (Config.LOG_LEVEL <= android.util.Log.INFO) {
      android.util.Log.i(tag, msg, tr);
    }
  }

  public static void i(String tag, String msgFormat, Object... args) {
    if (Config.LOG_LEVEL <= android.util.Log.INFO) {
      android.util.Log.i(tag, String.format(msgFormat, args));
    }
  }

  public static void i(String tag, Throwable t, String msgFormat, Object... args) {
    if (Config.LOG_LEVEL <= android.util.Log.INFO) {
      android.util.Log.i(tag, String.format(msgFormat, args), t);
    }
  }

  /*////////////////////////////
   * WARN
   *////////////////////////////
  public static void w(String msg) {
    w(Config.LOG_TAG, msg);
  }

  public static void w(Throwable tr) {
    w(Config.LOG_TAG, tr);
  }

  public static void w(String msgFormat, Object... args) {
    w(Config.LOG_TAG, msgFormat, args);
  }

  public static void w(Throwable t, String msgFormat, Object... args) {
    w(Config.LOG_TAG, t, msgFormat, args);
  }

  public static void w(String tag, String msg) {
    if (Config.LOG_LEVEL <= android.util.Log.WARN) {
      android.util.Log.w(tag, msg);
    }
  }

  public static void w(String tag, Throwable tr) {
    if (Config.LOG_LEVEL <= android.util.Log.WARN) {
      android.util.Log.w(tag, tr);
    }
  }

  public static void w(String tag, String msg, Throwable tr) {
    if (Config.LOG_LEVEL <= android.util.Log.WARN) {
      android.util.Log.w(tag, msg, tr);
    }
  }

  public static void w(String tag, String msgFormat, Object... args) {
    if (Config.LOG_LEVEL <= android.util.Log.WARN) {
      android.util.Log.w(tag, String.format(msgFormat, args));
    }
  }

  public static void w(String tag, Throwable t, String msgFormat, Object... args) {
    if (Config.LOG_LEVEL <= android.util.Log.WARN) {
      android.util.Log.w(tag, String.format(msgFormat, args), t);
    }
  }

  /*////////////////////////////
   * ERROR
   *////////////////////////////
  public static void e(String msg) {
    e(Config.LOG_TAG, msg);
  }

  public static void e(String msg, Throwable tr) {
    e(Config.LOG_TAG, msg, tr);
  }

  public static void e(Throwable t, String msgFormat, Object... args) {
    e(Config.LOG_TAG, t, msgFormat, args);
  }

  public static void e(String tag, String msg) {
    if (Config.LOG_LEVEL <= android.util.Log.ERROR) {
      android.util.Log.e(tag, msg);
    }
  }

  public static void e(String tag, String msg, Throwable tr) {
    if (Config.LOG_LEVEL <= android.util.Log.ERROR) {
      android.util.Log.e(tag, msg, tr);
    }
  }

  public static void e(String tag, Throwable t, String msgFormat, Object... args) {
    if (Config.LOG_LEVEL <= android.util.Log.ERROR) {
      android.util.Log.e(tag, String.format(msgFormat, args), t);
    }
  }
}



