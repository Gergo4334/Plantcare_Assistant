package hu.bme.aut.android.plantbuddy.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hu.bme.aut.android.plantbuddy.data.service.api.aiml.AimlApiService
import hu.bme.aut.android.plantbuddy.data.service.api.aiml.AimlApiServiceImpl
import hu.bme.aut.android.plantbuddy.data.service.api.kindwise.KindwisePlantIdApiService
import hu.bme.aut.android.plantbuddy.data.service.api.kindwise.KindwisePlantIdApiServiceImpl
import hu.bme.aut.android.plantbuddy.data.service.auth.AuthService
import hu.bme.aut.android.plantbuddy.data.service.auth.FirebaseAuthService
import hu.bme.aut.android.plantbuddy.data.service.firebase.storage.FirebaseStorageService
import hu.bme.aut.android.plantbuddy.data.service.firebase.storage.FirebaseStorageServiceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    @Singleton
    abstract fun bindAuthService(
        authService: FirebaseAuthService
    ): AuthService

    @Binds
    @Singleton
    abstract fun bindFirebaseStorageService(
        storageService: FirebaseStorageServiceImpl
    ): FirebaseStorageService

    @Binds
    @Singleton
    abstract fun bindAimlApiService(
        apiServiceImpl: AimlApiServiceImpl
    ): AimlApiService

    @Binds
    @Singleton
    abstract fun bindKindwisePlantIdApiService(
        apiServiceImpl: KindwisePlantIdApiServiceImpl
    ): KindwisePlantIdApiService
}