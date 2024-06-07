package com.anorisno.tracker.ui.layout

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.anorisno.tracker.model.DetectionUiState
import com.anorisno.tracker.tools.getCameraProvider
import com.anorisno.tracker.ui.ViewModel.PositionViewModel
import kotlinx.coroutines.delay
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.max
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
//        .drawWithContent {
//            drawContent()
//            val canvsaQuadrandSize = size / 2F
//            drawRect(
//                color = Color.Magenta,
//                size = canvsaQuadrandSize
//                        brush = Brush.radialGradient()
//            )
//        }
){
    val positionUiState = positionViewModel.uiState.collectAsState()
    Box(modifier = Modifier
//        .drawWithContent {
//            drawContent()
//            val canvsaQuadrandSize = size / 2F
//            drawRect(
//                color = Color.Magenta,
//                size = canvsaQuadrandSize
//                        brush = Brush.radialGradient()
//            )
//        }
    ) {
        CameraPreviewScreen(
            imageListener = imageListener,
            viewModel = positionViewModel,
            detection = positionUiState.value.detectionUiState,
//            preview = preview,
//            previewView = previewView
            modifier = Modifier
//                .fillMaxSize()
//                .drawWithContent {
//                    drawContent()
//                    val canvsaQuadrandSize = size / 2F
//                    drawRect(
//                        color = Color.Magenta,
//                        size = canvsaQuadrandSize
//                        brush = Brush.radialGradient()
//                    )
//                }
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
//    Canvas(
//        modifier = Modifier
//    ) {
//        val canvsaQuadrandSize = size / 2F
//        drawRect(
//            color = Color.Cyan,
//            size = canvsaQuadrandSize
//        )
//    }

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
    viewModel: PositionViewModel,
    detection: DetectionUiState,
//    preview: androidx.camera.core.Preview,
//    previewView: PreviewView
    modifier: Modifier

) {
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val previewView = remember {
        PreviewView(context)
    }
    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
//        val cameraProvider = setUpCamera(context = context)
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, viewModel.preview, viewModel.imageAnalyzer)
        viewModel.preview.setSurfaceProvider(previewView.surfaceProvider)
    }
    Box(
        modifier = Modifier
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier
                .fillMaxSize()
//                .drawWithContent {
//                    drawContent()
//
//                }
        )
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // todo may be static and serf from positionViewModel
            val scaleFactor = max(
                size.width * 1f / detection.imageWidth,
                size.height * 1f / detection.imageHeight
            )

            // todo this realization isn't correct. need to be recreated. but next time. Now i need some rest
            for (result in detection.result) {
                val canvsaQuadrandSize = size / 2F

                val boundingBox = result.boundingBox

                val top = boundingBox.top * scaleFactor
                val bottom = boundingBox.bottom * scaleFactor
                val left = boundingBox.left * scaleFactor
                val right = boundingBox.right * scaleFactor

                drawRect(
                    color = Color.Cyan,
                    topLeft = Offset(top, left),
                    size = Size(width = right - left, height = bottom - top)
                )
            }
//            drawRect(
//                color = Color.Magenta,
//                size = canvsaQuadrandSize
//                        brush = Brush.radialGradient()
//            )
        }

    }
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
