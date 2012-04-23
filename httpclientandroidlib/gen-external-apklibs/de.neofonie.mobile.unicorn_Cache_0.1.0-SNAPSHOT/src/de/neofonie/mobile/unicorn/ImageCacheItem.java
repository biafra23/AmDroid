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

import java.lang.ref.SoftReference;

import android.graphics.Bitmap;

public class ImageCacheItem {

  public SoftReference<Bitmap> bitmapReference;
  public long                  lastDownload;

  public ImageCacheItem(Bitmap image) {
    setImage(image);
  }

  public void setImage(Bitmap image) {
    lastDownload = System.currentTimeMillis();
    if (image != null) {
      bitmapReference = new SoftReference<Bitmap>(image);
    } else {
      bitmapReference = null;
    }
  }

}
