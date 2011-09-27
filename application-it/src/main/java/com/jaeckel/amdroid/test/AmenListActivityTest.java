package com.jaeckel.amdroid.test;

import android.test.ActivityInstrumentationTestCase2;
import com.jaeckel.amdroid.*;

public class AmenListActivityTest extends ActivityInstrumentationTestCase2<AmenListActivity> {

    public AmenListActivityTest() {
        super("com.jaeckel.amdroid", AmenListActivity.class);
    }

    public void testActivity() {
      AmenListActivity activity = getActivity();
        assertNotNull(activity);
    }
}

