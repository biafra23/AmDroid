package com.jaeckel.amenoid;

import java.util.List;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.github.ignition.location.annotations.IgnitedLocation;
import com.github.ignition.location.annotations.IgnitedLocationActivity;
import com.github.ignition.location.templates.OnIgnitedLocationChangedListener;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.fragments.AmenListFragment;
import com.jaeckel.amenoid.statement.ChooseStatementTypeActivity;
import com.jaeckel.amenoid.util.Log;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * @author biafra
 * @date 5/22/12 7:08 PM
 */
@IgnitedLocationActivity()
public class AmenListFragmentActivity extends SherlockFragmentActivity implements OnIgnitedLocationChangedListener  {

  private static String TAG      = AmenListFragmentActivity.class.getSimpleName();
  private        int    feedType = AmenService.FEED_TYPE_FOLLOWING;

  private AmenListFragment amenListFragment;

  @IgnitedLocation
  private Location lastLocation;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    setContentView(R.layout.activity_fragment_amen_list);

    FragmentManager fragmentManager = getSupportFragmentManager();
    amenListFragment = (AmenListFragment) fragmentManager.findFragmentById(R.id.activity_fragment_amen_list);

    // default feedType is following
    feedType = getIntent().getIntExtra(Constants.EXTRA_FEED_TYPE, AmenService.FEED_TYPE_FOLLOWING);

    if (!AmenoidApp.getInstance().isSignedIn() && feedType == AmenService.FEED_TYPE_FOLLOWING) {

      // if not signed in default to popular. new is gone
      feedType = AmenService.FEED_TYPE_POPULAR;
    }
    String title = "";
    if (feedType == AmenService.FEED_TYPE_FOLLOWING) {
      title = "Following";
    } else if (feedType == AmenService.FEED_TYPE_RECENT) {
      title = "New";
    } else if (feedType == AmenService.FEED_TYPE_POPULAR) {
      title = "Popular";
    }

    ActionBar bar = getSupportActionBar();
    bar.setSubtitle(title);
    bar.setTitle("Timeline");

    Log.d(TAG, "LastLocation: " + lastLocation);
    AmenoidApp.getInstance().setLastLocation(lastLocation);

  }


  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    Log.d(TAG, "onCreateOptionsMenu");

    MenuInflater inflater = this.getSupportMenuInflater();
    inflater.inflate(R.menu.menu_main, menu);

    return true;
  }

  public boolean onPrepareOptionsMenu(Menu menu) {
    Log.d(TAG, "onPrepareOptionsMenu");
    MenuItem following = menu.findItem(R.id.following_menu);
    MenuItem popular = menu.findItem(R.id.popular_menu);
    MenuItem explore = menu.findItem(R.id.explore_menu);
    MenuItem amenSth = menu.findItem(R.id.amen);
    MenuItem signInOut = menu.findItem(R.id.signin);
    //    MenuItem search = menu.findItem(R.id.search_menu_item);

    if (feedType == AmenService.FEED_TYPE_FOLLOWING) {
      //      recent.setVisible(true);
      following.setVisible(false);
      popular.setVisible(true);

    } else if (feedType == AmenService.FEED_TYPE_RECENT) {
      //      recent.setVisible(false);
      following.setVisible(true);
      popular.setVisible(true);

    } else if (feedType == AmenService.FEED_TYPE_POPULAR) {
      //      recent.setVisible(true);
      following.setVisible(true);
      popular.setVisible(false);

    }
    if (!AmenoidApp.getInstance().isSignedIn()) {

      amenSth.setEnabled(false);
      following.setEnabled(false);
      //      search.setEnabled(false);
      signInOut.setTitle("Sign in");

    } else {
      amenSth.setEnabled(true);
      following.setEnabled(true);
      //      search.setEnabled(true);
      signInOut.setTitle("Sign out");
    }

    return true;
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);

    Log.d(TAG, "onOptionsItemSelected()");

    switch (item.getItemId()) {

      case R.id.signin: {
        startActivity(new Intent(this, SettingsActivity.class));

        return true;
      }
      case R.id.refresh: {
        amenListFragment.refresh();
        return true;
      }
      case R.id.following_menu: {
        Intent intent = new Intent(this, AmenListFragmentActivity.class);
        intent.putExtra(Constants.EXTRA_FEED_TYPE, AmenService.FEED_TYPE_FOLLOWING);
        startActivity(intent);
        return true;
      }

      case R.id.popular_menu: {
        Intent intent = new Intent(this, AmenListFragmentActivity.class);
        intent.putExtra(Constants.EXTRA_FEED_TYPE, AmenService.FEED_TYPE_POPULAR);
        startActivity(intent);

        return true;
      }
      case R.id.explore_menu: {
        Log.d(TAG, "Starting ExploreFragmentActivity ");
        Intent intent = new Intent(this, ExploreFragmentActivity.class);
        startActivity(intent);

        return true;
      }
      case R.id.amen: {
        //        Toast.makeText(this, "Refreshing Amens", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ChooseStatementTypeActivity.class);
        startActivity(intent);
        return true;
      }
      case R.id.about_menu_item: {
        if (AmenoidApp.DEVELOPER_MODE) {
          Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
        return true;
      }
      case R.id.search_menu_item: {
        Log.d(TAG, "R.id.search");

        onSearchRequested();
        return true;
      }
    }

    return false;
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public boolean onIgnitedLocationChanged(Location newLocation) {

    lastLocation = newLocation;

    AmenoidApp.getInstance().setLastLocation(lastLocation);

    // request more location update requests by returning true
    return true;
  }
}