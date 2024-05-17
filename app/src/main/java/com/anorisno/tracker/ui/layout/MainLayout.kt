package com.anorisno.tracker.ui.layout

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.anorisno.tracker.ui.ViewModel.PositionViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun MainLayout(viewModel: PositionViewModel){

}

@Composable
internal fun SimpleLayout(
    positionViewModel: PositionViewModel,
    modifier: Modifier = Modifier
){
    val positionUiState = positionViewModel.uiState.collectAsState()
    Column(
//        modifier
//            .padding(10.dp)
    ) {
        var openCollectDataDialog by remember {
            mutableStateOf(false)
        }

        Text(text = "Position")
        Text(text = "Coordinate")
        Text(text = "x: ${positionUiState.value.coordinate.x}")
        Text(text = "y: ${positionUiState.value.coordinate.y}")
        Text(text = "z: ${positionUiState.value.coordinate.z}")
        Text(text = "Angle")
        Text(text = "x: ${positionUiState.value.angle.x}")
        Text(text = "y: ${positionUiState.value.angle.y}")
        Text(text = "z: ${positionUiState.value.angle.z}")
        Text(text = "timestamp = ${positionUiState.value.timestamp}")
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
                horizontalAlignment = Alignment.CenterHorizontally
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
                Spacer(modifier = Modifier
                    .height(40.dp))
                OutlinedButton(
                    onClick = { onStartDataCollection() },
                    ) {
                        Text(text = "Start data collection")
                }
                Button(
                    onClick = { onEndDataCollection() },
                ) {
                    Text(text = "End data collection")
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



@Preview(showBackground = true)
@Composable
fun SimpleLayoutPreview(){
    CollectCorrectionDataDialog(
        onDismissRequest = {},
        onEndDataCollection = {},
        onStartDataCollection = {}
    )
}
