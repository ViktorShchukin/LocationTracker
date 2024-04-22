package com.anorisno.tracker.model

data class GyroscopeData(
    override val x: Double,
    override val y: Double,
    override val z: Double,
    override val timestamp: Long
):SensorData {
}