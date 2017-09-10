package com.takusemba.cropmesample.clients;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.takusemba.cropmesample.clients.loaders.AlbumLoader;
import com.takusemba.cropmesample.clients.loaders.PhotoLoader;
import com.takusemba.cropmesample.models.Album;
import com.takusemba.cropmesample.models.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by takusemba on 2017/09/10.
 */

public class AlbumClient {

    interface OnLoadedListener {
        void onSuccess(List<Photo> photos);

        void onError();
    }

    private final AlbumLoader albumLoader;

    public AlbumClient(AlbumLoader albumLoader) {
        this.albumLoader = albumLoader;
    }

    public List<Album> getAlbums() {
        Cursor albumCursor = albumLoader.loadInBackground();
        List<Album> albums = new ArrayList<>();
        if (albumCursor.moveToFirst()) {
            do {
                PhotoLoader photoLoader = new PhotoLoader(albumLoader.getContext(), new String[]{albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))});
                Cursor photoCursor = photoLoader.loadInBackground();
                List<Photo> photos = new ArrayList<>();
                if (photoCursor.moveToFirst()) {
                    do {
                        Long id = photoCursor.getLong(photoCursor.getColumnIndex(MediaStore.Images.Media._ID));
                        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                        Photo photo = new Photo();
                        photo.id = id;
                        photo.uri = uri;
                        photos.add(photo);
                    } while (photoCursor.moveToNext());
                }
                Album album = new Album();
                album.bucketId = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
                album.name = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                album.photos = photos;
                albums.add(album);
            } while (albumCursor.moveToNext());
        }
        return albums;
    }
}
