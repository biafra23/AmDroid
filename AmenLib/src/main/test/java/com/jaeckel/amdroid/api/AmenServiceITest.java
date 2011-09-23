package com.jaeckel.amdroid.api;

import junit.framework.TestCase;

/**
 * User: biafra
 * Date: 9/23/11
 * Time: 9:06 PM
 */
public class AmenServiceITest extends TestCase {

  @Override
  public void setUp() {

  }
  @Override
  public void tearDown() {

  }

  public void testInit() {

    AmenService service = new AmenServiceImpl();

    service.init("nbotvin@different.name", "foobar23");


    
  }
}
