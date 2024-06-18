package com.anorisno.tracker.model.sensor

import java.sql.Timestamp

interface SensorData{
     abstract val x: Double
     abstract val y: Double
     abstract val z: Double
     abstract val timestamp: Long
}
