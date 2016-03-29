package com.csci448.examples.lab09;

import android.support.v4.app.Fragment;
import android.util.Log;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    private static final String TAG = "csci448.lab09.act";

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected Fragment createFragment() {
        Log.d( TAG, "creatingFragment() called" );
        return PhotoGalleryFragment.newInstance();
    }
}
