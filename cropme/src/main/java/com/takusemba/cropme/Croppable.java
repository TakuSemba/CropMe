package com.takusemba.cropme;

import android.net.Uri;

/**
 * Croppable
 *
 * @author takusemba
 * @since 05/09/2017
 **/
interface Croppable {

    void setUri(Uri uri);

    void crop(OnCropListener listener);
}
