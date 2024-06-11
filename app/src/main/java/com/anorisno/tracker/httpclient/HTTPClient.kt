package com.anorisno.tracker.httpclient

import com.anorisno.tracker.httpclient.dto.AlarmNonRangeDto
import com.anorisno.tracker.httpclient.dto.FrameNonRangeDto
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.tensorflow.lite.task.gms.vision.detector.Detection

class HTTPClient {

    // todo end logic to store base path in res and chage it from settings
    private val BASE_PATH: String = "http://"
    private val client: OkHttpClient = OkHttpClient()
    private val MEDIA_TYPE_JSON: MediaType = "application/json".toMediaType()

    private val mapper = jacksonObjectMapper()

    // todo end this
    fun postDetection(detection: List<Detection>) {
        val alarms: AlarmNonRangeDto
        val frame: FrameNonRangeDto
//        val requestBody = mapper.writeValueAsString(frame)
        val request = Request.Builder()
            .url(BASE_PATH)
//            .post(requestBody.toRequestBody(MEDIA_TYPE_JSON))
            .build()

        // todo call enqueue or similar
        client.newCall(request)


    }
}