package hu.bme.aut.android.plantbuddy.domain.interactor.api.kindwise

import hu.bme.aut.android.plantbuddy.data.remote.api.ai.kindwise.PlantIdentificationResponse
import java.time.LocalDate

interface KindwisePlantIdServiceInteractor {
    suspend fun identifyPlant(images: List<String>, dateTime: LocalDate, similarImages: Boolean): Result<PlantIdentificationResponse>
}