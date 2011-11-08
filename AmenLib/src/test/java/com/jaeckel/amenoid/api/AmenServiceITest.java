package com.jaeckel.amenoid.api;

import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.api.model.Objekt;
import com.jaeckel.amenoid.api.model.Statement;
import com.jaeckel.amenoid.api.model.Topic;
import com.jaeckel.amenoid.api.model.User;
import com.jaeckel.amenoid.api.model.UserInfo;
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
  public void setUp() throws IOException {
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

  public void testGetFeed() throws IOException {


    List<Amen> amens = service.getFeed(AmenService.FEED_TYPE_FOLLOWING);
    assertEquals("Amen amount wrong", 25, amens.size());

    amens = service.getFeed(182126L, 3, AmenService.FEED_TYPE_FOLLOWING);
    assertEquals("Amen amount wrong", 3, amens.size());
    assertEquals("Amen with wrong id", 182125L, (long) amens.get(0).getId());

    amens = service.getFeed(AmenService.FEED_TYPE_FOLLOWING);
    assertEquals("Amen amount wrong", 25, amens.size());

  }

  public void testDispute() throws IOException {


    Statement fooMe = service.getStatementForId(78221L);

    final Objekt objekt = new Objekt("Bar", AmenService.OBJEKT_KIND_THING);

    final Amen bar = new Amen(fooMe, objekt, 185874L);
    
    Long amenId = service.dispute(bar);

    assertNotNull(amenId);
    service.takeBack(amenId);
    

  }

  public void testDispute2() throws IOException {


    Statement fooMe = service.getStatementForId(87702L);

    final Objekt objekt = new Objekt("Zur Fetten Ecke", AmenService.OBJEKT_KIND_THING);

    final Amen bar = new Amen(fooMe, objekt, 216522L);

    Long amenId = service.dispute(bar);

    assertNotNull(amenId);
    service.takeBack(amenId);


  }

  public void testGetUserInfo() throws IOException {

    UserInfo ui = service.getUserInfo(14028L);

    assertEquals("Wrong user for id", 14028L, (long) ui.getId());

  }

  public void testGetMe() {

    User u = service.getMe();

    assertEquals("Wrong user", 14028L, (long) u.getId());
    assertEquals("Wrong user", "Nancy Botwin", u.getName());

  }

  public void testAmen() throws IOException {

    Statement statement = new Statement(new Objekt("Foo", AmenService.OBJEKT_KIND_THING), new Topic("placeholder", true, "Ever"));
    service.addStatement(statement);

  }

  public void testGetAmenForId() throws IOException {

    Amen a = service.getAmenForId(187365L);

    assertEquals("Wrong amen", 187365L, (long) a.getId());
  }

  public void testGetStatementForId() throws IOException {

    Statement a = service.getStatementForId(78256L);

    assertEquals("Wrong amen", 78256L, (long) a.getId());
  }

  public void testGetStatementForId78704() throws IOException {

    Statement a = service.getStatementForId(78704L);

    assertEquals("Wrong statement", 78704L, (long) a.getId());
    System.out.println("Statement: " + a);
  }

  public void testGetStatementForIdWithNullFirstPoster() throws IOException {

    Statement a = service.getStatementForId(60814L);

    assertEquals("Wrong statement", 60814L, (long) a.getId());
  }

  public void testGetTopics() throws IOException {

    Topic topic = service.getTopicsForId(29020L, null);

    assertEquals("Wrong topics", (Long)29020L, topic.getId());
    assertNotNull("Topic has null rankedStatements", topic.getRankedStatements());
    assertTrue("Topic has no rankedStatements", topic.getRankedStatements().size() > 0);
  }

  public void testAmening() throws IOException {


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

  public void testTakeBack() throws IOException {

    System.out.println("testTakeBack");


    Amen a = service.amen(188381L);
    System.out.println("Got Back Amen: " + a);

    boolean result = service.takeBack(a.getStatement().getId());

    assertEquals("Wrong result", true, result);
  }

  public void testFollowers() throws IOException {
    System.out.println("testFollowers");


    List<User> followers = service.followers(12665L);
    System.out.println("Got Back " + followers.size() + " Followers: " + followers);

  }

  public void testFollowing() throws IOException {
    System.out.println("testFollowing");

    List<User> following = service.following(12665L);
    System.out.println("Got Back " + following.size() + " Following: " + following);

  }

  public void testGetObjektsForThing() throws IOException {
    // https://getamen.com/objekts?q=a&kind_id=2
    System.out.println("testGetObjektsForThing");

    final String amadeus = "amadeus";
    List<Objekt> result = service.objektsForQuery(amadeus, AmenService.OBJEKT_KIND_THING, null, null);

    assertNotNull(result);
    for (Objekt o : result) {
      assertTrue("", o.getName().toLowerCase().contains(amadeus.toLowerCase()));
    }
    boolean foundMozart = false;
    for (Objekt o : result) {
      if ("Wolfgang Amadeus Mozart".equals(o.getName())) {

        foundMozart = true;
        break;
      }
    }

    assertTrue("Wolfgang Amadeus Mozart not found", foundMozart);

  }

  public void testGetObjektsForPlace() throws IOException {
    // https://getamen.com/objekts.json?kind_id=1&lat=52.5172056&lng=13.4667432
    System.out.println("testGetObjektsForPlace");
    List<Objekt> result = service.objektsForQuery("a", AmenService.OBJEKT_KIND_PLACE, 52.5172056, 13.4667432);

    assertNotNull(result);

  }

  public void testGetObjektsForPerson() throws IOException {
    // https://getamen.com/objekts?q=a&kind_id=0
    System.out.println("testGetObjektsForPerson");
    List<Objekt> result = service.objektsForQuery("a", AmenService.OBJEKT_KIND_PERSON, null, null);

    assertNotNull(result);

  }
}