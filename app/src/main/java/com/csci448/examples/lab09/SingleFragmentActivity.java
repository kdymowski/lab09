package com.csci448.examples.lab09;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Abstract class for a fragment activity that creates and stores a
 * single fragment
 *
 * Created by jpaone on 1/29/16.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    private static final String TAG = "csci448.singFragActvty";

    protected abstract Fragment createFragment();

    /**
     * This function should be overridden by children classes to have
     * the output associated with that child
     *
     * @return string of the TAG for logging
     */
    protected String getTag() {
        return TAG;
    }

    @LayoutRes
    protected int getLayoutResId() {
        Log.d( getTag(), "getLayoutResId() returning activity_fragment by default" );
        return R.layout.activity_fragment;
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, PhotoGalleryActivity.class);
    }

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        Log.d( getTag(), "onCreate(Bundle) called" );

        setContentView( getLayoutResId() );

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById( R.id.fragment_container ); // get the fragment associated with this container

        if( fragment == null ) {
            Log.d( getTag(), "creating new Fragment" );
            fragment = createFragment();                         // create a new fragment
            fm.beginTransaction()                               // create a fragment transaction
                    .add( R.id.fragment_container, fragment )   // add the Fragment to the associated container
                    .commit();                                  // commit this transaction to the FragmentManager
        } else {
            Log.d( getTag(), "fragment already exists" );
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d( getTag(), "onStart() called" );
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d( getTag(), "onPause() called" );
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d( getTag(), "onResume() called" );
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d( getTag(), "onStop() called" );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d( getTag(), "onDestroy() called" );
    }
}
