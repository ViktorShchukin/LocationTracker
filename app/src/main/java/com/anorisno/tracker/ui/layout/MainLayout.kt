package com.anorisno.tracker.ui.layout

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.anorisno.tracker.ImageListener
import com.anorisno.tracker.R
import com.anorisno.tracker.tools.getCameraProvider
import com.anorisno.tracker.ui.ViewModel.PositionViewModel
import kotlinx.coroutines.delay
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration.Companion.seconds

@Composable
fun MainLayout(viewModel: PositionViewModel){

}

@Composable
internal fun SimpleLayout(
    imageListener: ImageListener,
//    preview: androidx.camera.core.Preview,
//    previewView: PreviewView,
    positionViewModel: PositionViewModel,
    modifier: Modifier = Modifier
        .padding(8.dp)
){
    val positionUiState = positionViewModel.uiState.collectAsState()
    Box(modifier = modifier) {
        CameraPreviewScreen(
            imageListener = imageListener,
//            preview = preview,
//            previewView = previewView
        )
        Column(
            modifier = modifier
        ) {
            var openCollectDataDialog by remember {
                mutableStateOf(false)
            }

            Text(text = stringResource(R.string.position))
            Text(text = stringResource(id = R.string.coordinate))
            Text(text = stringResource(id = R.string.x, positionUiState.value.coordinate.x))
            Text(text = stringResource(id = R.string.y, positionUiState.value.coordinate.y))
            Text(text = stringResource(id = R.string.z, positionUiState.value.coordinate.z))
            Text(text = stringResource(id = R.string.angle))
            Text(text = stringResource(id = R.string.x, positionUiState.value.angle.x))
            Text(text = stringResource(id = R.string.y, positionUiState.value.angle.y))
            Text(text = stringResource(id = R.string.z, positionUiState.value.angle.z))
            Text(text = stringResource(id = R.string.currentTime, positionUiState.value.timestamp))
            Button(onClick = { positionViewModel.setCalculatorToZero() }) {
                Text(text = "set calculator to zero")
            }
            OutlinedButton(
                onClick = { openCollectDataDialog = true }) {
                Text(text = "Set correction")
            }

            when {
                openCollectDataDialog -> CollectCorrectionDataDialog(
                    onDismissRequest = { openCollectDataDialog = false},
                    onStartDataCollection = { positionViewModel.startCorrectionCollect() },
                    onEndDataCollection = { positionViewModel.endCorrectionCollect() })
            }

        }
    }

}

@Composable
fun CollectCorrectionDataDialog(
    onDismissRequest: () -> Unit,
    onStartDataCollection: () -> Unit,
    onEndDataCollection: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(260.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Collect correction data",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(4.dp)
                )
                Text(
                    text = "Keep calm and don't touch you phone during the data collection",
                    modifier = Modifier
                        .padding(8.dp)
                    )
                Timer()
//                Spacer(modifier = Modifier
//                    .height(40.dp))
                Column(
                    modifier = Modifier
                        .weight(1f, false)
                ) {
                    OutlinedButton(
                        onClick = { onStartDataCollection() },
                    ) {
                        Text(text = "Start data collection")
                    }
                    Button(
                        onClick = { onEndDataCollection() },
                        modifier = Modifier
                    ) {
                        Text(text = "End data collection")
                    }
                }

            }
        }
    }
}

@Composable
fun Timer() {
    var ticks by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while(true) {
            delay(1.seconds)
            ticks++
        }
    }
    Text(
        text = "timer: ${ticks.seconds}",
        modifier = Modifier
            .padding(8.dp))
}

@Composable
fun CameraPreviewScreen(
    imageListener: ImageListener,
//    preview: androidx.camera.core.Preview,
//    previewView: PreviewView
) {
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val preview = androidx.camera.core.Preview.Builder().build()
    val cameraExecutor = Executors.newSingleThreadExecutor()
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
                    if (!imageListener.isInitialized()) {
                        // The image rotation and RGB image buffer are initialized only once
                        // the analyzer has started running
                        imageListener.bitmapBuffer = Bitmap.createBitmap(
                            image.width,
                            image.height,
                            Bitmap.Config.ARGB_8888
                        )
                    }

                    imageListener.detectObjects(image)
                }
            }
    val previewView = remember {
        PreviewView(context)
    }
    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
//        val cameraProvider = setUpCamera(context = context)
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageAnalyzer)
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }
    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
}

suspend fun setUpCamera(context: Context): ProcessCameraProvider {
    val cameraProvider = context.getCameraProvider()
    cameraProvider.unbindAll()
    return cameraProvider
}


@Preview(showBackground = true)
@Composable
fun SimpleLayoutPreview(){
    CollectCorrectionDataDialog(
        onDismissRequest = {},
        onEndDataCollection = {},
        onStartDataCollection = {}
    )
}
