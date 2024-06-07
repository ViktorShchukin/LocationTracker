package com.anorisno.tracker.model

import org.tensorflow.lite.task.gms.vision.detector.Detection

data class PositionUiState(
    val coordinate: CoordinatesUiState = CoordinatesUiState(0.0,0.0,0.0),
    val angle: AngleUiState = AngleUiState(0.0,0.0,0.0),
    val timestamp: Long = System.currentTimeMillis(),
    val detectionUiState: DetectionUiState = DetectionUiState(mutableListOf(), 0,0,0)
)
