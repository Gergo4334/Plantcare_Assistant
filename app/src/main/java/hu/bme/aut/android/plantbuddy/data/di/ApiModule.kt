package hu.bme.aut.android.plantbuddy.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hu.bme.aut.android.plantbuddy.data.remote.api.ai.aiml.AimlApi
import hu.bme.aut.android.plantbuddy.data.remote.api.ai.aiml.AimlRetrofitInstance
import hu.bme.aut.android.plantbuddy.data.remote.api.ai.kindwise.KindwisePlantIdApi
import hu.bme.aut.android.plantbuddy.data.remote.api.ai.kindwise.KindwisePlantIdRetrofitInstance
import hu.bme.aut.android.plantbuddy.data.remote.api.plant.PerenualRetrofitInstance
import hu.bme.aut.android.plantbuddy.data.remote.api.plant.PerenualApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun providePerenualApi(): PerenualApi {
        return PerenualRetrofitInstance.api
    }

    @Provides
    @Singleton
    fun provideAimlApi(): AimlApi {
        return AimlRetrofitInstance.api
    }

    @Provides
    @Singleton
    fun provideKindwisePlantIdApi(): KindwisePlantIdApi {
        return KindwisePlantIdRetrofitInstance.api
    }
}
