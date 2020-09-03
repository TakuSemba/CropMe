package com.takusemba.cropmesample

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.takusemba.cropmesample.CropActivity.Companion.CIRCLE
import com.takusemba.cropmesample.CropActivity.Companion.CUSTOM
import com.takusemba.cropmesample.CropActivity.Companion.EXTRA_SHAPE_TYPE
import com.takusemba.cropmesample.CropActivity.Companion.RECTANGLE

class ChooserActivity : AppCompatActivity(R.layout.activity_chooser) {

  private val samples: Array<String> = arrayOf(RECTANGLE, CIRCLE, CUSTOM)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val listView = findViewById<ListView>(R.id.sample_list)
    val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, samples)
    listView.adapter = adapter
    listView.setOnItemClickListener { _, _, position, _ ->
      val intent = Intent(this, CropActivity::class.java)
      intent.putExtra(EXTRA_SHAPE_TYPE, samples[position])
      startActivity(intent)
    }
  }
}
