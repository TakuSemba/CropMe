package com.takusemba.cropmesample.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.takusemba.cropmesample.R
import com.takusemba.cropmesample.ui.CropActivity.Companion.EXTRA_SHAPE_TYPE

class ChooserActivity : AppCompatActivity() {

  private val samples: Array<String> = arrayOf(
      RECTANGLE,
      CIRCLE,
      CUSTOM)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_chooser)
    val listView = findViewById<ListView>(R.id.sample_list)
    val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, samples)
    listView.adapter = adapter
    listView.setOnItemClickListener { _, _, position, _ ->
      val intent = Intent(this, CropActivity::class.java)
      val layoutId = when (samples[position]) {
        // TODO pass type and retrieve layoutId in destination Activity.
        RECTANGLE -> R.layout.activity_crop_rectangle
        CIRCLE -> R.layout.activity_crop_circle
        CUSTOM -> R.layout.activity_crop_custom
        else -> throw IllegalStateException("unknown shape")
      }
      intent.putExtra(EXTRA_SHAPE_TYPE, layoutId)
      startActivity(intent)
    }
  }

  companion object {
    private const val RECTANGLE = "Rectangle"
    private const val CIRCLE = "Circle"
    private const val CUSTOM = "Custom"
  }
}
