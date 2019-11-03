package com.takusemba.cropmesample.ui.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.takusemba.cropme.CropLayout
import com.takusemba.cropme.OnCropListener
import com.takusemba.cropmesample.R
import com.takusemba.cropmesample.clients.AlbumClient
import com.takusemba.cropmesample.models.Photo
import com.takusemba.cropmesample.ui.OnPhotoClickListener
import com.takusemba.cropmesample.ui.adapters.AlbumAdapter
import java.util.ArrayList

class CropActivity : AppCompatActivity() {

  private val albumClient: AlbumClient by lazy { AlbumClient(this) }

  private val backButton by lazy { findViewById<ImageView>(R.id.cross) }
  private val cropButton by lazy { findViewById<ImageView>(R.id.crop) }
  private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }
  private val parent by lazy { findViewById<ConstraintLayout>(R.id.container) }
  private val cropLayout by lazy { findViewById<CropLayout>(R.id.crop_view) }
  private val progressBar by lazy { findViewById<ProgressBar>(R.id.progress) }

  private lateinit var adapter: AlbumAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(intent.getIntExtra(EXTRA_SHAPE_TYPE, -1))

    val listener = object : OnPhotoClickListener {
      override fun onPhotoClicked(photo: Photo) {
        cropLayout.setUri(photo.uri)
      }
    }
    adapter = AlbumAdapter(this, ArrayList(), listener)

    backButton.setOnClickListener { finish() }

    cropButton.setOnClickListener(View.OnClickListener {
      if (cropLayout.isOffFrame()) {
        Snackbar.make(parent, R.string.error_image_is_off_of_frame, Snackbar.LENGTH_LONG).show()
        return@OnClickListener
      }
      progressBar.visibility = View.VISIBLE
      cropLayout.crop(object : OnCropListener {
        override fun onSuccess(bitmap: Bitmap) {
          progressBar.visibility = View.GONE
          val view = layoutInflater.inflate(R.layout.dialog_result, null)
          view.findViewById<ImageView>(R.id.image).setImageBitmap(bitmap)
          AlertDialog.Builder(this@CropActivity).setView(view).show()
        }

        override fun onFailure() {
          Snackbar.make(parent, R.string.error_failed_to_clip_image, Snackbar.LENGTH_LONG).show()
        }
      })
    })

    val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    recyclerView.layoutManager = layoutManager
    recyclerView.adapter = adapter

    if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(this,
              Manifest.permission.READ_EXTERNAL_STORAGE)) {
        Snackbar.make(parent, R.string.error_permission_is_off, Snackbar.LENGTH_LONG).show()
      } else {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_CODE_PERMISSION)
      }
    } else {
      val vto = cropLayout.viewTreeObserver
      vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
          loadAlbums()
          when {
            vto.isAlive -> vto.removeOnPreDrawListener(this)
            else -> cropLayout.viewTreeObserver.removeOnPreDrawListener(this)
          }
          return true
        }
      })
    }
  }

  override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<String>,
      grantResults: IntArray
  ) {
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

  private fun loadAlbums() {
    adapter.clear()
    val result = albumClient.getAlbums()
    for (album in result) {
      if (album.photos.isNotEmpty()) {
        if (adapter.itemCount == 0) {
          val photo = album.photos[0]
          cropLayout.setUri(photo.uri)
        }
        adapter.addItem(album)
      }
    }
  }

  companion object {

    const val EXTRA_SHAPE_TYPE = "shape_type"

    private const val REQUEST_CODE_PERMISSION = 100
  }
}
