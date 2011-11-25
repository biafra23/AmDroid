package com.jaeckel.amenoid.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.jaeckel.amenoid.AboutActivity;
import com.jaeckel.amenoid.AmenListActivity;
import com.jaeckel.amenoid.SearchActivity;
import com.jaeckel.amenoid.statement.ChooseStatementTypeActivity;
import com.jayway.android.robotium.solo.Solo;

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

  @Smoke
  public void testMenusWhileSignedIn() throws Exception {
    while (hasVisibleProgressView()) {
      solo.sleep(1000);
      Log.d(TAG, "ProgressBar still visible");
    }

  }

  @Smoke
  public void testMenusWhileSignedInRefresh() throws Exception {
    while (hasVisibleProgressView()) {
      solo.sleep(1000);
      Log.d(TAG, "ProgressBar still visible");
    }
    solo.clickOnMenuItem("Refresh");
    solo.sleep(100);
    assertTrue(hasVisibleProgressView());
    solo.sleep(1000);
    Log.d(TAG, "ProgressBar still visible");
  }


  @Smoke
  public void testMenusWhileSignedInPopular() throws Exception {
    Log.d(TAG, "testMenusWhileSignedInPopular start... ");

    while (hasVisibleProgressView()) {
      solo.sleep(1000);
      Log.d(TAG, "ProgressBar still visible");
    }

    solo.clickOnMenuItem("Popular");
    solo.sleep(100);
    assertTrue(hasVisibleProgressView());

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
    solo.sleep(100);
    assertTrue(hasVisibleProgressView());

    while (hasVisibleProgressView()) {
      solo.sleep(1000);
      Log.d(TAG, "ProgressBar still visible");
    }

    solo.goBack();
    Log.d(TAG, "testMenusWhileSignedInNew done. ");

  }

  @Smoke
  public void testMenusWhileSignedInAmen() throws Exception {
    Log.d(TAG, "testMenusWhileSignedInAmen start... ");

    while (hasVisibleProgressView()) {
      solo.sleep(1000);
      Log.d(TAG, "ProgressBar still visible");
    }
    solo.clickOnMenuItem("Amen sth.");
    solo.sleep(100);
    Log.d(TAG, "Waiting for Activity: " + ChooseStatementTypeActivity.class.getName());
    solo.waitForActivity(SearchActivity.class.getName(), 10);

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
    Log.d(TAG, "Waiting for Activity: " + SearchActivity.class.getName());
    solo.waitForActivity(SearchActivity.class.getName(), 10);

    Activity a = solo.getCurrentActivity();
    Log.d(TAG, "Current Activity: " + a.getClass().getName());
    assertEquals(a.getClass().getName(), "com.jaeckel.amenoid.SearchActivity");
    solo.goBack();
    Log.d(TAG, "testMenusWhileSignedInSearch done. ");

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
//    List<Activity> activities = solo.getAllOpenedActivities();
//    for (Activity a : activities) {
//      Log.d(TAG, "Activities: " + a.getClass());
//    }
//  @Smoke
//  public void testShowDetails() throws Exception {
//
//    while (hasVisibleProgressView()) {
//      solo.sleep(1000);
//      Log.d(TAG, "ProgressBar still visible");
//    }
//    if (hasVisibleListView()) {
//      Log.d(TAG, "HAS VISIBLE LISTVIEWs");
//    }
//
//    List<ListView> listViews = solo.getCurrentListViews();
//
//    assertTrue(listViews.size() == 1);
//    ListView listView = listViews.get(0);
//
//    for (int i = 0; i < listView.getChildCount(); i++) {
//      View subView = listView.getChildAt(i);
//
//      Log.d(TAG, i + ": subView:" + subView.getClass());
//    }
//
//
//    for (int i = 1; i < 20; i++) {
//
//      List<TextView> texts = solo.clickInList(i);
//
//      for (TextView t : texts) {
//        Log.d(TAG, "TextView: " + t.getText());
//
//      }
//      solo.sleep(1000);
//      solo.goBack();
//    }

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

