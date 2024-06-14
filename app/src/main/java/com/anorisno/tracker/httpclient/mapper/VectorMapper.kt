package com.anorisno.tracker.httpclient.mapper

import com.anorisno.tracker.config.AppMapperConfig
import com.anorisno.tracker.httpclient.dto.Vector3DDto
import com.anorisno.tracker.model.CoordinatesUiState
import org.mapstruct.Mapper

@Mapper(config = AppMapperConfig::class)
interface VectorMapper {

    fun toDto(position: CoordinatesUiState): Vector3DDto
}