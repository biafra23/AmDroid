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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.neofonie.mobile.unicorn.model.CacheEntry;

/**
 * This Thread is able to parse the provided afcache files.
 * 
 * @author tim.messerschmidt@neofonie.de
 * 
 */
public class AFCacheParser extends Thread {
  // Date format provided by the afcache file entries
  private static final DateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",
                                                                          Locale.ENGLISH);
  private File                    cacheFile;
  private AFCacheParserListener   listener;

  /**
   * Creates a new instance of the AFCacheParser
   * 
   * @param pathToCacheFile
   *          the path to the afcache file
   * @param listener
   *          the {@link AFCacheParserListener} to be notified about events
   */
  public AFCacheParser(String pathToCacheFile, AFCacheParserListener listener) {
    this.cacheFile = new File(pathToCacheFile);
    this.listener = listener;
  }

  @Override
  public void run() {
    if (cacheFile.exists()) {
      FileInputStream fis;
      try {
        fis = new FileInputStream(cacheFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line;
        HashMap<String, CacheEntry> result = new HashMap<String, CacheEntry>();
        while ((line = br.readLine()) != null) {
          CacheEntry entry = getEntryFromLine(line);
          result.put(entry.getFilename(), entry);
        }
        listener.parserDone(result);
      } catch (IOException e) {
        e.printStackTrace();
        listener.parserError(e);
      }
    }
  }

  /**
   * Creates a new {@link CacheEntry} from a single line of the AFCache Manifest
   * 
   * @param line
   *          one of the entries to be parsed
   * @return a new instance of AFCacheEntry
   */
  public CacheEntry getEntryFromLine(String line) {
    String[] entry = line.split(";");
    String filename = entry[0].trim();
    String creationString = entry[1].trim();
    String expirationString = entry[2].trim();
    Date creationDate = null;
    Date expirationDate = null;
    try {
      creationDate = simpleDateFormat.parse(creationString);
    } catch (ParseException e) {
      e.printStackTrace();
      listener.parserError(e);
    }
    try {
      expirationDate = simpleDateFormat.parse(expirationString);
    } catch (ParseException e) {
      e.printStackTrace();
      listener.parserError(e);
    }
    return new CacheEntry(filename, creationDate, expirationDate);
  }
}
