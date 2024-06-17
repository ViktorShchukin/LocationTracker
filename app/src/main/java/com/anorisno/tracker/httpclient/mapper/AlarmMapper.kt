package com.anorisno.tracker.httpclient.mapper

import com.anorisno.tracker.config.AppMapperConfig
import com.anorisno.tracker.httpclient.dto.AlarmNonRangeDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.tensorflow.lite.task.gms.vision.detector.Detection

@Mapper(config = AppMapperConfig::class)
interface AlarmMapper {

    @Mapping(target = "id", source = "cameraId")
    @Mapping(target = "azimuth", expression = "java(detection.getBoundingBox().centerX())")
    @Mapping(target = "elevation", expression = "java(detection.getBoundingBox().centerY())")
    @Mapping(target = "width", expression = "java(detection.getBoundingBox().width())")
    @Mapping(target = "height", expression = "java(detection.getBoundingBox().height())")
    @Mapping(target = "type", expression = "java(detection.getCategories().get(0).getLabel())")
    fun toDto(cameraId: String, detection: Detection): AlarmNonRangeDto
}