package com.takusemba.cropmesample;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;
import com.takusemba.cropme.CropView;

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
        

    }

}
