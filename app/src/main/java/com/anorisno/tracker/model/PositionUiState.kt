package com.anorisno.tracker.model

data class PositionUiState(
    val coordinate: CoordinatesUiState = CoordinatesUiState(0.0,0.0,0.0),
    val angle: AngleUiState = AngleUiState(0.0,0.0,0.0),
    val timestamp: Long = System.currentTimeMillis()
)
