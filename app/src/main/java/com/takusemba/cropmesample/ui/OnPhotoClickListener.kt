package com.takusemba.cropmesample.ui

import com.takusemba.cropmesample.models.Photo

/**
 * listener to notify when photo item is clicked.
 */
interface OnPhotoClickListener {

    /**
     * called when photo item in list is clicked.
     */
    fun onPhotoClicked(photo: Photo)
}
