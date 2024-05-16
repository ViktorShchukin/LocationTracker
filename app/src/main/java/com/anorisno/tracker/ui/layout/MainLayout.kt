package com.anorisno.tracker.ui.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.viewModelFactory
import com.anorisno.tracker.ui.ViewModel.PositionViewModel

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

    }
}



@Preview(showBackground = true)
@Composable
fun SimpleLayoutPreview(){
//    SimpleLayout(
//        positionViewModel =
//    )
}
