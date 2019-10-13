package com.takusemba.cropmesample.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.takusemba.cropme.CropLayout
import com.takusemba.cropme.OnCropListener
import com.takusemba.cropmesample.R
import com.takusemba.cropmesample.clients.AlbumClient
import com.takusemba.cropmesample.clients.ImageClient
import com.takusemba.cropmesample.models.Photo
import com.takusemba.cropmesample.ui.OnPhotoClickListener
import com.takusemba.cropmesample.ui.adapters.AlbumAdapter
import java.util.*
import kotlin.concurrent.thread

class CropActivity : AppCompatActivity() {

    private val albumClient: AlbumClient by lazy { AlbumClient(this) }
    private val imageClient: ImageClient by lazy { ImageClient(this) }

    private val backButton by lazy { findViewById<ImageView>(R.id.cross) }
    private val cropButton by lazy { findViewById<ImageView>(R.id.crop) }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }
    private val parent by lazy { findViewById<RelativeLayout>(R.id.container) }
    private val cropLayout by lazy { findViewById<CropLayout>(R.id.crop_view) }
    private val progressBar by lazy { findViewById<ProgressBar>(R.id.progress) }

    private lateinit var adapter: AlbumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)

        val listener = object : OnPhotoClickListener {
            override fun onPhotoClicked(photo: Photo) {
                cropLayout.setUri(photo.uri)
            }
        }
        adapter = AlbumAdapter(this, ArrayList(), listener)

        backButton.setOnClickListener { finish() }

        cropButton.setOnClickListener(View.OnClickListener {
            if (cropLayout.isOffOfFrame) {
                Snackbar.make(parent, R.string.error_image_is_off_of_frame, Snackbar.LENGTH_LONG).show()
                return@OnClickListener
            }
            cropLayout.crop(object : OnCropListener {
                override fun onSuccess(bitmap: Bitmap) {
                    saveBitmapAndStartActivity(bitmap)
                }

                override fun onFailure() {

                }
            })
        })

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(parent, R.string.error_permission_is_off, Snackbar.LENGTH_LONG).show()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION)
            }
        } else {
            // TODO Fix this workaround
            Handler().postDelayed({ loadAlbums() }, 1000)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadAlbums()
            } else {
                Snackbar.make(parent, R.string.error_permission_denied, Snackbar.LENGTH_LONG).show()
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun saveBitmapAndStartActivity(bitmap: Bitmap) {
        progressBar.visibility = View.VISIBLE
        cropLayout.isEnabled = false
        thread {
            imageClient.saveBitmap(bitmap)
            runOnUiThread {
                progressBar.visibility = View.GONE
                cropLayout.isEnabled = true
                startActivity(Intent(this, ResultActivity::class.java))
            }
        }
    }

    private fun loadAlbums() {
        adapter.clear()
        val result = albumClient.getAlbums()
        for (album in result) {
            thread {
                runOnUiThread {
                    if (album.photos.isNotEmpty()) {
                        if (adapter.itemCount == 0) {
                            val photo = album.photos[0]
                            cropLayout.setUri(photo.uri)
                        }
                        adapter.addItem(album)
                    }
                }
            }
        }
    }

    companion object {

        private const val REQUEST_CODE_PERMISSION = 100
    }

}
