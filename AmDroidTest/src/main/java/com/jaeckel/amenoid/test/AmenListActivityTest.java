package com.jaeckel.amenoid.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.jaeckel.amenoid.AboutActivity;
import com.jaeckel.amenoid.AmenDetailFragmentActivity;
import com.jaeckel.amenoid.AmenListFragmentActivity;
import com.jaeckel.amenoid.statement.ChooseStatementTypeActivity;
import com.jayway.android.robotium.solo.Solo;

import java.util.List;

public class AmenListFragmentActivityTest extends ActivityInstrumentationTestCase2<AmenListFragmentActivity> {

  Solo solo;
  private static final String TAG = AmenListFragmentActivityTest.class.getSimpleName();

  public AmenListFragmentActivityTest() {
    super("com.jaeckel.amenoid", AmenListFragmentActivity.class);
  }

  public void testActivity() {
    Toast.makeText(getActivity(), "testActivity", Toast.LENGTH_SHORT).show();
    AmenListFragmentActivity activity = getActivity();
    assertNotNull(activity);
  }

  public void setUp() throws Exception {
    solo = new Solo(getInstrumentation(), getActivity());
  }

  @Smoke
  public void testAAAA() throws Exception {
    Log.d(TAG, "------------------------------------------------------------ starting tests...");

  }

  @Smoke
  public void testMenusWhileSignedIn() throws Exception {
    while (hasVisibleProgressView()) {
      solo.sleep(1000);
      Log.d(TAG, "ProgressBar still visible");
    }

  }

  @Smoke
  public void testMenusWhileSignedInRefresh() throws Exception {
    Log.d(TAG, "testMenusWhileSignedInRefresh start... ");

    while (hasVisibleProgressView()) {
      solo.sleep(1000);
      Log.d(TAG, "ProgressBar still visible");
    }
    solo.clickOnMenuItem("Refresh");

    // how to check if it refreshed here?

    Log.d(TAG, "testMenusWhileSignedInRefresh done. ");
  }


  @Smoke
  public void testMenusWhileSignedInPopular() throws Exception {
    Log.d(TAG, "testMenusWhileSignedInPopular start... ");

    while (hasVisibleProgressView()) {
      solo.sleep(1000);
      Log.d(TAG, "ProgressBar still visible");
    }

    solo.clickOnMenuItem("Popular");

    Log.d(TAG, "Waiting for Activity: " + AmenListFragmentActivity.class.getName());
    solo.waitForActivity(AmenListFragmentActivity.class.getName(), 10);

    Activity a = solo.getCurrentActivity();
    Log.d(TAG, "Current Activity: " + a.getClass().getName());
    assertEquals(a.getClass().getName(), "com.jaeckel.amenoid.AmenListFragmentActivity");

    CharSequence title = a.getTitle();

    assertEquals("Wrong title", title, "Amenoid/Timeline: Popular");

    while (hasVisibleProgressView()) {
      solo.sleep(1000);
      Log.d(TAG, "ProgressBar still visible");
    }

    solo.goBack();
    Log.d(TAG, "testMenusWhileSignedInPopular done. ");

  }

  @Smoke
  public void testMenusWhileSignedInNew() throws Exception {
    Log.d(TAG, "testMenusWhileSignedInNew start... ");

    while (hasVisibleProgressView()) {
      solo.sleep(1000);
      Log.d(TAG, "ProgressBar still visible");
    }
    solo.clickOnMenuItem("New");

    Activity a = solo.getCurrentActivity();
    Log.d(TAG, "Current Activity: " + a.getClass().getName());
    assertEquals(a.getClass().getName(), "com.jaeckel.amenoid.AmenListFragmentActivity");

    CharSequence title = a.getTitle();

    assertEquals("Wrong title", title, "Amenoid/Timeline: New");

    while (hasVisibleProgressView()) {
      solo.sleep(1000);
      Log.d(TAG, "ProgressBar still visible");
    }

    solo.goBack();
    Log.d(TAG, "testMenusWhileSignedInNew done. ");

  }

  @Smoke
  public void testMenusWhileSignedInAmenSomething() throws Exception {
    Log.d(TAG, "testMenusWhileSignedInAmen start... ");

    while (hasVisibleProgressView()) {
      solo.sleep(1000);
      Log.d(TAG, "ProgressBar still visible");
    }
    solo.clickOnMenuItem("Amen sth.");
    solo.sleep(100);
    Log.d(TAG, "Waiting for Activity: " + ChooseStatementTypeActivity.class.getName());
    solo.waitForActivity(ChooseStatementTypeActivity.class.getName(), 10);

    Activity a = solo.getCurrentActivity();
    Log.d(TAG, "Current Activity: " + a.getClass().getName());
    assertEquals(a.getClass().getName(), "com.jaeckel.amenoid.statement.ChooseStatementTypeActivity");
    solo.goBack();
    Log.d(TAG, "testMenusWhileSignedInAmen done. ");

  }


  @Smoke
  public void testMenusWhileSignedInSearch() throws Exception {
    Log.d(TAG, "testMenusWhileSignedInSearch start... ");

    while (hasVisibleProgressView()) {
      solo.sleep(1000);
      Log.d(TAG, "ProgressBar still visible");
    }
    solo.clickOnMenuItem("Search", true);
    solo.sleep(100);

    List<Activity> activities = solo.getAllOpenedActivities();

    for (Activity a : activities) {
      Log.d(TAG, "a.title: " + a.getTitle());
      Log.d(TAG, "a: " + a);
    }


    Log.d(TAG, "testMenusWhileSignedInSearch nothing done. ");

  }

