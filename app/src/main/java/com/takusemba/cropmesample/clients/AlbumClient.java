package com.takusemba.cropmesample.clients;

import android.content.ContentUris;
import android.content.Context;
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

    public interface OnLoadedListener {
        void onSuccess(List<Album> albums);

        void onError(Exception e);
    }

    private final AlbumLoader albumLoader;

    public AlbumClient(Context context) {
        this.albumLoader = new AlbumLoader(context);
    }

    public void getAlbums(OnLoadedListener listener) {
        List<Album> albums = new ArrayList<>();
        try {
            Cursor albumCursor = albumLoader.loadInBackground();
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
            listener.onSuccess(albums);
        } catch (Exception e) {
            listener.onError(e);
        }
    }
}
