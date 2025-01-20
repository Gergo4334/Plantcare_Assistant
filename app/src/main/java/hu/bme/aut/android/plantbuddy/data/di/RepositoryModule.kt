package hu.bme.aut.android.plantbuddy.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hu.bme.aut.android.plantbuddy.data.repositories.firebase.FirestoreRepository
import hu.bme.aut.android.plantbuddy.data.repositories.firebase.FirestoreRepositoryImpl
import hu.bme.aut.android.plantbuddy.data.repositories.plant.PlantRepository
import hu.bme.aut.android.plantbuddy.data.repositories.plant.PlantRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPlantRepository(
        repository: PlantRepositoryImpl
    ): PlantRepository

    @Binds
    @Singleton
    abstract fun bindFirestoreRepository(
        repository: FirestoreRepositoryImpl
    ): FirestoreRepository
}