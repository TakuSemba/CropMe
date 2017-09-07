package com.takusemba.cropme;

import android.graphics.Bitmap;

/**
 * Created by takusemba on 2017/09/08.
 */

public interface OnCropListener {

    void onSuccess(Bitmap bitmap);

    void onFailure();
}
