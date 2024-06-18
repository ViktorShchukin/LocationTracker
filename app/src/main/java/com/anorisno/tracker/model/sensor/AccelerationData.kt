package com.anorisno.tracker.model.sensor

import com.anorisno.tracker.model.sensor.SensorData

data class AccelerationData(
    override val x: Double,
    override val y: Double,
    override val z: Double,
    override val timestamp: Long

): SensorData {}