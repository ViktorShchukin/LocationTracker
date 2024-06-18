package com.anorisno.tracker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.anorisno.tracker.ui.ViewModel.PositionViewModel
import com.anorisno.tracker.ui.layout.SimpleLayout
import com.anorisno.tracker.ui.theme.LocationTrackerTheme
import com.anorisno.tracker.util.position.PositionCalculator
import com.anorisno.tracker.util.position.PositionCalculatorExecutor
import com.anorisno.tracker.util.position.SensorListener

const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var sensorAccelerometer: Sensor
    private lateinit var sensorGyroscope: Sensor

    private val positionCalculator: PositionCalculator = PositionCalculator()
    private val positionExecutor: PositionCalculatorExecutor =
        PositionCalculatorExecutor(positionCalculator)
    private val sensorListener: SensorListener = SensorListener(positionExecutor)
    private lateinit var positionViewModel: PositionViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) -> {
                // Camera permission already granted
                // Implement camera related code
            }

            else -> {
                cameraPermissionRequest.launch(Manifest.permission.CAMERA)
            }
        }

        Log.d(TAG, "after onCreate")
//        positionExecutor.runForever()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)!!
        sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!!
        sensorManager.registerListener(
            sensorListener,
            sensorAccelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            sensorListener,
            sensorGyroscope,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        positionViewModel =
            PositionViewModel(context = this, positionCalculatorExecutor = positionExecutor)
        setContent {
            val previewView = remember {
                PreviewView(this)
            }
            LocationTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SimpleLayout(
                        imageListener = positionViewModel,
//                        preview = preview!!,
//                        previewView = previewView,
                        positionViewModel = positionViewModel
                    )
                }
            }
        }
    }


    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Implement camera related  code
            } else {
                // Camera permission denied
            }

        }


    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorListener)

    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            sensorListener,
            sensorAccelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            sensorListener,
            sensorGyroscope,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        this.positionExecutor.stop()
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