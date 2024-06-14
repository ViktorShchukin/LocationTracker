package com.anorisno.tracker.httpclient

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.anorisno.tracker.httpclient.dto.AlarmNonRangeDto
import com.anorisno.tracker.httpclient.dto.FrameNonRangeDto
import com.anorisno.tracker.httpclient.dto.Vector3DDto
import com.anorisno.tracker.httpclient.mapper.VectorMapper
import com.anorisno.tracker.model.CoordinatesUiState
import com.anorisno.tracker.tools.Installation
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.Dispatcher
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import org.mapstruct.factory.Mappers
import org.tensorflow.lite.task.gms.vision.detector.Detection
import java.sql.Timestamp

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class HTTPClient(
    val context: Context
) {

    // todo end logic to store base path in res and chage it from settings
    private val BASE_PATH: String = "http://"
    private val client: OkHttpClient = OkHttpClient()
    private val MEDIA_TYPE_JSON: MediaType = "application/json".toMediaType()

    private val jackson = jacksonObjectMapper()

    private var phoneId: String = Installation.id(context)
    private val vectorMapper: VectorMapper = Mappers.getMapper(VectorMapper::class.java)

    // todo end this
    fun postDetection(detection: List<Detection>, position: CoordinatesUiState, timestamp: Long) {
        val vectorDto: Vector3DDto = vectorMapper.toDto(position)
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