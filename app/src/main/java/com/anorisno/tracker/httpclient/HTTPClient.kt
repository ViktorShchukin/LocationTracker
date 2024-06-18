package com.anorisno.tracker.httpclient

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.anorisno.tracker.httpclient.dto.AlarmNonRangeDto
import com.anorisno.tracker.httpclient.dto.FrameNonRangeDto
import com.anorisno.tracker.httpclient.dto.Vector3DDto
import com.anorisno.tracker.httpclient.mapper.AlarmMapper
import com.anorisno.tracker.httpclient.mapper.FrameMapper
import com.anorisno.tracker.httpclient.mapper.VectorMapper
import com.anorisno.tracker.model.CoordinatesUiState
import com.anorisno.tracker.tools.Installation
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.mapstruct.factory.Mappers
import org.tensorflow.lite.task.gms.vision.detector.Detection
import java.io.IOException

//val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
const val TAG = "HTTPClient"

class HTTPClient(
    val context: Context
) {

    // todo end logic to store base path in res and change it from settings
    private val BASE_PATH: String = "http://192.168.84.161:8080/alarm/non-range"
    private val client: OkHttpClient = OkHttpClient()
    private val MEDIA_TYPE_JSON: MediaType = "application/json".toMediaType()

    private val jackson = jacksonObjectMapper()

    private var phoneId: String = Installation.id(context)
    private val vectorMapper: VectorMapper = Mappers.getMapper(VectorMapper::class.java)
    private val alarmMapper: AlarmMapper = Mappers.getMapper(AlarmMapper::class.java)
    private val frameMapper: FrameMapper = Mappers.getMapper(FrameMapper::class.java)

    fun postDetection(
        detection: List<Detection>,
        position: CoordinatesUiState,
        timestamp: Long,
        frameIndex: Long
    ) {
        val vectorDto: Vector3DDto = vectorMapper.toDto(position)
        val alarms: List<AlarmNonRangeDto> = detection.map { alarmMapper.toDto(phoneId, it) }
        val frame: FrameNonRangeDto =
            frameMapper.toDto(frameIndex, phoneId, vectorDto, timestamp, alarms)
        val requestBody = jackson.writeValueAsString(frame)
        val request = Request.Builder()
            .url(BASE_PATH)
            .post(requestBody.toRequestBody(MEDIA_TYPE_JSON))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
//                Log.e(TAG, e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                // todo recreate logic | what I can do here??? logs is not really good idea
                Log.d(TAG, "request status: ${response.code}. response body: ${response.body?.string() ?: "body is empty"}")
            }
        })


    }
}