package com.takusemba.cropmesample;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;
import com.takusemba.cropme.CropView;
import com.takusemba.cropme.OnCropListener;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RxImagePicker.with(this).requestImage(Sources.GALLERY).subscribe(new Consumer<Uri>() {
            @Override
            public void accept(Uri uri) throws Exception {
                ((CropView) findViewById(R.id.crop_me_view)).setUri(uri);
            }
        });

        findViewById(R.id.crop_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CropView) findViewById(R.id.crop_me_view)).crop(new OnCropListener() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        ((ImageView) findViewById(R.id.result)).setImageBitmap(bitmap);
                    }

                    @Override
                    public void onFailure() {
                        Log.d("mydebug", "failed");
                    }
                });
            }
        });

    }

}
