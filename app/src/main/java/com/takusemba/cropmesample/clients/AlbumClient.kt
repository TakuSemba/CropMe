package com.takusemba.cropmesample.clients

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.media.ExifInterface
import com.takusemba.cropmesample.clients.loaders.AlbumLoader
import com.takusemba.cropmesample.clients.loaders.PhotoLoader
import com.takusemba.cropmesample.models.Album
import com.takusemba.cropmesample.models.Photo
import java.io.IOException
import java.util.*

/**
 * Created by takusemba on 2017/09/10.
 */

class AlbumClient(context: Context) {

    private val albumLoader: AlbumLoader = AlbumLoader(context)

    fun getAlbums(): List<Album> {
        val albums = ArrayList<Album>()
        try {
            val albumCursor = albumLoader.loadInBackground()
            if (albumCursor.moveToFirst()) {
                do {
                    val photoLoader = PhotoLoader(albumLoader.context, arrayOf(albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))))
                    val photoCursor = photoLoader.loadInBackground()
                    val photos = ArrayList<Photo>()
                    if (photoCursor.moveToFirst()) {
                        do {
                            val id = photoCursor.getLong(photoCursor.getColumnIndex(MediaStore.Images.Media._ID))
                            val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                            val photo = Photo(id, uri)
                            photos.add(photo)
                        } while (photoCursor.moveToNext() && photos.size < 40)
                    }
                    photoCursor.close()
                    val id = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
                    val name = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                    val album = Album(id, name, photos)
                    albums.add(album)
                } while (albumCursor.moveToNext() && albums.size < 10)
            }
            albumCursor.close()
            return albums
        } catch (e: Exception) {
            return ArrayList()
        }
    }


    fun getResizedBitmap(context: Context, album: Album): Album {
        for (photo in album.photos) {
            photo.bitmap = getResizedBitmap(context, photo.uri)
        }
        return album
    }

    private fun getResizedBitmap(context: Context, uri: Uri): Bitmap? {
        try {
            val initStream = context.contentResolver.openInputStream(uri)
            val exifStream = context.contentResolver.openInputStream(uri)

            var orientation = 0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && exifStream != null) {
                val exif = ExifInterface(exifStream)
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED)
            }

            // 画像サイズ情報を取得する
            val imageOptions = BitmapFactory.Options()
            imageOptions.inJustDecodeBounds = true
            BitmapFactory.decodeStream(initStream, null, imageOptions)

            initStream?.close()

            // もし、画像が大きかったら縮小して読み込む
            //  今回はimageSizeMaxの大きさに合わせる
            val bitmap: Bitmap?
            val maxSize = 100
            val inputStream = context.contentResolver.openInputStream(uri)
            val widthScale = (imageOptions.outWidth / maxSize).toFloat()
            val heightScale = (imageOptions.outHeight / maxSize).toFloat()

            // もしも、縮小できるサイズならば、縮小して読み込む
            if (widthScale > 2 && heightScale > 2) {
                val options = BitmapFactory.Options()

                // 縦横、小さい方に縮小するスケールを合わせる
                val imageScale = Math.floor(Math.min(heightScale, widthScale).toDouble()).toInt()

                // inSampleSizeには2のべき上が入るべきなので、imageScaleに最も近く、かつそれ以下の2のべき上の数を探す
                var i = 2
                while (i <= imageScale) {
                    options.inSampleSize = i
                    i *= 2
                }

                bitmap = BitmapFactory.decodeStream(inputStream, null, options)
            } else {
                bitmap = BitmapFactory.decodeStream(inputStream)
            }

            inputStream?.close()
            return rotateBitmap(bitmap, orientation)

        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }

    private fun rotateBitmap(bitmap: Bitmap?, orientation: Int): Bitmap? {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> return bitmap
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.setRotate(180f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
            else -> return bitmap
        }
        return try {
            val rotatedBitmap = Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            rotatedBitmap
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            null
        }

    }
}
