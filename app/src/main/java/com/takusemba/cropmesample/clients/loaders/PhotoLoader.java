package com.takusemba.cropmesample.clients.loaders;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

/**
 * Created by TakuSemba on 2016/03/03.
 */
public class PhotoLoader extends CursorLoader {

    private static final String[] PROJECTION = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.DATE_TAKEN};

    private static final String ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC";

    private static final String SELECTION = MediaStore.Images.Media.BUCKET_ID + " = ?";


    public PhotoLoader(Context context, String[] selectionArgs) {
        super(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PROJECTION, SELECTION,
                selectionArgs, ORDER_BY);
    }
}
