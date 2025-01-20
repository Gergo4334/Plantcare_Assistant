package hu.bme.aut.android.plantbuddy.data.remote.api.ai.kindwise

import hu.bme.aut.android.plantbuddy.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object KindwisePlantIdRetrofitInstance {
    private const val BASE_URL = "https://api.plant.id/v3/"
    private const val API_KEY = BuildConfig.KINDWISE_API_KEY

    private val apiKeyInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Api-key", API_KEY)
            .build()
        chain.proceed(request)
    }

    val api: KindwisePlantIdApi by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(KindwisePlantIdApi::class.java)
    }
}