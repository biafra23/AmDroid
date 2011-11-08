package com.jaeckel.amenoid.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import com.jaeckel.amenoid.AmenListActivity;
import com.jayway.android.robotium.solo.Solo;

public class AmenListActivityTest extends ActivityInstrumentationTestCase2<AmenListActivity> {

  Solo solo;

  public AmenListActivityTest() {
    super("com.jaeckel.amenoid", AmenListActivity.class);
  }

  public void testActivity() {
    AmenListActivity activity = getActivity();
    assertNotNull(activity);
  }

  public void setUp() throws Exception {
    solo = new Solo(getInstrumentation(), getActivity());
  }

  @Smoke
    public void testEnterCredentials() throws Exception {
    solo.clickOnButton("Ok");

    }

//  @Smoke
//  public void testAddNote() throws Exception {
//    solo.clickOnMenuItem("Add note");
//    //Assert that NoteEditor activity is opened
//    solo.assertCurrentActivity("Expected NoteEditor activity", "NoteEditor");
//    //In text field 0, add Note 1
//    solo.enterText(0, "Note 1");
//    solo.goBack();
//    //Clicks on menu item
//    solo.clickOnMenuItem("Add note");
//    //In text field 0, add Note 2
//    solo.enterText(0, "Note 2");
//    //Go back to first activity named "NotesList"
//    solo.goBackToActivity("NotesList");
//    boolean expected = true;
//    boolean actual = solo.searchText("Note 1") && solo.searchText("Note 2");
//    //Assert that Note 1 & Note 2 are found
//    assertEquals("Note 1 and/or Note 2 are not found", expected, actual);
//
//  }
//
//  @Smoke
//  public void testEditNote() throws Exception {
//    // Click on the second list line
//    solo.clickInList(2);
//    // Change orientation of activity
//    solo.setActivityOrientation(Solo.LANDSCAPE);
//    // Change title
//    solo.clickOnMenuItem("Edit title");
//    //In first text field (0), add test
//    solo.enterText(0, " test");
//    solo.goBackToActivity("NotesList");
//    boolean expected = true;
//    // (Regexp) case insensitive
//    boolean actual = solo.searchText("(?i).*?note 1 test");
//    //Assert that Note 1 test is found
//    assertEquals("Note 1 test is not found", expected, actual);
//
//  }
//
//
//  @Smoke
//  public void testRemoveNote() throws Exception {
//    //(Regexp) case insensitive/text that contains "test"
//    solo.clickOnText("(?i).*?test.*");
//    //Delete Note 1 test
//    solo.clickOnMenuItem("Delete");
//    //Note 1 test & Note 2 should not be found
//    boolean expected = false;
//    boolean actual = solo.searchText("Note 1 test");
//    //Assert that Note 1 test is not found
//    assertEquals("Note 1 Test is found", expected, actual);
//    solo.clickLongOnText("Note 2");
//    //Clicks on Delete in the context menu
//    solo.clickOnText("(?i).*?Delete.*");
//    actual = solo.searchText("Note 2");
//    //Assert that Note 2 is not found
//    assertEquals("Note 2 is found", expected, actual);
//  }

  @Override
  public void tearDown() throws Exception {
    try {
      //Robotium will finish all the activities that have been opened
      solo.finalize();
    } catch (Throwable e) {
      e.printStackTrace();
    }
    getActivity().finish();
    super.tearDown();
  }
}

