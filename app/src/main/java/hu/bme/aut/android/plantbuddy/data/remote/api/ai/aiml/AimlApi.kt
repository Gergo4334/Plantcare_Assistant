package hu.bme.aut.android.plantbuddy.data.remote.api.ai.aiml

import com.squareup.moshi.Json
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AimlApi {
    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Body requestBody: ChatCompletionRequest
    ): Response<ChatCompletionResponse>
}

data class ChatCompletionRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double,
    @field:Json(name = "max_tokens") val maxTokens: Int
)

data class Message(
    val role: String,
    val content: String
)

data class ChatCompletionResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)