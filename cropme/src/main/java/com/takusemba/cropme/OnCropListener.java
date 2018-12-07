package com.takusemba.cropme;

import android.graphics.Bitmap;

/**
 * OnCropListener
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
