package com.takusemba.cropmesample

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.takusemba.cropme.CropLayout
import com.takusemba.cropme.OnCropListener

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

    val contentLauncher = registerForActivityResult(GetContent()) { uri: Uri? ->
      if (uri == null) return@registerForActivityResult
      cropLayout.setUri(uri)
    }

    selectButton.setOnClickListener { contentLauncher.launch("image/*") }

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
