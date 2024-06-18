package com.anorisno.tracker.model

import org.tensorflow.lite.task.gms.vision.detector.Detection

data class DetectionAlarm(
    val result: MutableList<Detection>,
    val inferenceTime: Long,
    val imageHeight: Int,
    val imageWidth: Int
)
