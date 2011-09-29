package com.jaeckel.amdroid.api;

import com.jaeckel.amdroid.api.model.Amen;
import com.jaeckel.amdroid.api.model.Dispute;
import com.jaeckel.amdroid.api.model.Objekt;
import com.jaeckel.amdroid.api.model.Statement;
import com.jaeckel.amdroid.api.model.Topic;
import com.jaeckel.amdroid.api.model.User;
import com.jaeckel.amdroid.api.model.UserInfo;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * User: biafra
 * Date: 9/23/11
 * Time: 9:06 PM
 */
public class AmenServiceITest extends TestCase {

  private String      username;
  private String      password;
  private AmenService service;

  @Override
  public void setUp() {
    Properties props = new Properties();
    try {
      props.load(this.getClass().getResourceAsStream("test.properties"));
    } catch (IOException e) {
      throw new RuntimeException("Properties not loaded", e);
    }

    username = props.getProperty("amenlib.tests.username");
    password = props.getProperty("amenlib.tests.password");

    if ("${tests.username}".equals(username)) {
      System.out.println("Please put username and password in settings.xml. See README ");
    }
//    System.out.println("username: " + username);
//    System.out.println("password: " + password);

    service = new AmenServiceImpl();
    service.init(username, password);
  }

  @Override
  public void tearDown() {

  }

  public void testInit() {

    assertTrue("session cookie null", service.getCookie() != null);
    assertTrue("session cookie too small", service.getCookie().length() > 10);

    assertTrue("session csrf token null", service.getCsrfToken() != null);
    assertTrue("session csrf token small", service.getCsrfToken().length() > 10);

  }

  public void testGetFeed() {


    List<Amen> amens = service.getFeed();
    assertEquals("Amen amount wrong", 25, amens.size());

    amens = service.getFeed(182126L, 3);
    assertEquals("Amen amount wrong", 3, amens.size());
    assertEquals("Amen with wrong id", 182125L, (long) amens.get(0).getId());

    amens = service.getFeed();
    assertEquals("Amen amount wrong", 25, amens.size());

  }

  public void testDispute() {


    Statement fooMe = service.getStatementForId(78221L);

    service.dispute(new Dispute(fooMe, "Bar"));

  }


  public void testGetUserInfo() {

    UserInfo ui = service.getUserInfo(new User(14028L));

    assertEquals("Wrong user for id", 14028L, (long) ui.getId());

  }

  public void testGetMe() {

    User u = service.getMe();

    assertEquals("Wrong user", 14028L, (long) u.getId());
    assertEquals("Wrong user", "Nancy Botwin", u.getName());

  }

  public void testAmen() {

    Statement statement = new Statement(new Objekt("Foo", Objekt.THING), new Topic("placeholder", true, "Ever"));
    service.addStatement(statement);

  }

  public void testGetAmenForId() {

    Amen a = service.getAmenForId(187365L);

    assertEquals("Wrong amen", 187365L, (long) a.getId());
  }

  public void testGetStatementForId() {

    Statement a = service.getStatementForId(78256L);

    assertEquals("Wrong amen", 78256L, (long) a.getId());
  }

  public void testGetStatementForId78704() {

    Statement a = service.getStatementForId(78704L);

    assertEquals("Wrong statement", 78704L, (long) a.getId());
    System.out.println("Statement: " + a);
  }

  public void testGetStatementForIdWithNullFirstPoster() {

    Statement a = service.getStatementForId(60814L);

    assertEquals("Wrong statement", 60814L, (long) a.getId());
  }

  public void testGetTopics() {

    Topic topic = service.getTopicsForId(29020L);

    assertEquals("Wrong topics", 29020L, topic.getId());
    assertNotNull("Topic has null rankedStatements", topic.getRankedStatements());
    assertTrue("Topic has no rankedStatements", topic.getRankedStatements().size() > 0);
  }

  public void testAmening() {


    Amen result = service.amen(188381L);

    assertNotNull("Result should not be null", result);
    assertEquals("Wrong user", "Nancy Botwin", result.getUser().getName());

    List<User> agreeing = result.getStatement().getAgreeingNetwork();
    boolean foundUser = false;
    for (User u : agreeing) {
      if ("Nancy Botwin".equals(u.getName())) {
        foundUser = true;
      }
    }
    assertTrue("Wrong user", foundUser);


  }

  public void testTakeBack() {

    System.out.println("testTakeBack");


    Amen a = service.amen(188381L);
    System.out.println("Got Back Amen: " + a);

    boolean result = service.takeBack(a);

    assertEquals("Wrong result", true, result);
  }


}
