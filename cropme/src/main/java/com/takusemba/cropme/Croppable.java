package com.takusemba.cropme;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

/**
 * Interface to set an Image, and return an cropped image back.
 **/
interface Croppable {

    /**
     * setUri to {@link ImageView}
     **/
    void setUri(Uri uri);

    /**
     * setBitmap to {@link ImageView}
     **/
    void setBitmap(Bitmap bitmap);

    /**
     * check if Image is out of the frame.
     */
    boolean isOffOfFrame();

    /**
     * crop image.
     **/
    void crop(OnCropListener listener);
}
