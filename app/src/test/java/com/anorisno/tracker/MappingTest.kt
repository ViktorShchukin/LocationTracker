package com.anorisno.tracker

import com.anorisno.tracker.httpclient.dto.Vector3DDto
import com.anorisno.tracker.httpclient.mapper.VectorMapper
import com.anorisno.tracker.model.CoordinatesUiState
import org.junit.Test
import org.junit.Assert.assertEquals
import org.mapstruct.factory.Mappers

class MappingTest {

    @Test
    fun positionToVector3DDto_isCorrect() {
        val position: CoordinatesUiState = CoordinatesUiState(
            x = 1.0,
            y = 1.0,
            z = 1.0
        )
        val vector: Vector3DDto = Vector3DDto(
            x = 1.0,
            y = 1.0,
            z = 1.0
        )
        val mapper: VectorMapper = Mappers.getMapper(VectorMapper::class.java)
        val mappedVector = mapper.toDto(position)
        assertEquals(vector, mappedVector)
    }
}