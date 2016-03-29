package com.csci448.examples.lab09;

/**
 * Created by jpaone on 2/23/16.
 */
public class GalleryItem {

    /**
     * The caption for the gallery item
     */
    private String mCaption;
    /**
     * The id for the gallery item
     */
    private String mId;
    /**
     * The URL for the gallery item
     */
    private String mUrl;

    @Override
    public String toString() {
        return mCaption;
    }

    /**
     * Returns the value of mCaption
     *
     * @return Gets the value of mCaption
     *
     * @see String
     */
    public String getCaption() {
        return mCaption;
    }

    /**
     * Sets the value of mCaption
     *
     * @param caption value to set mCaption to
     *
     * @see String
     */
    public void setCaption( String caption ) {
        mCaption = caption;
    }

    /**
     * Returns the value of mId
     *
     * @return Gets the value of mId
     *
     * @see String
     */
    public String getId() {
        return mId;
    }

    /**
     * Sets the value of mId
     *
     * @param id value to set mId to
     *
     * @see String
     */
    public void setId( String id ) {
        mId = id;
    }

    /**
     * Returns the value of mUrl
     *
     * @return Gets the value of mUrl
     *
     * @see String
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * Sets the value of mUrl
     *
     * @param url value to set mUrl to
     *
     * @see String
     */
    public void setUrl( String url ) {
        mUrl = url;
    }
}