  @Smoke
  public void testMenusWhileSignedInAbout() throws Exception {
    Log.d(TAG, "testMenusWhileSignedInAbout start... ");

    while (hasVisibleProgressView()) {
      solo.sleep(1000);
      Log.d(TAG, "ProgressBar still visible");
    }
    solo.clickOnMenuItem("About", true);

    Log.d(TAG, "Waiting for Activity: " + AboutActivity.class.getName());
    solo.waitForActivity(AboutActivity.class.getName(), 10);

    Activity a = solo.getCurrentActivity();
    Log.d(TAG, "Current Activity: " + a.getClass().getName());
    assertEquals(a.getClass().getName(), "com.jaeckel.amenoid.AboutActivity");
    solo.goBack();

    Log.d(TAG, "testMenusWhileSignedInAbout done. ");

  }

  @Smoke
  public void testListItemClicked() throws Exception {
    Log.d(TAG, "testListItemClicked start... ");

    while (hasVisibleProgressView()) {
      solo.sleep(1000);
      Log.d(TAG, "ProgressBar still visible");
    }
    while (!hasVisibleListView()) {
      Log.d(TAG, "No visible ListView, yet");
      solo.sleep(1000);
    }
    Log.d(TAG, "HAS VISIBLE LISTVIEWs");

    for (int i = 0; i< 19 ; i++) {
      checkDetails(i);
    }
    Log.d(TAG, "testListItemClicked done. ");

  }

  private void checkDetails(int position) {

    Log.d(TAG, "----- SNIP -------");
    Log.d(TAG, "Checking details of Amen at position: " + position);

    List<ListView> listViews = solo.getCurrentListViews();

    assertEquals(1, listViews.size());
    ListView listView = listViews.get(0);

    for (int i = 0; i < listView.getChildCount(); i++) {
      View subView = listView.getChildAt(i);

      Log.d(TAG, i + ": subView: id: " + subView.getId());
    }

//    View view = listView.getChildAt();

    List<TextView> texts = solo.clickInList(position);

    CharSequence statement = texts.get(2).getText();
    Log.d(TAG, "Statement: " + statement);


    for (TextView t : texts) {
      Log.d(TAG, "TextView.text: " + t.getText());

    }

    Log.d(TAG, "Waiting for Activity: " + AmenDetailFragmentActivity.class.getName());
    solo.waitForActivity(AmenDetailFragmentActivity.class.getName(), 10);

    Activity a = solo.getCurrentActivity();
    assertEquals( "com.jaeckel.amenoid.AmenDetailActivity", a.getClass().getName());

    while (hasVisibleProgressView()) {
      solo.sleep(1000);
      Log.d(TAG, "ProgressBar still visible");
    }
    while (!hasVisibleListView()) {
      Log.d(TAG, "No visible ListView, yet");
      solo.sleep(1000);
    }
    Log.d(TAG, "HAS VISIBLE LISTVIEWs");

    listViews = solo.getCurrentListViews();

    assertEquals(1, listViews.size());

    listView = listViews.get(0);

    for (int i = 0; i < listView.getChildCount(); i++) {
      View subView = listView.getChildAt(i);

      Log.d(TAG, i + ": subView: id: " + subView.getId());
    }

    texts = solo.clickInList(0); // leads to scorecard

    solo.goBack();

    for (TextView t : texts) {
      Log.d(TAG, "text: " + t.getText());

    }

    CharSequence detailStatement = texts.get(0).getText();

    Log.d(TAG, "DetailStatement: " + detailStatement);
    Log.d(TAG, "      Statement: " + statement);

    assertEquals(statement.toString(), detailStatement.toString());

    solo.sleep(1000);
    solo.goBack();
  }

  @Smoke
  public void testListItemLongClicked() throws Exception {
    Log.d(TAG, "testListItemLongClicked start... ");

    Log.d(TAG, "testListItemLongClicked nothing done. ");

  }

  @Smoke
  public void testPullToRefresh() throws Exception {
    Log.d(TAG, "testPullToRefresh start... ");

    Log.d(TAG, "testPullToRefresh nothing done. ");

  }

  @Smoke
  public void testZZZZ() throws Exception {
    Log.d(TAG, "------------------------------------------------------------ all tests done.");

  }

  @Override
  public void tearDown() throws Exception {
    Log.d(TAG, "tearDown()...");
    try {
      //Robotium will finish all the activities that have been opened
      solo.finalize();
    } catch (Throwable e) {
      e.printStackTrace();
    }
    getActivity().finish();
    super.tearDown();
    Log.d(TAG, "done.");

  }

// helper methods

  private boolean hasVisibleListView() {
    for (View v : solo.getCurrentListViews()) {
//      Log.d(TAG, "v: " + v);
      if (v.getVisibility() == View.VISIBLE) {
        return true;
      }
    }
    return false;
  }

  private boolean hasVisibleProgressView() {
    for (View v : solo.getCurrentProgressBars()) {
//      Log.d(TAG, "v: " + v);
      if (v.getVisibility() == View.VISIBLE) {
        return true;
      }
    }
    return false;
  }

}

