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
package de.neofonie.mobile.unicorn.parser;

import java.util.HashMap;

import de.neofonie.mobile.unicorn.model.CacheEntry;

/**
 * Listener for the {@link AFCacheParser}
 * 
 * @author timmesserschmidt
 * 
 */
public interface AFCacheParserListener {
  /**
   * Will be executed if the {@link AFCacheParser} got finished successfully
   * 
   * @param result
   *          the {@link AFCacheParser}'s result
   */
  public void parserDone(HashMap<String, CacheEntry> result);

  /**
   * Will be executed if the {@link AFCacheParser} failed
   * 
   * @param e
   */
  public void parserError(Exception e);
}
