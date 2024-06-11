package com.anorisno.tracker.httpclient.dto

import java.util.UUID

data class AlarmNonRangeDto(
    val id: UUID, // camera id | same as device identifier
    val azimuth: Double, // radian
    val elevation: Double, // radian
    val width: Double, // radian
    val height: Double, // radian
    val type: String // type of detected object
)
