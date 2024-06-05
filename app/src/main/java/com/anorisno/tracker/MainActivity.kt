package com.anorisno.tracker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.anorisno.tracker.model.SensorData
import com.anorisno.tracker.tools.getCameraProvider
import com.anorisno.tracker.ui.ViewModel.PositionViewModel
import com.anorisno.tracker.ui.layout.SimpleLayout
import com.anorisno.tracker.ui.layout.setUpCamera
import com.anorisno.tracker.ui.theme.LocationTrackerTheme
import com.anorisno.tracker.util.position.PositionCalculator
import com.anorisno.tracker.util.position.PositionCalculatorExecutor
import com.anorisno.tracker.util.position.SensorListener
import org.tensorflow.lite.task.gms.vision.detector.Detection
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

const val TAG = "MainActivity"

class MainActivity : ComponentActivity(), ObjectDetectorHelper.DetectorListener, ImageListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var sensorAccelerometer: Sensor
    private lateinit var sensorGyroscope: Sensor

    private val positionCalculator: PositionCalculator = PositionCalculator()
    private val positionExecutor: PositionCalculatorExecutor = PositionCalculatorExecutor(positionCalculator)
    private val sensorListener: SensorListener = SensorListener(positionExecutor)
    private val positionViewModel: PositionViewModel = PositionViewModel(positionExecutor)

    private lateinit var objectDetectorHelper: ObjectDetectorHelper
    override lateinit var bitmapBuffer: Bitmap
    private var preview: androidx.camera.core.Preview? = null
//    private val previewView = PreviewView(this)
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    /** Blocking camera operations are performed using this executor */
    lateinit var cameraExecutor: ExecutorService

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
        sensorGyroscope =sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!!
        sensorManager.registerListener(sensorListener, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(sensorListener, sensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL)
        objectDetectorHelper = ObjectDetectorHelper(
            // todo maybe i should do another thing with context??
            context = this,
            objectDetectorListener = this
        )
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
                        imageListener = this,
//                        preview = preview!!,
//                        previewView = previewView,
                        positionViewModel = positionViewModel
                    )
                }
            }
        }
    }

    override fun isInitialized(): Boolean {
        return ::bitmapBuffer.isInitialized
    }


    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Implement camera related  code
            } else {
                // Camera permission denied
            }

        }

//    private fun setUpCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        cameraProviderFuture.addListener(
//            {
//                // CameraProvider
//                cameraProvider = cameraProviderFuture.get()
//
//                // Build and bind the camera use cases
//                bindCameraUseCases()
//            },
//            ContextCompat.getMainExecutor(this)
//        )
////        cameraProvider = getCameraProvider()
////            com.anorisno.tracker.ui.layout.setUpCamera(this)
//    }

//    private fun bindCameraUseCases() {
//
//        // CameraProvider
//        val cameraProvider =
//            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")
//
//        // CameraSelector - makes assumption that we're only using the back camera
//        val cameraSelector =
//            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
//
//        // Preview. Only using the 4:3 ratio because this is the closest to our models
//        preview =
//            androidx.camera.core.Preview.Builder()
////                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
////                .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
//                .build()
//
//        // ImageAnalysis. Using RGBA 8888 to match how our models work
//        imageAnalyzer =
//            ImageAnalysis.Builder()
////                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
////                .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
//                .build()
//                // The analyzer can then be assigned to the instance
//                .also {
//                    it.setAnalyzer(cameraExecutor) { image ->
//                        if (!::bitmapBuffer.isInitialized) {
//                            // The image rotation and RGB image buffer are initialized only once
//                            // the analyzer has started running
//                            bitmapBuffer = Bitmap.createBitmap(
//                                image.width,
//                                image.height,
//                                Bitmap.Config.ARGB_8888
//                            )
//                        }
//
//                        detectObjects(image)
//                    }
//                }
//
//        // Must unbind the use-cases before rebinding them
//        cameraProvider.unbindAll()
//        try {
//            // A variable number of use-cases can be passed here -
//            // camera provides access to CameraControl & CameraInfo
//            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
//
//            // Attach the viewfinder's surface provider to preview use case
////            preview?.setSurfaceProvider(fragmentCameraBinding.viewFinder.surfaceProvider)
////            preview?.setSurfaceProvider(previewView.surfaceProvider)
//        } catch (exc: Exception) {
//            Log.e(TAG, "Use case binding failed", exc)
//        }
//    }

    override fun detectObjects(image: ImageProxy) {
        // Copy out RGB bits to the shared bitmap buffer
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

        val imageRotation = image.imageInfo.rotationDegrees
        // Pass Bitmap and rotation to the object detector helper for processing and detection
        objectDetectorHelper.detect(bitmapBuffer, imageRotation)
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

    override fun onInitialized() {
//        TODO("Not yet implemented")
        objectDetectorHelper.setupObjectDetector()
        cameraExecutor = Executors.newSingleThreadExecutor()
//        setUpCamera()
    }

    override fun onError(error: String) {
//        TODO("Not yet implemented")
        Log.e(TAG, "error in object detector helper initialization error: $error")
    }

    override fun onResults(
        results: MutableList<Detection>?,
        inferenceTime: Long,
        imageHeight: Int,
        imageWidth: Int
    ) {
//        TODO("Not yet implemented")
        when (results) {
            null -> Log.d(TAG, "Empty result")
            else ->
                for (result in results) {
                    Log.d(TAG, "result: " + result.categories[0].label)
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