package com.jaeckel.amenoid.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.jaeckel.amenoid.AmenListActivity;
import com.jayway.android.robotium.solo.Solo;

import java.util.ArrayList;
import java.util.List;

public class AmenListActivityTest extends ActivityInstrumentationTestCase2<AmenListActivity> {

  Solo solo;
  private static final String TAG = "AmenListActivityTest";

  public AmenListActivityTest() {
    super("com.jaeckel.amenoid", AmenListActivity.class);
  }

  public void testActivity() {
    Toast.makeText(getActivity(), "testActivity", Toast.LENGTH_SHORT).show();
    AmenListActivity activity = getActivity();
    assertNotNull(activity);
  }

  public void setUp() throws Exception {
    solo = new Solo(getInstrumentation(), getActivity());
  }

//  @Smoke
//  public void testEnterCredentials() throws Exception {
//    Toast.makeText(getActivity(), "testEnterCredentials", Toast.LENGTH_SHORT).show();
//
//    solo.clickOnButton("Ok");
//
//    ArrayList<TextView> views = solo.clickInList(0);
//    for (TextView textView : views) {
//      Log.d(TAG, "textView: " + textView);
//      Log.d(TAG, "textView: " + textView.getText());
//    }
////    solo.enterText();
////    solo.enterText("d@eu.ro");
//    solo.clickOnButton(0);
//
//    solo.clickInList(1);
//    solo.enterText(0, "foobar23");
//    solo.clickOnButton(0);
//
//  }

//  @Smoke
//  public void testMenu() throws Exception {
//    Toast.makeText(getActivity(), "testMenu", Toast.LENGTH_SHORT).show();
//
//    solo.clickOnButton("Ok");
//
//    ArrayList<TextView> views = solo.clickInList(0);
//    for (TextView textView : views) {
//      Log.d(TAG, "textView: " + textView);
//      Log.d(TAG, "textView: " + textView.getText());
//    }
////    solo.enterText();
////    solo.enterText("d@eu.ro");
//    solo.clickOnButton(0);
//
//    solo.clickInList(1);
//    solo.enterText(0, "foobar23");
//    solo.clickOnButton(0);
//
//  }

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
  @Smoke
  public void testShowDetails() throws Exception {

    ArrayList<View> views = solo.getViews();
    Log.d(TAG, "views.size(): " + views.size());

    for (View v : views) {
      Log.d(TAG, "v: " + v);
    }
    while (hasVisibleProgressView(solo.getViews())) {
      solo.sleep(1000);
      Log.d(TAG, "ProgressBar still visible");
    }
    if (hasVisibleListView(solo.getViews())) {
      Log.d(TAG, "HAS VISIBLE LISTVIEWs");
    }

    List<ListView> listViews = solo.getCurrentListViews();

    assertTrue(listViews.size() == 1);

    for (int i = 1; i < 20; i++) {

      List<TextView> texts = solo.clickInList(i);

      for (TextView t : texts) {
        Log.d(TAG, "TextView: " + t);
      }
      solo.sleep(1000);
      solo.goBack();
    }
  }

  private boolean hasVisibleListView(ArrayList<View> views) {
    for (View v : views) {
//        Log.d(TAG, "v: " + v);
      if (v.getClass() == ListView.class && v.getVisibility() == View.VISIBLE) {
        return true;
      }
    }
    return false;
  }

  private boolean hasVisibleProgressView(ArrayList<View> views) {
    for (View v : views) {
//      Log.d(TAG, "v: " + v);
      if (v.getClass() == ProgressBar.class && v.getVisibility() == View.VISIBLE) {
        return true;
      }
    }
    return false;
  }
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

