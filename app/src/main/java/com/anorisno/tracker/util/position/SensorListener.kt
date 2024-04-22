package com.anorisno.tracker.util.position

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import com.anorisno.tracker.model.AccelerationData
import com.anorisno.tracker.model.GyroscopeData
import com.anorisno.tracker.model.SensorData

class SensorListener(private val positionExecutor: PositionCalculatorExecutor): SensorEventListener {

//    private val lock: Lock = ReentrantLock()

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val data: SensorData = AccelerationData(
                x = event.values[0].toDouble(),
                y = event.values[1].toDouble(),
                z = event.values[2].toDouble(),
                timestamp = event.timestamp
            )
            positionExecutor.saveData(data)
        }
        if (event.sensor.type == Sensor.TYPE_GYROSCOPE){
            val data: SensorData = GyroscopeData(
                x = event.values[0].toDouble(),
                y = event.values[1].toDouble(),
                z = event.values[2].toDouble(),
                timestamp = event.timestamp
            )
            positionExecutor.saveData(data)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }



}