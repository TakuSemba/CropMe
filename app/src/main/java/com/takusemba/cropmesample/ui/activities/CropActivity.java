package com.takusemba.cropmesample.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.takusemba.cropme.CropView;
import com.takusemba.cropme.OnCropListener;
import com.takusemba.cropmesample.R;
import com.takusemba.cropmesample.clients.AlbumClient;
import com.takusemba.cropmesample.clients.ImageClient;
import com.takusemba.cropmesample.models.Album;
import com.takusemba.cropmesample.ui.adapters.AlbumAdapter;

import java.util.ArrayList;
import java.util.List;

public class CropActivity extends AppCompatActivity {

    private AlbumClient albumClient;
    private ImageClient imageClient;
    private AlbumAdapter adapter;

    private ImageView backButton;
    private ImageView cropButton;
    private RecyclerView recyclerView;
    private CropView cropView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        findViewsByIds();

        albumClient = new AlbumClient(this);
        imageClient = new ImageClient(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        adapter = new AlbumAdapter(CropActivity.this, new ArrayList<Album>());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropView.crop(new OnCropListener() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        saveBitmapAndStartActivity(bitmap);
                    }

                    @Override
                    public void onFailure() {

                    }
                });
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CropActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        final List<Album> result = albumClient.getAlbums();
        for (final Album album : result) {
            new Thread(new Runnable() {
                public void run() {
                    albumClient.getResizedBitmap(CropActivity.this, album);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!album.photos.isEmpty()) {
                                if (adapter.getItemCount() == 0) {
                                    Uri uri = album.photos.get(0).uri;
                                    cropView.setUri(uri);
                                }
                                adapter.addItem(album);
                            }
                        }
                    });
                }
            }).start();
        }
    }

    private void findViewsByIds() {
        backButton = (ImageView) findViewById(R.id.cross);
        cropButton = (ImageView) findViewById(R.id.crop);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        cropView = (CropView) findViewById(R.id.crop_view);
    }

    private void saveBitmapAndStartActivity(final Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                imageClient.saveBitmap(bitmap);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(CropActivity.this, ResultActivity.class));
                    }
                });
            }
        }).start();
    }

}
