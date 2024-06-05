package com.anorisno.tracker

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy

interface ImageListener {
    // todo should it be ? or lateintit
    var bitmapBuffer: Bitmap
    fun detectObjects(image: ImageProxy)

    fun isInitialized(): Boolean
}