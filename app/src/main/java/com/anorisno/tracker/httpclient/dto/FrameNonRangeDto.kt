package com.anorisno.tracker.httpclient.dto

import java.util.UUID

data class FrameNonRangeDto(
    val index: Long,
    val camera: UUID, // camera id | same as device identifier
    val point: Vector3DDto, // should be taken at the sane time as detections
    val time: Long, // timestamp of detections, not current
    val alarms: List<AlarmNonRangeDto>
)
