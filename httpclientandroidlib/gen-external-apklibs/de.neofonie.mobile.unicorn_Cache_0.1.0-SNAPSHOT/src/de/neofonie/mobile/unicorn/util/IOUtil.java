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
package de.neofonie.mobile.unicorn.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtil {

  public static void copyStream(InputStream inStr, OutputStream outStr, int bufferSize) throws IOException {
    byte[] buf = new byte[bufferSize];
    for (;;) {
      int got = inStr.read(buf, 0, bufferSize);
      if (got < 0) {
        break;
      } else if (got > 0) {
        outStr.write(buf, 0, got);
      }
    }
    inStr.close();
  }

  /**
   * The name says it all.
   */
  private static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

  /**
   * Copy bytes from an InputStream to an OutputStream.
   * 
   * @param input
   *          the InputStream to read from
   * @param output
   *          the OutputStream to write to
   * @return the number of bytes copied
   * @throws IOException
   *           In case of an I/O problem
   */
  public static int copy(InputStream input, OutputStream output) throws IOException {
    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
    int count = 0;
    int n = 0;
    while (-1 != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
      count += n;
    }
    return count;
  }

}
