package hu.bme.aut.android.plantbuddy.data.remote.api.ai.aiml

import hu.bme.aut.android.plantbuddy.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object AimlRetrofitInstance {
    private const val BASE_URL = "https://api.aimlapi.com/v1/"
    private const val API_KEY = BuildConfig.AIML_API_KEY

    val api: AimlApi by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor())
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AimlApi::class.java)
    }

    private class ApiKeyInterceptor: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val requestWithApiKey = originalRequest.newBuilder()
                .header("Authorization", "Bearer $API_KEY")
                .build()
            return chain.proceed(requestWithApiKey)
        }
    }
}