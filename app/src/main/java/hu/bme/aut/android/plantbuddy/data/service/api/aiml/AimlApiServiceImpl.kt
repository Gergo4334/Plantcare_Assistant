package hu.bme.aut.android.plantbuddy.data.service.api.aiml

import hu.bme.aut.android.plantbuddy.data.remote.api.ApiError
import hu.bme.aut.android.plantbuddy.data.remote.api.ai.aiml.AimlApi
import hu.bme.aut.android.plantbuddy.data.remote.api.ai.aiml.ChatCompletionRequest
import hu.bme.aut.android.plantbuddy.data.remote.api.ai.aiml.Message
import okio.IOException
import javax.inject.Inject

class AimlApiServiceImpl @Inject constructor(
    private val aimlApi: AimlApi
): AimlApiService {
    override suspend fun generateResponse(model: String, systemPrompt: String, userPrompt: String, maxtoken: Int): Result<String> {
        val requestBody = ChatCompletionRequest(
            model = model,
            messages = listOf(
                Message(role = "system", content = systemPrompt),
                Message(role = "user", content = userPrompt)
            ),
            temperature = 0.4,
            maxTokens = maxtoken
        )
        return try {
            val response = aimlApi.createChatCompletion(requestBody)
            if (response.isSuccessful) {
                response.body()?.choices?.firstOrNull()?.message?.content
                    ?.let {
                        Result.success(it)
                    }
                    ?: Result.failure(ApiError.UnknownError("No response from the AI model"))
            } else {
                Result.failure(ApiError.HttpError(response.code(), response.errorBody()?.string() ?: "Unknown HTTP error"))
            }
        } catch (e: IOException) {
            Result.failure(ApiError.NetworkError("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(ApiError.UnknownError("Unexpected error: ${e.message}"))
        }
    }
}