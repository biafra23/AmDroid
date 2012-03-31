package com.jaeckel.amenoid.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.http.impl.client.DefaultHttpClient;

import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.api.model.Objekt;
import com.jaeckel.amenoid.api.model.Statement;
import com.jaeckel.amenoid.api.model.Topic;
import com.jaeckel.amenoid.api.model.User;

import junit.framework.TestCase;

/**
 * User: biafra
 * Date: 9/23/11
 * Time: 9:06 PM
 */
public class AmenServiceITest extends TestCase {

  private String            username;
  private String            password;
  private AmenService       service;
  private DefaultHttpClient amenHttpClient;

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

    InputStream in = ClassLoader.getSystemResourceAsStream("amenkeystore");
    amenHttpClient = new AmenHttpClient(in, "mysecret", "JKS");

    service = new AmenServiceImpl(amenHttpClient);
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
    System.out.println(" : " + amens.get(0));
    assertEquals("Amen with wrong id", 182043, (long) amens.get(0).getId());

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

    User ui = service.getUserForId(14028L);

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

    Topic topic = service.getTopicsForId(29020 + "", null);

    assertEquals("Wrong topics", (Long) 29020L, topic.getId());
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

    final String amadeus = "wolfgang amadeus";
    List<Objekt> result = service.objektsForQuery(amadeus, AmenService.OBJEKT_KIND_THING, null, null);

    assertNotNull(result);
    for (Objekt o : result) {
      assertTrue(o.toString().toLowerCase() + ".contains(" + amadeus + ")", o.toString().toLowerCase().contains(amadeus.toLowerCase()));
    }
    boolean foundMozart = false;
    for (Objekt o : result) {
      if ("Wolfgang Amadeus Mozart".equals(o.getName())) {

        foundMozart = true;
        break;
      } else {
        System.out.println("o.getName(): " + o.getName());
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

  public void testGetAmenForObjekt() throws IOException {
    System.out.println("testGetAmenForObjekt");
    List<Amen> result = service.getAmenForObjekt(97282L);

    assertNotNull(result);

    for (Amen a : result) {
      System.out.println("a: " + a);
    }
  }

  public void testSearch() throws IOException {
    System.out.println("testGetAmenForObjekt");
    List<Amen> result = service.search("Drachenspielplatz");

    assertNotNull(result);

    for (Amen a : result) {
      System.out.println("a: " + a);
      assertTrue(a.toString().toLowerCase().contains("drachenspielplatz"));

    }
  }

  public void testSearch2() throws IOException {
    System.out.println("testGetAmenForObjekt");
    List<Amen> result = service.search("worst customer service");

    assertNotNull(result);

    for (Amen a : result) {
      System.out.println("a: " + a);
//        assertTrue(a.toString().toLowerCase().contains("\"best\":false"));
      assertTrue(a.toString().toLowerCase().contains("customer"));
      assertTrue(a.toString().toLowerCase().contains("service"));

    }
  }

  public void testGetAmenForUser() throws IOException {
    System.out.println("testGetAmenForUser");
    List<Amen> result = service.getAmenForUser("dirkjaeckel", 0L);

    assertNotNull(result);

    for (Amen a : result) {
      System.out.println("a: " + a);
      //        assertTrue(a.toString().toLowerCase().contains("\"best\":false"));
//          assertTrue(a.toString().toLowerCase().contains("customer"));
//          assertTrue(a.toString().toLowerCase().contains("service"));

    }
  }

  public void testGetAmenWithMediaItem() throws IOException {
//https://getamen.com/amen.json?last_amen_id=990399&limit=25&auth_token=GyG8p74rmAqo3ufU6bZq

    System.out.println("testGetAmenForUser");
    List<Amen> amens = service.getFeed(AmenService.FEED_TYPE_FOLLOWING);
        assertEquals("Amen amount wrong", 25, amens.size());

        amens = service.getFeed(990399L, 25, AmenService.FEED_TYPE_FOLLOWING);

        assertNotNull(amens);

        for (Amen a : amens) {
          System.out.println("a: " + a);

        }
  }
}
