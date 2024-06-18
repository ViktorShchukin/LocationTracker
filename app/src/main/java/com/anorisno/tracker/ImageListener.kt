package com.anorisno.tracker

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.anorisno.tracker.model.CoordinatesUiState

interface ImageListener {
    var bitmapBuffer: Bitmap
    fun detectObjects(image: ImageProxy, position: CoordinatesUiState, timestamp: Long)

    fun isInitialized(): Boolean
}