package com.anorisno.tracker.httpclient.mapper;

import com.anorisno.tracker.config.AppMapperConfig;
import com.anorisno.tracker.httpclient.dto.AlarmNonRangeDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tensorflow.lite.task.gms.vision.detector.Detection;

@Mapper(config = AppMapperConfig.class)
public interface AlarmMapper {

    // this scale was got from python script. see appDataTool/pythonScripts/pixelScale/pixel_size_rad.py
    double PIXEL_SIZE = 0.0018262619153803297;

    @Mapping(target = "id", source = "cameraId")
    @Mapping(target = "azimuth", expression = "java(detection.getBoundingBox().centerX() * AlarmMapper.PIXEL_SIZE)")
    @Mapping(target = "elevation", expression = "java(detection.getBoundingBox().centerY() * AlarmMapper.PIXEL_SIZE)")
    @Mapping(target = "width", expression = "java(detection.getBoundingBox().width() * AlarmMapper.PIXEL_SIZE)")
    @Mapping(target = "height", expression = "java(detection.getBoundingBox().height() * AlarmMapper.PIXEL_SIZE)")
    @Mapping(target = "type", expression = "java(detection.getCategories().get(0).getLabel())")
    AlarmNonRangeDto toDto(String cameraId, Detection detection);
}
