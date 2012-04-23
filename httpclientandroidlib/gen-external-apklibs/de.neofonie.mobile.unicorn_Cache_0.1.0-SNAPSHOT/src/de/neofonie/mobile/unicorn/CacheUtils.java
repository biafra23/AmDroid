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

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * @author tim.messerschmidt@neofonie.de
 */
public class CacheUtils {
  /**
   * Gets the filename by cutting of the string until the last slash
   *
   * @param url the full url
   * @return the filename
   */
  public static String getFilename(String url) {
    String filename = (String) url.subSequence(url.lastIndexOf("/") + 1, url.length());
    if (filename.length() == 0) {
      filename = Constants.NO_FILE;
    }
    return filename;
  }

  /**
   * Gets the full path to the file by cutting of everything beginning at the last slash
   *
   * @param url the full url
   * @return the path
   */
  public static String getPath(String url) {
    return ((String) url.subSequence(0, url.lastIndexOf("/"))).replace("http://", "");
  }

  /**
   * Gets the full cache path
   *
   * @param basePath The cache's base path
   * @param url      The provided url which serves as file
   * @return the combined path
   */
  public static String getFullCachePath(String basePath, String url) {
    return basePath.concat(getPath(url));
  }

  /**
   * Creates a directory if it didn't exist before. Else it does nothing.
   *
   * @param cacheDir The directory path you want to create.
   * @return <code>true</code> when the cache dir has been created or already exists, else <code>false</code>.
   */
  public static boolean createCacheDirIfNotExists(String cacheDir) {
    File cache = new File(cacheDir);
    if (!cache.exists()) {
      return cache.mkdirs();
    }
    return true;
  }

  /*//////////////////////////////////////////
   * Methods for the file suffixes
   *//////////////////////////////////////////

  /**
   * Returns if the provided file/url is a no-file url
   *
   * @param url The file's url
   * @return <code>true</code> if no-file or <code>false</code> if not
   */
  public static boolean isDir(String url) {
    return url.endsWith("/");
  }

  /**
   * Returns if the provided file/url is a no-file url
   *
   * @param url The file's url
   * @return <code>true</code> if no-file or <code>false</code> if not
   */
  public static boolean isDefault(String url) {
    return url.endsWith(".default");
  }

  /**
   * Returns if the provided file is an html file
   *
   * @param url The file's url
   * @return <code>true</code> if html or <code>false</code> if not
   */
  public static boolean isHTML(String url) {
    return url.endsWith(".htm") || url.endsWith(".html") || url.endsWith(".shtml");
  }

  /**
   * Returns if the provided file is a jpeg file
   *
   * @param url The file's url
   * @return <code>true</code> if a jpeg or <code>false</code> if not
   */
  public static boolean isJPEG(String url) {
    return url.endsWith(".jpeg") || url.endsWith(".jpg");
  }

  /**
   * Returns if the provided file is a png file
   *
   * @param url The file's url
   * @return <code>true</code> if a png or <code>false</code> if not
   */
  public static boolean isPNG(String url) {
    return url.endsWith(".png");
  }

  /**
   * Returns if the provided file is a javascript file
   *
   * @param url The file's url
   * @return <code>true</code> if a javascript or <code>false</code> if not
   */
  public static boolean isJS(String url) {
    return url.endsWith(".js");
  }

  /**
   * Returns if the provided file is a css file
   *
   * @param url The file's url
   * @return <code>true</code> if a css or <code>false</code> if not
   */
  public static boolean isCSS(String url) {
    return url.endsWith(".css");
  }

  /**
   * Returns if the provided file is a mpeg file
   *
   * @param url The file's url
   * @return <code>true</code> if a mpeg or <code>false</code> if not
   */
  public static boolean isMPEG(String url) {
    return url.endsWith(".mpg") || url.endsWith(".mpeg") || url.endsWith(".mpe") || url.endsWith(".mp4");
  }


  /**
   * Find the default directory for caching. External if possible otherwise internal memory is used.
   *
   * @param context
   * @return
   */
  public static String findDefaultCacheDir(Context context) {
    final String state = Environment.getExternalStorageState();
    String cacheDir;

    if (Environment.MEDIA_MOUNTED.equals(state)) {
      cacheDir = context.getExternalCacheDir().getPath();
    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
      // We can only read the media
      cacheDir = context.getCacheDir().getPath();
    } else {
      // Something else is wrong. It may be one of many other states, but all we need
      //  to know is we can neither read nor write
      // using internal cacheDir
      cacheDir = context.getCacheDir().getPath();
    }
    return cacheDir;
  }

}
