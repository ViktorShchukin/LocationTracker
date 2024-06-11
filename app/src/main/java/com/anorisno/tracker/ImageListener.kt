package com.anorisno.tracker

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.anorisno.tracker.model.CoordinatesUiState
import com.anorisno.tracker.model.PositionUiState

interface ImageListener {
    // todo should it be ? or lateintit
    var bitmapBuffer: Bitmap
    fun detectObjects(image: ImageProxy, position: CoordinatesUiState, timestamp: Long)

    fun isInitialized(): Boolean
}