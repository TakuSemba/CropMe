package com.takusemba.cropmesample.models

import android.graphics.Bitmap
import android.net.Uri

class Photo(
        val id: Long,
        val uri: Uri
) {

    var bitmap: Bitmap? = null

    var isSelected: Boolean = false
}
