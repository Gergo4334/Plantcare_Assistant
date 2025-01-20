package hu.bme.aut.android.plantbuddy.domain.model.mapper

import hu.bme.aut.android.plantbuddy.data.model.dto.ApiPlantDetailsDto
import hu.bme.aut.android.plantbuddy.data.model.dto.PlantDimensionsDto
import hu.bme.aut.android.plantbuddy.data.model.dto.ApiPlantDto
import hu.bme.aut.android.plantbuddy.data.model.dto.PlantImageDto
import hu.bme.aut.android.plantbuddy.data.model.dto.WateringBenchmarkDto
import hu.bme.aut.android.plantbuddy.domain.model.plant.api.ApiPlant
import hu.bme.aut.android.plantbuddy.domain.model.plant.api.ApiPlantDetails
import hu.bme.aut.android.plantbuddy.domain.model.plant.PlantDimensions
import hu.bme.aut.android.plantbuddy.domain.model.plant.PlantImage
import hu.bme.aut.android.plantbuddy.domain.model.plant.user.UserPlant
import hu.bme.aut.android.plantbuddy.domain.model.plant.WateringPeriod
import java.time.LocalDate

object PlantMapper {
    fun mapToApiPlant(dto: ApiPlantDto, detailsDto: ApiPlantDetailsDto? = null): ApiPlant {
        val plantImage = dto.defaultImage?.let { mapPlantImage(it) }
        val plantDetails = detailsDto?.let { mapToApiPlantDetails(detailsDto) }

        return ApiPlant(
            id = dto.id,
            name = dto.commonName,
            cycle = dto.cycle,
            watering = dto.watering,
            sunlight = dto.sunlight,
            image = plantImage,
            details = plantDetails
        )
    }

    private fun mapToApiPlantDetails(dto: ApiPlantDetailsDto): ApiPlantDetails {
        val plantDimensions = mapPlantDimensions(dto.dimensions)
        val plantWateringPeriod = mapWateringPeriod(dto.wateringGeneralBenchmark)
        return ApiPlantDetails(
            type = dto.type,
            dimensions = plantDimensions,
            wateringPeriod = plantWateringPeriod,
            indoor = dto.indoor,
            careLevel = dto.careLevel,
            flowers = dto.flowers,
            floweringSeason = dto.floweringSeason,
            growthRate = dto.growthRate
        )
    }

    private fun mapPlantImage(dto: PlantImageDto): PlantImage {
        return PlantImage(
            regularUrl = dto.regularUrl,
            smallUrl = dto.smallUrl,
            mediumUrl = dto.mediumUrl,
            thumbnail = dto.thumbnail
        )
    }

    private fun mapPlantDimensions(dto: PlantDimensionsDto): PlantDimensions {
        return PlantDimensions(
            type = dto.type,
            minValue = dto.minValue,
            maxValue = dto.maxValue,
            unit = dto.unit
        )
    }

    private fun mapWateringPeriod(dto: WateringBenchmarkDto): WateringPeriod {
        return WateringPeriod(
            value = dto.value,
            unit = dto.unit
        )
    }
}