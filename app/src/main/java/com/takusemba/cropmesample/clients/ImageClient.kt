package com.takusemba.cropmesample.clients

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

import java.io.ByteArrayOutputStream

class ImageClient(private val prefs: SharedPreferences) {

    fun getBitmap(): Bitmap? {
        val bitmapStr = prefs.getString(KEY_BITMAP_STRING, "") ?: ""
        var bitmap: Bitmap? = null
        if (bitmapStr.isNotEmpty()) {
            val bytes = Base64.decode(bitmapStr, Base64.DEFAULT)
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).copy(Bitmap.Config.ARGB_8888, true)
        }
        return bitmap
    }

    fun saveBitmap(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val bitmapStr = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
        val editor = prefs.edit()
        editor.putString(KEY_BITMAP_STRING, bitmapStr)
        editor.apply()
    }

    companion object {

        private const val KEY_BITMAP_STRING = "key_bitmap_string"
    }
}
