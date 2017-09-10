package com.takusemba.cropmesample.ui.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.takusemba.cropmesample.R;
import com.takusemba.cropmesample.clients.ImageClient;

/**
 * Created by takusemba on 2017/09/10.
 */

public class ResultActivity extends AppCompatActivity {

    private ImageClient imageClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        imageClient = new ImageClient(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = imageClient.getBitmap();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((ImageView) findViewById(R.id.image)).setImageBitmap(bitmap);
                    }
                });
            }
        }).start();
    }
}
