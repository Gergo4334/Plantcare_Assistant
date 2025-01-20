package hu.bme.aut.android.plantbuddy.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hu.bme.aut.android.plantbuddy.domain.interactor.api.ai.AimlApiServiceInteractor
import hu.bme.aut.android.plantbuddy.domain.interactor.api.ai.AimlApiServiceInteractorImpl
import hu.bme.aut.android.plantbuddy.domain.interactor.api.kindwise.KindwisePlantIdServiceInteractor
import hu.bme.aut.android.plantbuddy.domain.interactor.api.kindwise.KindwisePlantIdServiceInteractorImpl
import hu.bme.aut.android.plantbuddy.domain.interactor.auth.AuthServiceInteractor
import hu.bme.aut.android.plantbuddy.domain.interactor.auth.AuthServiceInteractorImpl
import hu.bme.aut.android.plantbuddy.domain.interactor.firebase.storage.FirebaseStorageServiceInteractor
import hu.bme.aut.android.plantbuddy.domain.interactor.firebase.storage.FirebaseStorageServiceInteractorImpl
import hu.bme.aut.android.plantbuddy.domain.interactor.firebase.firestore.FirestoreRepositoryInteractor
import hu.bme.aut.android.plantbuddy.domain.interactor.firebase.firestore.FirestoreRepositoryInteractorImpl
import hu.bme.aut.android.plantbuddy.domain.interactor.plant.PlantRepositoryInteractor
import hu.bme.aut.android.plantbuddy.domain.interactor.plant.PlantRepositoryInteractorImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InteractorModule {
    @Binds
    @Singleton
    abstract fun bindAuthServiceInteractor(
        authServiceInteractorImpl: AuthServiceInteractorImpl
    ): AuthServiceInteractor
    
    @Binds
    @Singleton
    abstract fun bindPlantRepositoryInteractor(
        plantRepositoryInteractor: PlantRepositoryInteractorImpl
    ): PlantRepositoryInteractor

    @Binds
    @Singleton
    abstract fun bindFirestoreRepositoryInteractor(
        firestoreRepositoryInteractor: FirestoreRepositoryInteractorImpl
    ): FirestoreRepositoryInteractor

    @Binds
    @Singleton
    abstract fun bindFirebaseStorageServiceInteractor(
        firebaseStorageServiceInteractor: FirebaseStorageServiceInteractorImpl
    ): FirebaseStorageServiceInteractor

    @Binds
    @Singleton
    abstract fun bindAimlApiServiceInteractor(
        aimlApiServiceInteractor: AimlApiServiceInteractorImpl
    ): AimlApiServiceInteractor

    @Binds
    @Singleton
    abstract fun bindKindwisePlantIdServiceInteractor(
        kindwisePlantIdServiceInteractor: KindwisePlantIdServiceInteractorImpl
    ): KindwisePlantIdServiceInteractor
}