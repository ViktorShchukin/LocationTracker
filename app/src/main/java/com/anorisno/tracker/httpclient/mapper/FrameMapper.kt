package com.anorisno.tracker.httpclient.mapper

import com.anorisno.tracker.config.AppMapperConfig
import com.anorisno.tracker.httpclient.dto.AlarmNonRangeDto
import com.anorisno.tracker.httpclient.dto.FrameNonRangeDto
import com.anorisno.tracker.httpclient.dto.Vector3DDto
import org.mapstruct.Mapper

@Mapper(config = AppMapperConfig::class)
interface FrameMapper {


    fun toDto(
        index: Long,
        camera: String, // UUID in string that represent the device. The same as in AlarmNonRangeDto
        point: Vector3DDto,
        time: Long,
        alarms: List<AlarmNonRangeDto>
    ): FrameNonRangeDto
}