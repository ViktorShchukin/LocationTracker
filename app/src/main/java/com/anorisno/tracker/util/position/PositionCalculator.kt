package com.anorisno.tracker.util.position

import com.anorisno.tracker.model.AccelerationData
import com.anorisno.tracker.model.GyroscopeData
import com.anorisno.tracker.model.SensorData
import kotlin.math.cos
import kotlin.math.sin

class PositionCalculator {
//    val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    private var orientation: Array<Double> = Array(size = 3, init = {0.0} )
    private var distance: Array<Double> = Array(size = 3, init = {0.0} )
    private var lastVelocity: Array<Double> = Array(size = 3, init = {0.0} )

    private var lastAcceleration: AccelerationData? = null
    private var lastGyroscope: GyroscopeData? = null

    private val toSecond: Double = 1/1e9

    public fun setToZero(){
        orientation = Array(size = 3, init = {0.0} )
        distance = Array(size = 3, init = {0.0} )
        lastVelocity = Array(size = 3, init = {0.0} )

        lastAcceleration = null
        lastGyroscope = null

    }

    fun evaluate(data: SensorData){
        when (data){
            is AccelerationData -> lastAcceleration = if (lastAcceleration == null){
                rotate(data)
            }else{
                handleAcceleration(rotate(data))
                rotate(data)
            }
            is GyroscopeData -> lastGyroscope = if (lastGyroscope == null){
                data
            }else{
                handleRotation(data)
                data
            }
        }
    }

    private fun handleAcceleration(data: AccelerationData){
        val deltaVelocityX = integral(lastAcceleration!!.x, data.x, lastAcceleration!!.timestamp, data.timestamp)
        val deltaVelocityY = integral(lastAcceleration!!.y, data.y, lastAcceleration!!.timestamp, data.timestamp)
        val deltaVelocityZ = integral(lastAcceleration!!.z, data.z, lastAcceleration!!.timestamp, data.timestamp)

        val deltaDistanceX = integral(lastVelocity[0], lastVelocity[0] + deltaVelocityX, lastAcceleration!!.timestamp, data.timestamp)
        val deltaDistanceY = integral(lastVelocity[1], lastVelocity[1] + deltaVelocityY, lastAcceleration!!.timestamp, data.timestamp)
        val deltaDistanceZ = integral(lastVelocity[2], lastVelocity[2] + deltaVelocityZ, lastAcceleration!!.timestamp, data.timestamp)
        lastVelocity[0] += deltaVelocityX
        lastVelocity[1] += deltaVelocityY
        lastVelocity[2] += deltaVelocityZ
        distance[0] += deltaDistanceX
        distance[1] += deltaDistanceY
        distance[2] += deltaDistanceZ
    }

    private fun handleRotation(data: GyroscopeData){
        val deltaX = integral(lastGyroscope!!.x, data.x, lastGyroscope!!.timestamp, data.timestamp)
        val deltaY = integral(lastGyroscope!!.y, data.y, lastGyroscope!!.timestamp, data.timestamp)
        val deltaZ = integral(lastGyroscope!!.z, data.z, lastGyroscope!!.timestamp, data.timestamp)

        orientation[0] += deltaX
        orientation[1] += deltaY
        orientation[2] += deltaZ
    }

    private fun rotate(data: AccelerationData): AccelerationData {
        val vec = arrayOf(arrayOf(data.x, data.y, data.z))
        val res = multiplyMatrices(vec, getRotationMatrix())
        return AccelerationData(res[0][0], res[0][1], res[0][2], data.timestamp)
    }

    private fun getRotationMatrix(): Array<Array<Double>> {
        val rotX = arrayOf(
            arrayOf(1.0, 0.0, 0.0),
            arrayOf(0.0, cos(orientation[0]), sin(orientation[0])),
            arrayOf(0.0, -sin(orientation[0]), cos(orientation[0]))
        )

        val rotY = arrayOf(
            arrayOf(cos(orientation[1]), 0.0, -sin(orientation[1])),
            arrayOf(0.0, 1.0, 0.0),
            arrayOf(sin(orientation[1]), 0.0, cos(orientation[1]))
        )

        val rotZ = arrayOf(
            arrayOf(cos(orientation[2]), sin(orientation[2]), 0.0),
            arrayOf(-sin(orientation[2]), cos(orientation[2]), 0.0),
            arrayOf(0.0, 0.0, 1.0)
        )

        val rotXY = multiplyMatrices(rotX, rotY)
        return multiplyMatrices(rotXY, rotZ)

    }

    private fun multiplyMatrices(matrix1: Array<Array<Double>>, matrix2: Array<Array<Double>>): Array<Array<Double>> {
        val row1 = matrix1.size
        val col1 = matrix1[0].size
        val col2 = matrix2[0].size
        val product = Array(row1) { Array(col2) { 0.0 } }

        for (i in 0 until row1) {
            for (j in 0 until col2) {
                for (k in 0 until col1) {
                    product[i][j] += matrix1[i][k] * matrix2[k][j]
                }
            }
        }

        return product
    }


    private fun integral(
        dataBefore: Double,
        dataLast: Double,
        timestampBefore: Long,
        timestampLast: Long
    ): Double {
        return (dataLast + dataBefore) / 2 * (timestampLast - timestampBefore) * toSecond
    }
}