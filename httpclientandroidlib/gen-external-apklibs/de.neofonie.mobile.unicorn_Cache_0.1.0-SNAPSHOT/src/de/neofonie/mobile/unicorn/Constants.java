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
 * Library wide constants for Unicorn
 * 
 * @author weiss@neofonie.de
 * @author tim.messerschmidt@neofonie.de
 */
public class Constants {
  /**
   * Set the log-level for the cache
   */
  protected static int       LOG_LEVEL      = android.util.Log.DEBUG;
  /*
   * MIME-TYPES for {@link WebResourceResponse}
   */
  public static final String MIME_TYPE_CSS  = "text/css";
  public static final String MIME_TYPE_HTML = "text/html";
  public static final String MIME_TYPE_JS   = "text/javascript";
  public static final String MIME_TYPE_PNG  = "image/png";
  public static final String MIME_TYPE_JPEG = "image/jpeg";
  public static final String MIME_TYPE_MPEG = "video/mpeg";
  /*
   * Default name for no-file urls
   */
  public static final String NO_FILE        = "unicorn.default";
}
