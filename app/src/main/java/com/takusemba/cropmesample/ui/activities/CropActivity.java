package com.takusemba.cropmesample.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.takusemba.cropmesample.R;
import com.takusemba.cropmesample.clients.AlbumClient;
import com.takusemba.cropmesample.models.Album;
import com.takusemba.cropmesample.ui.adapters.AlbumAdapter;

import java.util.ArrayList;
import java.util.List;

public class CropActivity extends AppCompatActivity {

    private AlbumClient client;
    private AlbumAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        client = new AlbumClient(this);

        findViewById(R.id.cross).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.crop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CropActivity.this, ResultActivity.class));
            }
        });

        adapter = new AlbumAdapter(CropActivity.this, new ArrayList<Album>());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CropActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        final List<Album> result = client.getAlbums();
        for (final Album album : result) {
            new Thread(new Runnable() {
                public void run() {
                    client.getResizedBitmap(CropActivity.this, album);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.addItem(album);
                        }
                    });
                }
            }).start();
        }
    }


}
