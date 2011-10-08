package com.jaeckel.amdroid.test;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.util.Log;
import com.jaeckel.amdroid.api.model.Amen;
import com.jaeckel.amdroid.api.model.Objekt;
import com.jaeckel.amdroid.api.model.Statement;
import com.jaeckel.amdroid.api.model.User;

import java.util.Date;

/**
 * User: biafra
 * Date: 10/8/11
 * Time: 1:38 PM
 */

public class ParcelITest extends AndroidTestCase {

  private static final String TAG = "ParcelITest";

  public void testObjektTakesRoundTripThroughParcel() throws Exception {

    Log.d(TAG, "testObjektTakesRoundTripThroughParcel");
    Objekt objekt = new Objekt();
    objekt.setName("Test Name");
    objekt.setCategory("Test Category");

    Parcel parcel = Parcel.obtain();
    objekt.writeToParcel(parcel, 0);
    //done writing, now reset parcel for reading
    parcel.setDataPosition(0);
    //finish round trip
    Objekt createFromParcel = Objekt.CREATOR.createFromParcel(parcel);

    assertEquals(createFromParcel.getName(), objekt.getName());
    assertEquals(createFromParcel.getCategory(), objekt.getCategory());

  }



  public void testStatementsTakesRoundTripThroughParcel() throws Exception {

    Log.d(TAG, "testStatementsTakesRoundTripThroughParcel");
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

    assertEquals(statement.getId(), createFromParcel.getId());
    assertEquals(statement.getTotalAmenCount(), createFromParcel.getTotalAmenCount());
    assertEquals(statement.isAgreeable(), createFromParcel.isAgreeable());

    Log.d(TAG, "createFromParcel.getFirstPostedAt(): " + createFromParcel.getFirstPostedAt());
    Log.d(TAG, "       statement.getFirstPostedAt(): " + statement.getFirstPostedAt());

    assertEquals("FirstPostedAt is not the same", statement.getFirstPostedAt(), createFromParcel.getFirstPostedAt());


  }

  public void testAmenTakesRoundTripThroughParcel() throws Exception {

    /*
      private Long      id;
  private Long      userId;
  private User      user;
  private Date      createdAt;
  private Integer   kindId; //normal, amen, dispute
  private Statement statement;
  //sometimes disputed Amen
  private Amen referringAmen;

     */

    Log.d(TAG, "testStatementsTakesRoundTripThroughParcel");
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

    assertEquals(amen.getId(), createFromParcel.getId());

    Log.d(TAG, "createFromParcel.getCreatedAt(): " + createFromParcel.getCreatedAt());
    Log.d(TAG, "            amen.getCreatedAt(): " + amen.getCreatedAt());
    assertEquals("CreatedAt is not the same", amen.getCreatedAt(), createFromParcel.getCreatedAt());

    assertEquals(amen.getUserId(), createFromParcel.getUserId());

  }
}
