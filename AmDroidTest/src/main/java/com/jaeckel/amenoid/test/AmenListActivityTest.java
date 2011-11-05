package com.jaeckel.amenoid.test;

import android.test.ActivityInstrumentationTestCase2;
import com.jaeckel.amdroid.AmenListActivity;

public class AmenListActivityTest extends ActivityInstrumentationTestCase2<AmenListActivity> {

    public AmenListActivityTest() {
        super("com.jaeckel.amdroid", AmenListActivity.class);
    }

    public void testActivity() {
      AmenListActivity activity = getActivity();
        assertNotNull(activity);
    }
}

