package hu.bme.aut.android.plantbuddy.data.service.api.aiml

interface AimlApiService {
    suspend fun generateResponse(model: String, systemPrompt: String, userPrompt: String, maxtoken: Int): Result<String>
}