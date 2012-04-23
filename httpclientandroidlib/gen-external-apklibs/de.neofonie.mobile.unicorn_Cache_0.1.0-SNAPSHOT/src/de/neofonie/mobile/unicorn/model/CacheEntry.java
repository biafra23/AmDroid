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
package de.neofonie.mobile.unicorn.model;

import java.util.Date;

/**
 * This class represents a single line of a cache file
 * 
 * @author tim.messerschmidt@neofonie.de
 * @author weiss@neofonie.de
 * 
 */
public class CacheEntry {
  private String filename;
  private Date   creationDate;
  private Date   expireDate;

  /**
   * A single line of a AFCache file
   */
  public CacheEntry() {}

  /**
   * A single line of a AFCache file
   * 
   * @param filename
   *          the entries file
   * @param creationDate
   *          the creation date of the file
   * @param expireDate
   *          the last valid date of the file
   */
  public CacheEntry(String filename, Date creationDate, Date expireDate) {
    this.filename = filename;
    this.creationDate = creationDate;
    this.expireDate = expireDate;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public Date getExpireDate() {
    return expireDate;
  }

  public void setExpireDate(Date expireDate) {
    this.expireDate = expireDate;
  }

  /**
   * This method is used to decide if the file is still valid
   * 
   * @return <code>true</code> if still valid
   */
  public boolean isEntryValid() {
    return isEntryValid(System.currentTimeMillis());
  }

  /**
   * This method is used to decide if the file is still valid at a provided time
   * 
   * @param timeStamp
   * @return <code>true</code> if still valid
   */
  public boolean isEntryValid(long timeStamp) {
    return this.expireDate.compareTo(new Date(timeStamp)) > 0;
  }
}
