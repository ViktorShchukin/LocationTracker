package com.anorisno.tracker.httpclient.mapper

import com.anorisno.tracker.config.AppMapperConfig
import com.anorisno.tracker.httpclient.dto.AlarmNonRangeDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.tensorflow.lite.task.gms.vision.detector.Detection

@Mapper(config = AppMapperConfig::class)
interface AlarmMapper {

    // this scale was got from python script. see appDataTool/pythonScripts/pixelScale/pixel_size_rad.py
    // todo reformat it as normal constant
    companion object pixelSize{
        val value = 0.0018262619153803297
    }

    @Mapping(target = "id", source = "cameraId")
    @Mapping(target = "azimuth", expression = "java(detection.getBoundingBox().centerX() * 0.0018262619153803297)")
    @Mapping(target = "elevation", expression = "java(detection.getBoundingBox().centerY() * 0.0018262619153803297)")
    @Mapping(target = "width", expression = "java(detection.getBoundingBox().width() * 0.0018262619153803297)")
    @Mapping(target = "height", expression = "java(detection.getBoundingBox().height() * 0.0018262619153803297)")
    @Mapping(target = "type", expression = "java(detection.getCategories().get(0).getLabel())")
    fun toDto(cameraId: String, detection: Detection): AlarmNonRangeDto
}