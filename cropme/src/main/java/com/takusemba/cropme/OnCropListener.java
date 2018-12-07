package com.takusemba.cropme;

import android.graphics.Bitmap;

/**
 * Listener to return an cropped image.
 **/
public interface OnCropListener {

    /**
     * called when cropping is successful
     *
     * @param bitmap result bitmap
     **/
    void onSuccess(Bitmap bitmap);

    /**
     * called when cropping is failed
     **/
    void onFailure();
}
