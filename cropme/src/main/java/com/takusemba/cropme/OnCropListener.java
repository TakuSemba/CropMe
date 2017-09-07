package com.takusemba.cropme;

import android.graphics.Bitmap;

/**
 * Created by takusemba on 2017/09/08.
 */

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
