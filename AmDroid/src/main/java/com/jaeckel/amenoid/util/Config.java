package com.jaeckel.amenoid.util;

import com.jaeckel.amenoid.R;

import android.content.Context;

public class Config {
  public static final  String LOG_TAG         = "Amenoid";
  private static final int    LOG_LEVEL_DEBUG = android.util.Log.DEBUG;
  private static final int    LOG_LEVEL_WARN  = android.util.Log.WARN;

  public static int    LOG_LEVEL;
  public static Config INSTANCE;

  public static void init(Context context) {
    if (context != null && INSTANCE == null) {
      INSTANCE = new Config(context);
    }
  }

  public Config(Context context) {
    String log = context.getString(R.string.log);
    if (log.equals("WARN")) {
      LOG_LEVEL = LOG_LEVEL_WARN;
    } else if (log.equals("DEBUG")) {
      LOG_LEVEL = LOG_LEVEL_DEBUG;
    }
  }
}


