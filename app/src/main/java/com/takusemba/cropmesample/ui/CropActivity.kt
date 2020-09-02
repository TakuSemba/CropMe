package com.takusemba.cropmesample.ui

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.takusemba.cropme.CropLayout
import com.takusemba.cropme.OnCropListener
import com.takusemba.cropmesample.BuildConfig
import com.takusemba.cropmesample.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CropActivity : AppCompatActivity() {
  private val backButton by lazy { findViewById<ImageView>(R.id.cross) }
  private val selectButton by lazy { findViewById<ImageView>(R.id.select) }
  private val cropButton by lazy { findViewById<ImageView>(R.id.crop) }
  private val parent by lazy { findViewById<ConstraintLayout>(R.id.container) }
  private val cropLayout by lazy { findViewById<CropLayout>(R.id.crop_view) }
  private val progressBar by lazy { findViewById<ProgressBar>(R.id.progress) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val layoutId = when (intent.getStringExtra(EXTRA_SHAPE_TYPE)) {
      RECTANGLE -> R.layout.activity_crop_rectangle
      CIRCLE -> R.layout.activity_crop_circle
      CUSTOM -> R.layout.activity_crop_custom
      else -> throw IllegalStateException("unknown shape")
    }
    setContentView(layoutId)

    backButton.setOnClickListener { finish() }

    selectButton.setOnClickListener {

      MaterialAlertDialogBuilder(this)
          .setTitle(getString(R.string.dialog_message_get_picture))
          .setItems(
              arrayOf(
                  getString(R.string.dialog_message_select_picture),
                  getString(R.string.dialog_message_take_picture)
              )
          ) { dialog, which ->
            when (which) {
              0 -> {
                val launcher = registerForActivityResult(GetContent()) { uri: Uri? ->
                  if (uri == null) return@registerForActivityResult
                  cropLayout.setUri(uri)
                }
                launcher.launch("image/*")
              }
              1 -> {
                val format = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                val timestamp = format.format(Date())
                val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                val file = File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
                val authority = "${BuildConfig.APPLICATION_ID}.fileprovider"
                val uri = FileProvider.getUriForFile(this, authority, file)
                val launcher = registerForActivityResult(TakePicture()) { isSuccessful: Boolean ->
                  if (!isSuccessful) return@registerForActivityResult
                  cropLayout.setUri(uri)
                }
                launcher.launch(uri)
              }
            }
            dialog.dismiss()
          }
          .show()
    }

    cropLayout.addOnCropListener(object : OnCropListener {
      override fun onSuccess(bitmap: Bitmap) {
        progressBar.visibility = View.GONE

        val view = layoutInflater.inflate(R.layout.dialog_result, null)
        view.findViewById<ImageView>(R.id.image).setImageBitmap(bitmap)
        MaterialAlertDialogBuilder(this@CropActivity)
            .setTitle(R.string.dialog_title_result)
            .setView(view)
            .setPositiveButton(R.string.dialog_button_close) { dialog, _ -> dialog.dismiss() }
            .show()
      }

      override fun onFailure(e: Exception) {
        Snackbar.make(parent, R.string.error_failed_to_clip_image, Snackbar.LENGTH_LONG).show()
      }
    })

    cropButton.setOnClickListener(View.OnClickListener {
      if (cropLayout.isOffFrame()) {
        Snackbar.make(parent, R.string.error_image_is_off_frame, Snackbar.LENGTH_LONG).show()
        return@OnClickListener
      }
      progressBar.visibility = View.VISIBLE
      cropLayout.crop()
    })
  }

  companion object {

    const val EXTRA_SHAPE_TYPE = "shape_type"

    const val RECTANGLE = "Rectangle"
    const val CIRCLE = "Circle"
    const val CUSTOM = "Custom"
  }
}
