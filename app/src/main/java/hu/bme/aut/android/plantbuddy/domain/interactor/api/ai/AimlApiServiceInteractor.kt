package hu.bme.aut.android.plantbuddy.domain.interactor.api.ai

interface AimlApiServiceInteractor {
    suspend fun generateAiText(model: String, systemPrompt: String, userPrompt: String, maxtoken: Int): Result<String>
}