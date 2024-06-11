package com.anorisno.tracker.httpclient

import com.anorisno.tracker.httpclient.dto.AlarmNonRangeDto
import com.anorisno.tracker.httpclient.dto.FrameNonRangeDto
import com.anorisno.tracker.model.CoordinatesUiState
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import org.tensorflow.lite.task.gms.vision.detector.Detection
import java.sql.Timestamp

class HTTPClient {

    // todo end logic to store base path in res and chage it from settings
    private val BASE_PATH: String = "http://"
    private val client: OkHttpClient = OkHttpClient()
    private val MEDIA_TYPE_JSON: MediaType = "application/json".toMediaType()

    private val jackson = jacksonObjectMapper()

    // todo end this
    fun postDetection(detection: List<Detection>, position: CoordinatesUiState, timestamp: Long) {
        val alarms: AlarmNonRangeDto
        val frame: FrameNonRangeDto
//        val requestBody = jackson.writeValueAsString(frame)
        val request = Request.Builder()
            .url(BASE_PATH)
//            .post(requestBody.toRequestBody(MEDIA_TYPE_JSON))
            .build()

        // todo call enqueue or similar
        client.newCall(request)


    }
}