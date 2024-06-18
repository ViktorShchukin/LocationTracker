package com.anorisno.tracker.util.position

import com.anorisno.tracker.model.sensor.SensorData
import java.util.ArrayDeque
import java.util.Optional
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PositionCalculatorExecutor(val calculator: PositionCalculator) {

//    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private val canRun: AtomicBoolean = AtomicBoolean(true)
//    private val correctionIsActive: Boolean = false

    private val sensorDataQueue = ArrayDeque<SensorData>()
    private val sensorDataChannel = Channel<SensorData>(10)

    private val defaultScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
    private val uiScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    val flow: Flow<Array<Array<Double>>> = flow {
//        defaultScope.launch {
            while (canRun.get()){
                val result = getData()
                    .map(calculator::evaluate)
                if (result.isPresent) emit(result.get())
            }
//        }
    }.flowOn(Dispatchers.Default)

    public fun saveData(data: SensorData){
        uiScope.launch {
            sensorDataChannel.send(data)
        }
    }

    private suspend fun getData(): Optional<SensorData> {
        var data: SensorData? = null
        data = sensorDataChannel.receive()
        return Optional.ofNullable(data)
    }

    public fun stop(){
        canRun.set(false)
    }

    fun setToZero() {
        calculator.setToZero()
    }

    fun startCorrectionCollect() {
        calculator.startCorrection()
    }

    fun endCorrectionCollect() {
        calculator.endCorrection()
    }

//    public fun runForever(){
//        defaultScope.launch {
//            while (canRun.get()){
//                val result = getData()
//                    .map(calculator::evaluate)
//            }
//        }
//    }
}