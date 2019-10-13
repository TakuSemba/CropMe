package com.takusemba.cropmesample.clients.loaders

import android.content.Context
import android.provider.MediaStore
import androidx.loader.content.CursorLoader

/**
 * [CursorLoader] to load albums
 */
class AlbumLoader(
        context: Context
) : CursorLoader(
        context,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        PROJECTION,
        BUCKET_GROUP_BY,
        null,
        null
) {

    companion object {

        private val PROJECTION = arrayOf(MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media._ID)

        private const val BUCKET_GROUP_BY = "1) GROUP BY (1"
    }
}
