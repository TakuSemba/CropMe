package com.takusemba.cropmesample.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.takusemba.cropmesample.R;

import java.util.List;

public class CropActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

//    private List<Uri> getPhotos(){
//        AlbumLoader albumLoader = new AlbumLoader(this);
//        Cursor albumCursor =
//    }
//
//
//    override fun getPhotos(): Single<List<Photo>> {
//        return Single.create<List<Photo>> { subscriber ->
//                val albumCursor = albumLoader.loadInBackground()
//                val photos: ArrayList<Photo> = ArrayList()
//        if (albumCursor.moveToFirst()) {
//            do {
//                val photoLoader = PhotoLoader(albumLoader.context, arrayOf(albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))))
//                val photoCursor = photoLoader.loadInBackground()
//                if (photoCursor.moveToFirst()) {
//                    do {
//                        val id = photoCursor.getLong(photoCursor.getColumnIndex(MediaStore.Images.Media._ID))
//                        val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
//                        val photo = Photo()
//                        photo.uri = uri.toString()
//                        photos.add(photo)
//                    } while (photoCursor.moveToNext() and (photos.size < 100))
//                }
//            } while (albumCursor.moveToNext() and (photos.size < 100))
//        }
//        albumCursor.close()
//        subscriber.onSuccess(photos)
//        }
//    }

}
