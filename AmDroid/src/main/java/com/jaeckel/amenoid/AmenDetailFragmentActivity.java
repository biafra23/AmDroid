package com.jaeckel.amenoid;


import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.api.model.Statement;
import com.jaeckel.amenoid.fragments.AmenDetailFragment;
import com.jaeckel.amenoid.util.Log;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

/**
 * User: biafra
 * Date: 9/25/11
 * Time: 10:27 AM
 */
public class AmenDetailFragmentActivity extends SherlockFragmentActivity {

  private static final String TAG       = AmenDetailFragmentActivity.class.getSimpleName();
  //  private Amen             currentAmen;
//  private Statement        currentStatement;
//  private Topic            topicWithRankedStatements;
//  private TextView         statementView;
//  private TextView         userView;
//  private TextView         amenCount;
//  private TextView         commentsCount;
//  private Button           amenTakeBackButton;
//  private Button           hellNoButton;
//  private UserListAdapter  adapter;
//  private ThumbnailAdapter thumbs;
//  private AmenService      service;
  private static final int[]  IMAGE_IDS = {R.id.user_image};
  private              String lastError = null;
  //  private SimpleWebImageCache<ThumbnailBus, ThumbnailMessage> cache;
//  private Typeface                                            amenTypeThin;
//  private Typeface                                            amenTypeBold;
//  private TextView                                            commentsTextView;
  private ShareActionProvider mShareActionProvider;
  private AmenDetailFragment  amenDetailFragment;
  private Handler handler = new Handler();


  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    Log.d(TAG, "onConfigurationChanged(): " + newConfig);
//    setContentView(R.layout.myLayout);
  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "--> onCreate");

//    service = AmenoidApp.getInstance().getService();
//    cache = AmenoidApp.getInstance().getCache();
    setContentView(R.layout.activity_fragment_amen_detail);


    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle("Amendetails");

    FragmentManager fragmentManager = getSupportFragmentManager();
    amenDetailFragment = (AmenDetailFragment) fragmentManager.findFragmentById(R.id.activity_fragment_amen_detail);

  }

  public void onPause() {
    super.onPause();
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);
    Log.d(TAG, "--> onPrepareOptionsMenu");

//    MenuInflater inflater = getSupportMenuInflater();
//    inflater.inflate(R.menu.menu_detail, menu);
//
    MenuItem directionsMenu = menu.findItem(R.id.route_there);
    MenuItem showOnMapMenu = menu.findItem(R.id.show_on_map);
    Log.d(TAG, "getCurrentStatement(): " + getCurrentStatement());

    if (getCurrentStatement() != null
        && getCurrentStatement().getObjekt().getLat() != null
        && getCurrentStatement().getObjekt().getLng() != null) {
      Log.d(TAG, "directionsMenu.setVisible(true); showOnMapMenu.setVisible(true);");
      directionsMenu.setVisible(true);
      showOnMapMenu.setVisible(true);

    } else {
      Log.d(TAG, "directionsMenu.setVisible(false); showOnMapMenu.setVisible(false);");

      directionsMenu.setVisible(false);
      showOnMapMenu.setVisible(false);

    }
    return true;
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    Log.d(TAG, "--> onCreateOptionsMenu");

    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.menu_detail, menu);


    MenuItem shareMenuItem = menu.findItem(R.id.share_details);
    mShareActionProvider = (ShareActionProvider) shareMenuItem.getActionProvider();

    if (getCurrentAmen() != null) {
      createShareIntent(getCurrentAmen());
    } else if (getCurrentStatement() != null) {
      createShareIntent(getCurrentStatement());
    }

    return true;
  }
  private void createShareIntent(Amen amen) {
    if (amen == null) {
      return;
    }
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    String amenText = amen.getStatement().toDisplayString();
    shareIntent.setType("text/plain");
    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, amenText + " #getamen https://getamen.com/" + amen.getUser().getUsername() + "/amen/" + amen.getStatement().getSlug());
    if (mShareActionProvider != null) {
      mShareActionProvider.setShareIntent(shareIntent);
    }
  }
  private void createShareIntent(Statement statement) {
    if (statement == null) {
      return;
    }
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    String amenText = statement.toDisplayString();
    shareIntent.setType("text/plain");
    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, amenText + " #getamen https://getamen.com/statements/" + statement.getId());
    if (mShareActionProvider != null) {
      mShareActionProvider.setShareIntent(shareIntent);
    }
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);

    Log.d(TAG, "onOptionsItemSelected -> item.getItemId(): " + item.getItemId());

    final Intent amenListIntent = new Intent(this, AmenListFragmentActivity.class);
    amenListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

    switch (item.getItemId()) {

      case android.R.id.home: {
        startActivity(amenListIntent);
        return true;
      }

      case R.id.scoreboard: {
        startScoreBoardActivity();
        return true;
      }

      case R.id.subject_page: {
        Intent intent = new Intent(this, SubjectPageActivity.class);


        intent.putExtra(Constants.EXTRA_OBJEKT_ID, getCurrentStatement().getObjekt().getId());
        startActivity(intent);
        return true;
      }

      case R.id.show_on_map: {
        startShowOnMapActivity();
        return true;
      }
      case R.id.route_there: {
        startRouteThereActivity();
        return true;
      }
    }
    return false;
  }

  private void startRouteThereActivity() {

    Double lat = getCurrentStatement().getObjekt().getLat();
    Double lng = getCurrentStatement().getObjekt().getLng();

    final String uriString = "http://maps.google.com"
                             + "/maps?f=d&daddr="
                             + lat
                             + ','
                             + lng;
    Log.d(TAG, "uriString for route: " + uriString);
    Intent showOnMapIntent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(uriString));

    startActivity(showOnMapIntent);
  }

  private void startShowOnMapActivity() {

    Double lat = getCurrentStatement().getObjekt().getLat();
    Double lng = getCurrentStatement().getObjekt().getLng();
    String label = getCurrentStatement().getObjekt().getName().replaceAll("\\(", "").replaceAll("\\)", "");

//    final String uriString = "geo:" + lat + ',' + lng + "?z=15";
    final String uriString = "https://maps.google.com/maps?q=" + lat + ',' + lng + "(" + label +")" + "&z=15";

    Log.d(TAG, "uriString for show on map: " + uriString);

    Intent showOnMapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));

    try {
      startActivity(showOnMapIntent);
    } catch (ActivityNotFoundException anf) {
      toast("No activity found to view " + uriString, Toast.LENGTH_LONG);
    }
  }

  private Amen getCurrentAmen() {
    return amenDetailFragment.getCurrentAmen();
  }

  private Statement getCurrentStatement() {
    return amenDetailFragment.getCurrentStatement();
  }

  private void startScoreBoardActivity() {
    Intent intent = new Intent(this, ScoreBoardActivity.class);


    intent.putExtra(Constants.EXTRA_TOPIC, getCurrentStatement().getTopic());
    intent.putExtra(Constants.EXTRA_OBJEKT_KIND, getCurrentStatement().getObjekt().getKindId());
    startActivity(intent);
  }

  interface FooListener {
    void foo();
  }

  private void toast(final String msg, final int duration) {

    Toast.makeText(this, msg, duration).show();

  }
}
