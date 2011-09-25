package com.jaeckel.amdroid.test;

import android.test.ActivityInstrumentationTestCase2;
import com.jaeckel.amdroid.*;

public class HelloAndroidActivityTest extends ActivityInstrumentationTestCase2<AmenListActivity> {

    public HelloAndroidActivityTest() {
        super(AmenListActivity.class);
    }

    public void testActivity() {
        AmenListActivity activity = getActivity();
        assertNotNull(activity);
    }
}

