package com.jaeckel.amenoid;
import com.jaeckel.Amenoid.R;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.fragments.ExploreFragment;
import com.jaeckel.amenoid.util.Log;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

/**
 * @author biafra
 * @date 6/15/12 10:06 PM
 */
public class ExploreFragmentActivity extends SherlockFragmentActivity {

  private static String TAG = AmenListFragmentActivity.class.getSimpleName();

  private ExploreFragment exploreFragment;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_fragment_explore);

    FragmentManager fragmentManager = getSupportFragmentManager();
    //exploreFragment = (ExploreFragment) fragmentManager.findFragmentById(R.id.);

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

}