package com.jaeckel.amenoid;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * @author biafra
 * @date 5/1/12 5:22 PM
 */
public class FragmentDemo extends FragmentActivity {
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//
//    FragmentManager fragmentManager = this.getFragmentManager();
//    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//    WindowManager windowManager = getWindowManager();
//    Display d = windowManager.getDefaultDisplay();
//    if (d.getWidth() > d.getHeight()) {
//      //landscape
//      Fragment1 fragment1 = new Fragment1();
//      fragmentTransaction.replace(android.R.id.content, fragment1);
//    } else {
//      //portrait
//      Fragment2 fragment2 = new Fragment2();
//      fragmentTransaction.replace(android.R.id.content, fragment2);
//    }
//    fragmentTransaction.commit();

    setContentView(R.layout.frag);


  }


  @Override
  public void onResume() {
    super.onResume();

//    FragmentManager fragmentManager = this.getSupportFragmentManager();
//    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//    WindowManager windowManager = getWindowManager();
//    Display d = windowManager.getDefaultDisplay();
//    if (d.getWidth() > d.getHeight()) {
//      //landscape
//      Fragment1 fragment1 = new Fragment1();
//      fragmentTransaction.replace(android.R.id.content, fragment1);
//    } else {
//      //portrait
//      Fragment2 fragment2 = new Fragment2();
//      fragmentTransaction.replace(android.R.id.content, fragment2);
//    }
//    fragmentTransaction.commit();

  }
}