package com.jaeckel.amdroid.test;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.util.Log;
import com.jaeckel.amdroid.api.model.Amen;
import com.jaeckel.amdroid.api.model.Objekt;
import com.jaeckel.amdroid.api.model.Statement;
import com.jaeckel.amdroid.api.model.Topic;
import com.jaeckel.amdroid.api.model.User;

import java.util.Date;

/**
 * User: biafra
 * Date: 10/8/11
 * Time: 1:38 PM
 */

public class ParcelITest extends AndroidTestCase {

  private static final String TAG = "ParcelITest";

  public void testObjekt() throws Exception {

    Log.d(TAG, "testObjekt");
    Objekt objekt = new Objekt();
    objekt.setName("Test Name");
    objekt.setCategory("Test Category");

    Parcel parcel = Parcel.obtain();
    objekt.writeToParcel(parcel, 0);
    //done writing, now reset parcel for reading
    parcel.setDataPosition(0);
    //finish round trip
    Objekt createFromParcel = Objekt.CREATOR.createFromParcel(parcel);

    assertEquals(createFromParcel, objekt);
  }

  public void testObjektEmpty() throws Exception {

    Log.d(TAG, "testObjektEmpty");
    Objekt objekt = new Objekt();

    Parcel parcel = Parcel.obtain();
    objekt.writeToParcel(parcel, 0);
    //done writing, now reset parcel for reading
    parcel.setDataPosition(0);
    //finish round trip
    Objekt createFromParcel = Objekt.CREATOR.createFromParcel(parcel);

    assertEquals(createFromParcel, objekt);


  }

  public void testTopic() throws Exception {

    Log.d(TAG, "testTopic");
    Topic topic = new Topic();
    topic.setId(7L);
    topic.setBest(true);
    topic.setDescription("Test-Beschreibung");
    topic.setObjektsCount(1001);
    topic.setScope("Ever");

    Parcel parcel = Parcel.obtain();
    topic.writeToParcel(parcel, 0);
    //done writing, now reset parcel for reading
    parcel.setDataPosition(0);
    //finish round trip
    Topic createFromParcel = Topic.CREATOR.createFromParcel(parcel);

    assertEquals(createFromParcel, topic);

    assertEquals(createFromParcel.getId(), topic.getId());
    assertEquals(createFromParcel.isBest(), topic.isBest());
    assertEquals(createFromParcel.getDescription(), topic.getDescription());
    assertEquals(createFromParcel.getScope(), topic.getScope());
    assertEquals(createFromParcel.getObjektsCount(), topic.getObjektsCount());
    assertEquals(createFromParcel.getRankedStatements(), topic.getRankedStatements());

  }

  public void testTopicEmpty() throws Exception {

    Log.d(TAG, "testTopicEmpty");
    Topic topic = new Topic();

    Parcel parcel = Parcel.obtain();
    topic.writeToParcel(parcel, 0);
    //done writing, now reset parcel for reading
    parcel.setDataPosition(0);
    //finish round trip
    Topic createFromParcel = Topic.CREATOR.createFromParcel(parcel);

    assertEquals(createFromParcel, topic);

  }


  public void testStatement() throws Exception {

    Log.d(TAG, "testStatement");
    Statement statement = new Statement();
    statement.setId(7331L);
    statement.setAgreeable(true);
    statement.setTotalAmenCount(1001L);
    statement.setFirstPostedAt(new Date());
    statement.setObjekt(new Objekt("Foo", Objekt.THING));
    statement.setTopic(new Topic("Beschreibung", true, "Eva"));

    Parcel parcel = Parcel.obtain();
    statement.writeToParcel(parcel, 0);
    //done writing, now reset parcel for reading
    parcel.setDataPosition(0);
    //finish round trip
    Statement createFromParcel = Statement.CREATOR.createFromParcel(parcel);

    assertEquals(statement, createFromParcel);

  }

