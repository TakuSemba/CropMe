package com.takusemba.cropmesample.models

/**
 * Created by takusemba on 2017/09/10.
 */

class Album(
        val bucketId: String,
        val name: String,
        val photos: List<Photo>
) {
    var isSelected: Boolean = false
}