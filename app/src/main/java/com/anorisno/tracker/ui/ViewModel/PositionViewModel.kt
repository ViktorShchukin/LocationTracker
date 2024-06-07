package com.anorisno.tracker.ui.ViewModel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anorisno.tracker.ImageListener
import com.anorisno.tracker.ObjectDetectorHelper
import com.anorisno.tracker.model.AngleUiState
import com.anorisno.tracker.model.PositionUiState
import com.anorisno.tracker.util.position.PositionCalculatorExecutor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.anorisno.tracker.model.CoordinatesUiState
import com.anorisno.tracker.model.DetectionUiState
import org.tensorflow.lite.task.gms.vision.detector.Detection
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

const val TAG = "PositionViewModel"

class PositionViewModel constructor(
    private val positionCalculatorExecutor: PositionCalculatorExecutor,
    @field:SuppressLint("StaticFieldLeak") private val context: Context
): ViewModel(), ObjectDetectorHelper.DetectorListener, ImageListener {

    private val _uiState = MutableStateFlow(PositionUiState())
    val uiState: StateFlow<PositionUiState> = _uiState.asStateFlow()

    private lateinit var objectDetectorHelper: ObjectDetectorHelper
    // todo maybe should use coroutine scope for this???
    val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    override lateinit var bitmapBuffer: Bitmap
    val preview = Preview.Builder().build()
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
                    if (!isInitialized()) {
                        // The image rotation and RGB image buffer are initialized only once
                        // the analyzer has started running
                        bitmapBuffer = Bitmap.createBitmap(
                            image.width,
                            image.height,
                            Bitmap.Config.ARGB_8888
                        )
                    }

                    detectObjects(image)
                }
            }

    init {
        // todo where i should innit this object??
        objectDetectorHelper = ObjectDetectorHelper(
            context = context,
            objectDetectorListener = this
        )
        viewModelScope.launch {
            positionCalculatorExecutor.flow
                .collect { value ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            coordinate = CoordinatesUiState(value[0][0],value[0][1],value[0][2]),
                            angle = AngleUiState(value[1][0],value[1][1],value[1][2]),
                            timestamp = System.currentTimeMillis()
                        )
                    }
                }
        }
    }
    public fun test(){
        viewModelScope.launch {  }
    }

    override fun isInitialized(): Boolean {
        return ::bitmapBuffer.isInitialized
    }

    override fun detectObjects(image: ImageProxy) {
        // Copy out RGB bits to the shared bitmap buffer
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

        val imageRotation = image.imageInfo.rotationDegrees
        // Pass Bitmap and rotation to the object detector helper for processing and detection
        objectDetectorHelper.detect(bitmapBuffer, imageRotation)
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
        inferenceTime: Long,
        imageHeight: Int,
        imageWidth: Int
    ) {
        when(results) {
            null -> return
            else ->
                _uiState.update { currentState ->
                    currentState.copy(
                        detectionUiState = DetectionUiState(
                            result = results,
                            inferenceTime = inferenceTime,
                            imageHeight = imageHeight,
                            imageWidth = imageWidth
                        )
                    )
                }
        }

//        Log.d(TAG, "imageHeight: $imageHeight")
//        Log.d(TAG, "imageWidth: $imageWidth")
//        when {
//            results == null -> Log.d(TAG, "No results")
//            results.isEmpty() -> Log.d(TAG, "Empty result")
//            else ->
//                for (result in results) {
//                    Log.d(TAG, "result: " + result.categories[0].label)
//                }
//        }
    }

    fun setCalculatorToZero() {
        positionCalculatorExecutor.setToZero()
//        TODO("Not yet implemented")
    }

    fun startCorrectionCollect() {
        positionCalculatorExecutor.startCorrectionCollect()
    }

    fun endCorrectionCollect() {
        positionCalculatorExecutor.endCorrectionCollect()
    }
}