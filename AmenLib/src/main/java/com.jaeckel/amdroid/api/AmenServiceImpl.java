package com.jaeckel.amdroid.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: biafra
 * Date: 9/23/11
 * Time: 8:35 PM
 */
public class AmenServiceImpl implements AmenService{

  final Logger log = LoggerFactory.getLogger("Amen");
  private String authName;
  private String authPassword;
  private Object serviceUrl;

  @Override
  public AmenService init(String authName, String authPassword) {
//        log.trace("------------------------------------------------------------------------------------");
//        log.debug("------------------------------------------------------------------------------------");
//        log.info("------------------------------------------------------------------------------------");
//        log.error("------------------------------------------------------------------------------------");

      System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
      System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
      System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
      System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");

      this.serviceUrl = "https://getamen.com/";
      this.authName = authName;
      this.authPassword = authPassword;
      return this;
  }
}
