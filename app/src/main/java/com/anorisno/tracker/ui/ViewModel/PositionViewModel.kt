package com.anorisno.tracker.ui.ViewModel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anorisno.tracker.ImageListener
import com.anorisno.tracker.ObjectDetectorHelper
import com.anorisno.tracker.httpclient.HTTPClient
import com.anorisno.tracker.model.AngleUiState
import com.anorisno.tracker.ui.model.PositionUiState
import com.anorisno.tracker.util.position.PositionCalculatorExecutor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.anorisno.tracker.model.CoordinatesUiState
import com.anorisno.tracker.model.DetectionAlarm
import org.tensorflow.lite.task.gms.vision.detector.Detection
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

const val TAG = "PositionViewModel"

class PositionViewModel constructor(
    private val positionCalculatorExecutor: PositionCalculatorExecutor,
    @field:SuppressLint("StaticFieldLeak") private val context: Context
) : ViewModel(), ObjectDetectorHelper.DetectorListener, ImageListener {

    private val _uiState = MutableStateFlow(PositionUiState())
    val uiState: StateFlow<PositionUiState> = _uiState.asStateFlow()

    private val http: HTTPClient = HTTPClient(context)

    private val frameCounter: AtomicLong = AtomicLong(0)
    var frameTimestamp: Long = System.currentTimeMillis()
    private lateinit var objectDetectorHelper: ObjectDetectorHelper

    init {
        objectDetectorHelper = ObjectDetectorHelper(
            context = context,
            objectDetectorListener = this
        )
    }

    // todo maybe should use coroutine scope for this???
    // todo should I shutdown this executor and where i need to do this???
    val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    override lateinit var bitmapBuffer: Bitmap

    val preview = Preview.Builder().build()
    @SuppressLint("StaticFieldLeak")
    val previewView: PreviewView = PreviewView(context)
    val imageAnalyzer =
        ImageAnalysis.Builder()
//                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//                .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
            // The analyzer can then be assigned to the instance
            .also {
                it.setAnalyzer(cameraExecutor) { image ->
                    Log.v(TAG, "should call detect object ++++++++++")
                    val imageBitmap = image.toBitmap()
                    val imageRotation = image.imageInfo.rotationDegrees
                    val currentTimestamp = System.currentTimeMillis()
                    val timestampDiff = currentTimestamp - frameTimestamp
                    Log.v(TAG, "should call detect object ==============")
                    if (timestampDiff >= 40L) {
                        Log.v(TAG, "should call detect object")
                        detectObjects(imageBitmap, imageRotation, uiState.value.coordinate, uiState.value.timestamp)
                        frameTimestamp = currentTimestamp
                    }
                    image.close()
                }
            }

    init {
        viewModelScope.launch {
            positionCalculatorExecutor.flow
                .collect { value ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            coordinate = CoordinatesUiState(value[0][0], value[0][1], value[0][2]),
                            angle = AngleUiState(value[1][0], value[1][1], value[1][2]),
                            timestamp = System.currentTimeMillis()
                        )
                    }
                }
        }
    }

    public fun test() {
        viewModelScope.launch { }
    }

    override fun isInitialized(): Boolean {
        return ::bitmapBuffer.isInitialized
    }

    override fun detectObjects(image: Bitmap, imageRotation: Int, position: CoordinatesUiState, timestamp: Long) {
        // Copy out RGB bits to the shared bitmap buffer
//        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

//        val imageRotation = image.imageInfo.rotationDegrees
        // Pass Bitmap and rotation to the object detector helper for processing and detection
        objectDetectorHelper.detect(image, imageRotation, position, timestamp)
    }

    override fun onInitialized() {
        objectDetectorHelper.setupObjectDetector()
//        cameraExecutor = Executors.newSingleThreadExecutor()
//        setUpCamera()
    }

    override fun onError(error: String) {
        Log.e(TAG, "error in object detector helper initialization error: $error")
    }

    override fun onResults(
        results: MutableList<Detection>?,
        position: CoordinatesUiState,
        timestamp: Long,
        inferenceTime: Long,
        imageHeight: Int,
        imageWidth: Int
    ) {
        // todo recreate logic inside this function. Now it silly. no need to check
        when (results) {
            null -> return
            else -> {
                val counterValue = frameCounter.addAndGet(1)
                if (counterValue == Long.MAX_VALUE) {
                    frameCounter.set(0)
                }
                http.postDetection(results.toList(), position, timestamp, counterValue)
                _uiState.update { currentState ->
                    currentState.copy(
                        detectionUiState = DetectionAlarm(
                            result = results,
                            inferenceTime = inferenceTime,
                            imageHeight = imageHeight,
                            imageWidth = imageWidth
                        )
                    )
                }
            }
        }
    }

    fun setCalculatorToZero() {
        positionCalculatorExecutor.setToZero()
    }

    fun startCorrectionCollect() {
        positionCalculatorExecutor.startCorrectionCollect()
    }

    fun endCorrectionCollect() {
        positionCalculatorExecutor.endCorrectionCollect()
    }
}