package hu.bme.aut.android.plantbuddy.domain.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hu.bme.aut.android.plantbuddy.domain.usecases.IsEmailValidUseCase
import hu.bme.aut.android.plantbuddy.domain.usecases.PasswordsMatchUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideIsEmailValidUseCase() = IsEmailValidUseCase()

    @Provides
    @Singleton
    fun providePasswordsMatchUseCase() = PasswordsMatchUseCase()
}