  public void testStatement2() throws Exception {

    Log.d(TAG, "testStatement");
    Statement statement = new Statement();
    statement.setId(7331L);
    statement.setAgreeable(true);
    statement.setTotalAmenCount(1001L);
    statement.setFirstPostedAt(new Date());

    Parcel parcel = Parcel.obtain();
    statement.writeToParcel(parcel, 0);
    //done writing, now reset parcel for reading
    parcel.setDataPosition(0);
    //finish round trip
    Statement createFromParcel = Statement.CREATOR.createFromParcel(parcel);

    assertEquals(statement, createFromParcel);

  }

  public void testStatementEmpty() throws Exception {

    Log.d(TAG, "testStatementEmpty");
    Statement statement = new Statement();

    Parcel parcel = Parcel.obtain();
    statement.writeToParcel(parcel, 0);
    //done writing, now reset parcel for reading
    parcel.setDataPosition(0);
    //finish round trip
    Statement createFromParcel = Statement.CREATOR.createFromParcel(parcel);

    assertEquals(statement.getId(), createFromParcel.getId());
    assertEquals(statement.getTotalAmenCount(), createFromParcel.getTotalAmenCount());
    assertEquals(statement.isAgreeable(), createFromParcel.isAgreeable());

    Log.d(TAG, "createFromParcel.getFirstPostedAt(): " + createFromParcel.getFirstPostedAt());
    Log.d(TAG, "       statement.getFirstPostedAt(): " + statement.getFirstPostedAt());
    assertEquals("FirstPostedAt is not the same", statement.getFirstPostedAt(), createFromParcel.getFirstPostedAt());

    assertEquals("Objekt is not the same", statement.getObjekt(), createFromParcel.getObjekt());
    assertEquals("Topic is not the same", statement.getTopic(), createFromParcel.getTopic());


  }

  public void testAmen() throws Exception {

    Log.d(TAG, "testAmen");
    Amen amen = new Amen();
    amen.setId(7331L);
    amen.setUser(new User(100L));
    amen.setUserId(100L);
    amen.setCreatedAt(new Date());

    Statement statement = new Statement();
    statement.setId(7331L);
    statement.setAgreeable(true);
    statement.setTotalAmenCount(1001L);
    statement.setFirstPostedAt(new Date());
    amen.setStatement(statement);

    // ----------------- parcel here -------------------
    Parcel parcel = Parcel.obtain();
    amen.writeToParcel(parcel, 0);
    //done writing, now reset parcel for reading
    parcel.setDataPosition(0);
    //finish round trip
    Amen createFromParcel = Amen.CREATOR.createFromParcel(parcel);

    assertEquals(amen, createFromParcel);

  }

  public void testAmenEmpty() throws Exception {

    Log.d(TAG, "testAmenEmpty");
    Amen amen = new Amen();

    // ----------------- parcel here -------------------
    Parcel parcel = Parcel.obtain();
    amen.writeToParcel(parcel, 0);
    //done writing, now reset parcel for reading
    parcel.setDataPosition(0);
    //finish round trip
    Amen createFromParcel = Amen.CREATOR.createFromParcel(parcel);

    assertEquals(amen, createFromParcel);

  }

  public void testUser() throws Exception {

    Log.d(TAG, "testUser");
    User user = new User();
    user.setId(7331L);
    user.setCreatedAt(new Date());
    user.setFollowersCount(0);
    user.setFollowing(true);
    user.setPicture("fooo");
    user.setReceivedAmenCount(100);
    user.setCreatedStatementsCount(101);

    // ----------------- parcel here -------------------
    Parcel parcel = Parcel.obtain();
    user.writeToParcel(parcel, 0);
    //done writing, now reset parcel for reading
    parcel.setDataPosition(0);
    //finish round trip
    User createFromParcel = User.CREATOR.createFromParcel(parcel);

    assertEquals(user, createFromParcel);

  }

  public void testUserEmpty() throws Exception {

    Log.d(TAG, "testUserEmpty");
    User user = new User();

    // ----------------- parcel here -------------------
    Parcel parcel = Parcel.obtain();
    user.writeToParcel(parcel, 0);
    //done writing, now reset parcel for reading
    parcel.setDataPosition(0);
    //finish round trip
    User createFromParcel = User.CREATOR.createFromParcel(parcel);

    assertEquals(user, createFromParcel);

  }
}
