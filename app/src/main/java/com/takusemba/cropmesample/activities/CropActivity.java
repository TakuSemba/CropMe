package com.takusemba.cropmesample.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.takusemba.cropmesample.R;

public class CropActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);


        findViewById(R.id.cross).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
