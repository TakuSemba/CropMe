package com.takusemba.cropmesample.clients

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.takusemba.cropmesample.clients.loaders.AlbumLoader
import com.takusemba.cropmesample.clients.loaders.PhotoLoader
import com.takusemba.cropmesample.models.Album
import com.takusemba.cropmesample.models.Photo
import java.util.ArrayList

class AlbumClient(context: Context) {

  private val albumLoader: AlbumLoader = AlbumLoader(context)

  fun getAlbums(): List<Album> {
    val albums = ArrayList<Album>()
    try {
      val albumCursor = albumLoader.loadInBackground() ?: return emptyList()
      if (albumCursor.moveToFirst()) {
        do {
          val photoLoader = PhotoLoader(albumLoader.context, arrayOf(
              albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))))
          val photoCursor = photoLoader.loadInBackground() ?: break
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
          val id = albumCursor.getString(
              albumCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
          val name = albumCursor.getString(
              albumCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
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
}
