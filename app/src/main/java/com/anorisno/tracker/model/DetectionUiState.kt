package com.anorisno.tracker.model

import org.tensorflow.lite.task.gms.vision.detector.Detection

data class DetectionUiState(
    val result: MutableList<Detection>,
    val inferenceTime: Long,
    val imageHeight: Int,
    val imageWidth: Int
)
