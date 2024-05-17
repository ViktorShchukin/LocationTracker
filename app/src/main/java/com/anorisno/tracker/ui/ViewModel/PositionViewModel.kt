package com.anorisno.tracker.ui.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anorisno.tracker.model.AngleUiState
import com.anorisno.tracker.model.PositionUiState
import com.anorisno.tracker.util.position.PositionCalculatorExecutor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.anorisno.tracker.model.CoordinatesUiState

class PositionViewModel(
    private val positionCalculatorExecutor: PositionCalculatorExecutor
): ViewModel() {

    private val _uiState = MutableStateFlow(PositionUiState())
    val uiState: StateFlow<PositionUiState> = _uiState.asStateFlow()

    init {
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