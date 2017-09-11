package com.takusemba.cropmesample.clients;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.media.ExifInterface;

import com.takusemba.cropmesample.clients.loaders.AlbumLoader;
import com.takusemba.cropmesample.clients.loaders.PhotoLoader;
import com.takusemba.cropmesample.models.Album;
import com.takusemba.cropmesample.models.Photo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by takusemba on 2017/09/10.
 */

public class AlbumClient {

    private final AlbumLoader albumLoader;


    public AlbumClient(Context context) {
        this.albumLoader = new AlbumLoader(context);
    }

    public List<Album> getAlbums() {

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
                            photo.isSelected = photos.isEmpty();
                            photos.add(photo);
                        } while (photoCursor.moveToNext() && photos.size() < 40);
                    }
                    photoCursor.close();
                    Album album = new Album();
                    album.bucketId = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
                    album.name = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    album.photos = photos;
                    album.isSelected = albums.isEmpty();
                    albums.add(album);
                } while (albumCursor.moveToNext() && albums.size() < 10);
            }
            albumCursor.close();
            return albums;
        } catch (final Exception e) {
            return new ArrayList<>();
        }
    }

    public Album getResizedBitmap(Context context, Album album) {
        for (Photo photo : album.photos) {
            photo.bitmap = getResizedBitmap(context, photo.uri);
        }
        return album;
    }

    private Bitmap getResizedBitmap(Context context, Uri uri) {
        try {
            InputStream initStream = context.getContentResolver().openInputStream(uri);
            InputStream exifStream = context.getContentResolver().openInputStream(uri);

            int orientation = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && exifStream != null) {
                ExifInterface exif = new ExifInterface(exifStream);
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
            }

            // 画像サイズ情報を取得する
            BitmapFactory.Options imageOptions = new BitmapFactory.Options();
            imageOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(initStream, null, imageOptions);

            if (initStream != null) {
                initStream.close();
            }

            // もし、画像が大きかったら縮小して読み込む
            //  今回はimageSizeMaxの大きさに合わせる
            Bitmap bitmap;
            int maxSize = 100;
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            float widthScale = imageOptions.outWidth / maxSize;
            float heightScale = imageOptions.outHeight / maxSize;

            // もしも、縮小できるサイズならば、縮小して読み込む
            if (widthScale > 2 && heightScale > 2) {
                BitmapFactory.Options options = new BitmapFactory.Options();

                // 縦横、小さい方に縮小するスケールを合わせる
                int imageScale = (int) Math.floor((Math.min(heightScale, widthScale)));

                // inSampleSizeには2のべき上が入るべきなので、imageScaleに最も近く、かつそれ以下の2のべき上の数を探す
                int i = 2;
                while (i <= imageScale) {
                    options.inSampleSize = i;
                    i *= 2;
                }

                bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            } else {
                bitmap = BitmapFactory.decodeStream(inputStream);
            }

            if (inputStream != null) {
                inputStream.close();
            }
            return rotateBitmap(bitmap, orientation);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1F, 1F);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180F);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL: {
                matrix.setRotate(180F);
                matrix.postScale(-1F, 1F);
                break;
            }
            case ExifInterface.ORIENTATION_TRANSPOSE: {
                matrix.setRotate(90F);
                matrix.postScale(-1F, 1F);
                break;
            }
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90F);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE: {
                matrix.setRotate(-90F);
                matrix.postScale(-1F, 1F);
                break;
            }
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90F);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return rotatedBitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
}
