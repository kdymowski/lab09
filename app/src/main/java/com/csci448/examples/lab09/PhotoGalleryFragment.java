package com.csci448.examples.lab09;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays Pictures from Flickr
 *
 * Created by jpaone on 2/22/16.
 */
public class PhotoGalleryFragment extends Fragment {



    /**
     * TAG used for Logging
     */
    private static final String TAG = "PhotoGalleryFragment";

    private List<GalleryItem> mItems = new ArrayList<>();
    /**
     * Our RecyclerView object to display the photos
     */
    private RecyclerView mPhotoRecyclerView;
    /**
     * The Adapter for our RecyclerView
     */
    private PhotoAdapter mPhotoAdapter;
    /**
     * The current list of photos to be displaying
     */
    private List<GalleryItem> mGalleryItems;



    /**
     * Creates a new instance of our Fragment
     *
     * @return  a properly created PhotoGalleryFragment
     */
    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    /**
     * Called when the fragment is created.  Sets the fragment to retain its state.
     * Begins our background thread to handle networking
     *
     * @param savedInstanceState    a saved bundle from a previous run
     */
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        Log.d( TAG, "onCreate( Bundle ) called" );

        // retain the fragment
        setRetainInstance( true );
        new FetchItemsTask().execute();
        mGalleryItems = new ArrayList<>();
    }

    /**
     * Called when the view is created.  Gets a reference to our recycler view
     *
     * @param inflater              our layout inflater
     * @param container             the parent container
     * @param savedInstanceState    a state from a saved previous run
     * @return                      the created view
     */
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        Log.d( TAG, "onCreateView() called" );

        View view = inflater.inflate( R.layout.fragment_photo_gallery, container, false );

        mPhotoRecyclerView = (RecyclerView) view.findViewById( R.id.fragment_photo_gallery_recycler_view );
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mPhotoAdapter = null;

        setupAdapter();

        return view;
    }

    private class FetchItemsTask extends AsyncTask<Void,Void, List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            return new FlickrFetchr().fetchItems();
        }
        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            setupAdapter();
        }
    }

    /**
     * Sets up our RecyclerView Adapter if the Fragment has been attached to an
     * activity
     */
    private void setupAdapter() {
        Log.d( TAG, "setupAdapter() called" );

        // check if the fragment is added to an activity
        if( isAdded() ) {
            // set the adapter
            Log.d( TAG, "Created a new adapter" );
            mPhotoAdapter = new PhotoAdapter( mGalleryItems );
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
            //mPhotoRecyclerView.setAdapter( mPhotoAdapter );
        } else if( !isAdded() ) {
            Log.d( TAG, "fragment not added to activity yet" );
        }
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState ) {
        super.onActivityCreated( savedInstanceState );
        Log.d( TAG, "onActivityCreated( Bundle ) called" );
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    /**
     * The ViewHolder for our RecyclerView to display Photos
     */
    private class PhotoHolder extends RecyclerView.ViewHolder {
        /**
         * A textview to display the photo caption
         */
        private TextView mTitleTextView;

        /**
         * Creates a new ViewHolder
         *
         * @param itemView  currently must be a text view
         */
        public PhotoHolder( View itemView ) {
            super(itemView);

            mTitleTextView = (TextView) itemView;
        }

        /**
         * Binds a gallery item to this ViewHolder
         * @param item  the gallery item to bind
         * @see GalleryItem
         */
        public void bindGalleryItem( GalleryItem item ) {
            mTitleTextView.setText( item.toString() );
        }
    }

    /**
     * The Adapter for our RecyclerView
     */
    private class PhotoAdapter extends RecyclerView.Adapter< PhotoHolder > {
        /**
         * The list of photos to display
         */
        private List<GalleryItem> mGalleryItems;

        /**
         * Create a new adapter with a given set of photos
         * @param galleryItems  the photos to display
         */
        public PhotoAdapter( List<GalleryItem> galleryItems ) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder( ViewGroup viewGroup, int viewType ) {
            TextView textView = new TextView( getActivity() );
            return new PhotoHolder( textView );
        }

        @Override
        public void onBindViewHolder( PhotoHolder photoHolder, int position ) {
            GalleryItem galleryItem = mGalleryItems.get( position );
            photoHolder.bindGalleryItem( galleryItem );
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }
}
