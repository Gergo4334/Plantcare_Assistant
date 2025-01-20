package hu.bme.aut.android.plantbuddy.domain.interactor.api.ai

import hu.bme.aut.android.plantbuddy.data.service.api.aiml.AimlApiService
import javax.inject.Inject

class AimlApiServiceInteractorImpl @Inject constructor(
    private val aimlApiService: AimlApiService
) : AimlApiServiceInteractor {
    override suspend fun generateAiText(model: String, systemPrompt: String, userPrompt: String, maxtoken: Int): Result<String> {
        return aimlApiService.generateResponse(model, systemPrompt, userPrompt, maxtoken)
    }

}