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
package de.neofonie.mobile.unicorn.files;

/**
 * Listener for the {@link FileExtractor}
 * 
 * @author tim.messerschmidt
 * 
 */
public interface FileExtractorListener {
  /**
   * Will be executed if the {@link FileExtractor} got finished successfully
   * 
   * @param result
   *          the {@link FileExtractor}'s result
   */
  public <E> void done(E result);

  /**
   * Will be executed if the {@link FileExtractor} failed
   * 
   * @param e
   */
  public void error(Exception e);
}
