package com.jaeckel.amdroid.api;

import com.jaeckel.amdroid.api.model.Amen;
import junit.framework.TestCase;

import java.util.List;

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

    assertTrue("session cookie null", service.getCookie() != null);
    assertTrue("session cookie too small", service.getCookie().length() > 10);

    assertTrue("session csrf token null", service.getCsrfToken() != null);
    assertTrue("session csrf token small", service.getCsrfToken().length() > 10);

  }

  public void testGetFeed() {

    AmenService service = new AmenServiceImpl();

    service.init("nbotvin@different.name", "foobar23");
    List<Amen>amens = service.getFeed();
    assertEquals("Amen amount wrong", 25, amens.size());

    amens = service.getFeed(182126L, 3);
    assertEquals("Amen amount wrong", 3, amens.size());
    assertEquals("Amen with wrong id", 182125L, amens.get(0).getId() );

    amens = service.getFeed();
    assertEquals("Amen amount wrong", 25, amens.size());

  }
}
