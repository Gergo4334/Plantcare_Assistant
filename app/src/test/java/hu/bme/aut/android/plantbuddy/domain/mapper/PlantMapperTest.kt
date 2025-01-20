package hu.bme.aut.android.plantbuddy.domain.mapper

import hu.bme.aut.android.plantbuddy.data.model.dto.ApiPlantDetailsDto
import hu.bme.aut.android.plantbuddy.data.model.dto.ApiPlantDto
import hu.bme.aut.android.plantbuddy.data.model.dto.PlantDimensionsDto
import hu.bme.aut.android.plantbuddy.data.model.dto.PlantImageDto
import hu.bme.aut.android.plantbuddy.data.model.dto.WateringBenchmarkDto
import hu.bme.aut.android.plantbuddy.domain.model.mapper.PlantMapper
import hu.bme.aut.android.plantbuddy.domain.model.plant.PlantDimensions
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import org.junit.Test

class PlantMapperTest {
    @Test
    fun `test mapToApiPlant should correctly map ApiPlantDto to ApiPlant`() {
        val dto = ApiPlantDto(
            id = 1,
            commonName = "Rose",
            cycle = "annual",
            watering = "daily",
            sunlight = listOf("full sun"),
            defaultImage = PlantImageDto("regular", "medium", "small", "thumbnail")
        )
        val detailsDto = ApiPlantDetailsDto(
            type = "flower",
            dimensions = PlantDimensionsDto("height", 1, 10, "cm"),
            wateringGeneralBenchmark = WateringBenchmarkDto("value", "unit"),
            indoor = true,
            careLevel = "easy",
            flowers = true,
            floweringSeason = "summer",
            growthRate = "fast"
        )

        val apiPlant = PlantMapper.mapToApiPlant(dto, detailsDto)

        assertEquals(dto.id, apiPlant.id)
        assertEquals(dto.commonName, apiPlant.name)
        assertEquals(dto.cycle, apiPlant.cycle)
        assertEquals(dto.watering, apiPlant.watering)
        assertEquals(dto.sunlight, apiPlant.sunlight)
        assertNotNull(apiPlant.image)
        assertNotNull(apiPlant.details)
        assertEquals(detailsDto.type, apiPlant.details?.type)
        assertEquals(detailsDto.indoor, apiPlant.details?.indoor)
    }

    @Test
    fun `test mapToApiPlant should map without detailsDto if null`() {
        val dto = ApiPlantDto(
            id = 1,
            commonName = "Tulip",
            cycle = "perennial",
            watering = "weekly",
            sunlight = listOf("partial sun"),
            defaultImage = PlantImageDto("regular", "medium", "small", "thumbnail")
        )

        val apiPlant = PlantMapper.mapToApiPlant(dto)

        assertEquals(dto.id, apiPlant.id)
        assertEquals(dto.commonName, apiPlant.name)
        assertNull(apiPlant.details)
    }
}