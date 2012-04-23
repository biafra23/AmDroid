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

/**
 * This Exception must be used if the requested file is not downloadable
 * 
 * @author tim.messerschmidt@neofonie.de
 * 
 */
public class FileNotCachableException extends Exception {
  private static final long serialVersionUID = 171491945768477074L;
  private String            resourceUrl;

  /**
   * Constructs a new exception with the specified detail message. The cause is not initialized.
   * 
   * @param message
   *          the detail message
   */
  public FileNotCachableException(String message, String resourceUrl) {
    super(message);
    this.resourceUrl = resourceUrl;
  }

  /**
   * Constructs a new exception with the specified detail message and cause.
   * 
   * @param message
   *          the detail message
   * @param cause
   *          the cause
   */
  public FileNotCachableException(String message, String resourceUrl, Exception cause) {
    super(message);
    this.initCause(cause);
    this.resourceUrl = resourceUrl;
  }

  /**
   * This method returns the files url
   * 
   * @return the which is not in the Cache
   */
  public String getResourceUrl() {
    return resourceUrl;
  }
}
