package com.takusemba.cropmesample.clients.loaders

import android.content.Context
import android.provider.MediaStore
import android.support.v4.content.CursorLoader


/**
 * [CursorLoader] to load photos
 */
class PhotoLoader(
        context: Context,
        selectionArgs: Array<String>
) : CursorLoader(
        context,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        PROJECTION,
        SELECTION,
        selectionArgs,
        ORDER_BY
) {

    companion object {

        private val PROJECTION = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.DATE_TAKEN)

        private val ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC"

        private const val SELECTION = MediaStore.Images.Media.BUCKET_ID + " = ?"
    }
}
