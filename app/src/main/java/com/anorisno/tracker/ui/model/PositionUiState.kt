package com.anorisno.tracker.ui.model

import com.anorisno.tracker.model.AngleUiState
import com.anorisno.tracker.model.CoordinatesUiState
import com.anorisno.tracker.model.DetectionAlarm

data class PositionUiState(
    val coordinate: CoordinatesUiState = CoordinatesUiState(0.0,0.0,0.0),
    val angle: AngleUiState = AngleUiState(0.0,0.0,0.0),
    val timestamp: Long = System.currentTimeMillis(),
    val detectionUiState: DetectionAlarm = DetectionAlarm(mutableListOf(), 0,0,0)
)
