package hu.bme.aut.android.plantbuddy.data.remote.api.plant

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object PerenualRetrofitInstance {
    private const val BASE_URL = "https://perenual.com/api/"

    val api: PerenualApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(PerenualApi::class.java)
    }
}