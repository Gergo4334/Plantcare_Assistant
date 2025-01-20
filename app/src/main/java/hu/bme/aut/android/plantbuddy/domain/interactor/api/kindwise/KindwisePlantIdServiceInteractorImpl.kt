package hu.bme.aut.android.plantbuddy.domain.interactor.api.kindwise

import hu.bme.aut.android.plantbuddy.data.remote.api.ai.kindwise.PlantIdentificationResponse
import hu.bme.aut.android.plantbuddy.data.service.api.kindwise.KindwisePlantIdApiService
import java.time.LocalDate
import javax.inject.Inject

class KindwisePlantIdServiceInteractorImpl @Inject constructor(
    private val kindwisePlantIdApiService: KindwisePlantIdApiService
): KindwisePlantIdServiceInteractor {
    override suspend fun identifyPlant(
        images: List<String>,
        dateTime: LocalDate,
        similarImages: Boolean
    ): Result<PlantIdentificationResponse> {
        return kindwisePlantIdApiService.identifyPlant(
            images,
            dateTime.toString(),
            similarImages
        )
    }
}