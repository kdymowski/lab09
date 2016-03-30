package com.csci448.examples.lab09;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.SearchView;
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
    private SwipeRefreshLayout mSwipeRefreshLayout;
    /**
     * The Adapter for our RecyclerView
     */
    private PhotoAdapter mPhotoAdapter;
    /**
     * The current list of photos to be displaying
     */
    private List<GalleryItem> mGalleryItems;

    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

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
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate( Bundle ) called");

        // retain the fragment
        setRetainInstance(true);
        new FetchItemsTask().execute();
        mGalleryItems = new ArrayList<>();
        setHasOptionsMenu(true);

        //updateItems();
        //PollService.setServiceAlarm(getActivity(), true);
        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
                    @Override
                    public void onThumbnailDownloaded(PhotoHolder photoHolder, Bitmap bitmap) {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        photoHolder.bindDrawable(drawable);
                    }
                }
        );
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_photo_gallery, menu);
        //MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        //final SearchView searchView = (SearchView) searchItem.getActionView();


        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarmOn(getActivity())) {
            toggleItem.setTitle(R.string.stop_polling);
        } else {
         toggleItem.setTitle(R.string.start_polling);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


   /* private void updateItems() {
        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
    }
*/
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

        View view = inflater.inflate( R.layout.fragment_photo_gallery, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mPhotoRecyclerView = (RecyclerView) view.findViewById( R.id.fragment_photo_gallery_recycler_view );
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mPhotoAdapter = null;

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               new FetchItemsTask().execute();
                mSwipeRefreshLayout.setRefreshing(false);
            }


        });



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
        Log.d(TAG, "setupAdapter() called");

        // check if the fragment is added to an activity
        if( isAdded() ) {
            // set the adapter
            Log.d( TAG, "Created a new adapter" );
            mPhotoAdapter = new PhotoAdapter( mGalleryItems );
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        } else if( !isAdded() ) {
            Log.d( TAG, "fragment not added to activity yet" );
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState ) {
        super.onActivityCreated( savedInstanceState );
        Log.d(TAG, "onActivityCreated( Bundle ) called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    /**
     * The ViewHolder for our RecyclerView to display Photos
     */
    private class PhotoHolder extends RecyclerView.ViewHolder {

        private ImageView mItemImageView;

        /**
         * Creates a new ViewHolder
         *
         * @param itemView  currently must be a text view
         */
        public PhotoHolder( View itemView ) {
            super(itemView);
            mItemImageView = (ImageView) itemView
                    .findViewById(R.id.fragment_photo_gallery_image_view);
        }

        /**
         * Binds a gallery item to this ViewHolder
         * @param
         * @see GalleryItem
         */
        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
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
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, viewGroup, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder( PhotoHolder photoHolder, int position ) {
            GalleryItem galleryItem = mGalleryItems.get( position );
            Drawable placeholder = getResources().getDrawable(R.drawable.bill_up_close);
            photoHolder.bindDrawable(placeholder);
            mThumbnailDownloader.queueThumbnail(photoHolder, galleryItem.getUrl());
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }
}
