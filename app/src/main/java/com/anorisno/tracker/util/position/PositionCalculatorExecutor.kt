package com.anorisno.tracker.util.position

import java.util.concurrent.locks.Condition
import com.anorisno.tracker.model.SensorData
import java.util.ArrayDeque
import java.util.Optional
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class PositionCalculatorExecutor(val calculator: PositionCalculator) {

    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private val canRun: AtomicBoolean = AtomicBoolean(true)

    private val sensorDataQueue = ArrayDeque<SensorData>()

    private val lock: Lock = ReentrantLock()
    private val condition: Condition  = lock.newCondition()

    public fun saveData(data: SensorData){
        lock.lock()
        try {
            sensorDataQueue.add(data)
            condition.signal()
        } finally {
            lock.unlock()
        }

    }

    private fun getData(): Optional<SensorData> {
        lock.lock()
        var data: SensorData? = null
        try {
            data = sensorDataQueue.poll()
            if (data == null) {
               if( condition.await(100, TimeUnit.MILLISECONDS)){
                   data = sensorDataQueue.poll()
               }
            }
        } catch (_: InterruptedException){
            ;
        } finally {
            lock.unlock()
        }
        return Optional.ofNullable(data)
    }

    public fun stop(){
        canRun.set(false)
    }

    public fun runForever(){
        executorService.execute {
            while (canRun.get()) {
                this.getData()
                    .map(calculator::evaluate)
            }
        }
    }
}