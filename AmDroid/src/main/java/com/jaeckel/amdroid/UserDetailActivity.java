package com.jaeckel.amdroid;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.jaeckel.amdroid.api.AmenService;
import com.jaeckel.amdroid.api.model.Amen;
import com.jaeckel.amdroid.api.model.User;
import com.jaeckel.amdroid.api.model.UserInfo;
import com.jaeckel.amdroid.app.AmdroidApp;

import java.util.List;

/**
 * User: biafra
 * Date: 10/15/11
 * Time: 5:39 AM
 */
public class UserDetailActivity extends ListActivity {

  private static String TAG = "amdroid/UserDetailActivity";

  private User        currentUser;
  private AmenService service;
  private AmenAdapter adapter;


  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    service = AmdroidApp.getInstance().getService();

    setContentView(R.layout.user);

    ListView list = (ListView) findViewById(android.R.id.list);
    View header = getLayoutInflater().inflate(R.layout.user_header, null, false);
    list.addHeaderView(header);


    Intent startingIntent = getIntent();

    currentUser = startingIntent.getParcelableExtra(Constants.EXTRA_USER);
    final UserInfo userInfo = service.getUserInfo(currentUser.getId());

    Log.d(TAG, "======> currentUser: " + currentUser);

    List<Amen> amen = service.getAmenForUser(currentUser.getId());

    adapter = new AmenAdapter(this, android.R.layout.simple_list_item_1, amen);

    setListAdapter(adapter);

    TextView userName = (TextView) findViewById(R.id.name);
    userName.setText(userInfo.getName());

    final TextView follow = (TextView) findViewById(R.id.follow);

    if (userInfo.getFollowing() != null && userInfo.getFollowing()) {
      follow.setBackgroundColor(Color.CYAN);
      follow.setText("Following");
    } else {
      follow.setBackgroundColor(Color.GRAY);
    }
    
    follow.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        if (userInfo.getFollowing()) {
          service.unfollow(currentUser);
          follow.setBackgroundColor(Color.GRAY);
        } else {
          service.follow(currentUser);
          follow.setBackgroundColor(Color.CYAN);
        }
      }
    });

    TextView followers = (TextView) findViewById(R.id.followers);
    followers.setText(userInfo.getFollowersCount() + " Followers");

    TextView following = (TextView) findViewById(R.id.following);
    following.setText(userInfo.getFollowingCount() + " Following");


  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {

    Amen amen = (Amen) getListAdapter().getItem(position - 1);

    Log.d(TAG, "Selected Amen: " + amen);

    Intent intent = new Intent(this, AmenDetailActivity.class);
    intent.putExtra(Constants.EXTRA_AMEN, amen);
    startActivity(intent);
  }


}