package com.takusemba.cropme

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView

import androidx.annotation.MainThread

/**
 * Interface to set an Image, and return an cropped image back.
 */
interface Croppable {

  /**
   * check if Image is out of the frame.
   */
  fun isOffFrame(): Boolean

  /**
   * setUri to [ImageView]
   */
  fun setUri(uri: Uri)

  /**
   * setBitmap to [ImageView]
   */
  fun setBitmap(bitmap: Bitmap)

  /**
   * Crop the Image on a new thread, and return the cropped Bitmap on MainThread via [OnCropListener].
   */
  @MainThread
  fun crop(listener: OnCropListener)
}
