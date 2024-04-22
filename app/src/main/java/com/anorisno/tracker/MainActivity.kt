package com.anorisno.tracker

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anorisno.tracker.model.SensorData
import com.anorisno.tracker.ui.theme.LocationTrackerTheme
import com.anorisno.tracker.util.position.PositionCalculator
import com.anorisno.tracker.util.position.PositionCalculatorExecutor
import com.anorisno.tracker.util.position.SensorListener
import java.util.concurrent.LinkedBlockingQueue

class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var sensorAccelerometer: Sensor
    private lateinit var sensorGyroscope: Sensor

    private val positionCalculator: PositionCalculator = PositionCalculator()
    private val positionExecutor: PositionCalculatorExecutor = PositionCalculatorExecutor(positionCalculator)
    private val sensorListener: SensorListener = SensorListener(positionExecutor)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.drawPage()

        positionExecutor.runForever()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)!!
        sensorGyroscope =sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!!
        sensorManager.registerListener(sensorListener, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(sensorListener, sensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL)
    }


    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorListener)

    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(sensorListener, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(sensorListener, sensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.positionExecutor.stop()
    }

    private fun drawPage() {
        setContent {
            LocationTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LocationTrackerTheme {
        Greeting("Android")
    }
}