package hu.bme.aut.android.plantbuddy.feature

import android.net.Uri
import hu.bme.aut.android.plantbuddy.domain.interactor.api.ai.AimlApiServiceInteractor
import hu.bme.aut.android.plantbuddy.domain.interactor.api.kindwise.KindwisePlantIdServiceInteractor
import hu.bme.aut.android.plantbuddy.domain.interactor.firebase.storage.FirebaseStorageServiceInteractor
import hu.bme.aut.android.plantbuddy.domain.interactor.plant.PlantRepositoryInteractor
import hu.bme.aut.android.plantbuddy.domain.model.plant.api.ApiPlant
import hu.bme.aut.android.plantbuddy.domain.model.plant.filter.ApiPlantFilterButtonState
import hu.bme.aut.android.plantbuddy.feature.home.api_plants.list.ApiPlantEvent
import hu.bme.aut.android.plantbuddy.feature.home.api_plants.list.ApiPlantsScreenViewModel
import hu.bme.aut.android.plantbuddy.util.UiEvent
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ApiPlantsScreenViewModelTest {
    private lateinit var mockRepositoryInteractor: PlantRepositoryInteractor
    private lateinit var mockStorageInteractor: FirebaseStorageServiceInteractor
    private lateinit var mockKindwisePlantIdApi: KindwisePlantIdServiceInteractor
    private lateinit var mockAimlApi: AimlApiServiceInteractor
    private lateinit var viewModel: ApiPlantsScreenViewModel

    private var mockApiPlant = mockk<ApiPlant>()

    @Before
    fun setUp() {
        mockRepositoryInteractor = mockk()
        mockStorageInteractor = mockk()
        mockKindwisePlantIdApi = mockk()
        mockAimlApi = mockk()
        viewModel = ApiPlantsScreenViewModel(
            mockRepositoryInteractor,
            mockStorageInteractor,
            mockKindwisePlantIdApi,
            mockAimlApi
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test FetchPlants loads plants and triggers success event`() = runTest {
        val mockPlants = listOf(mockApiPlant)

        coEvery { mockRepositoryInteractor.getAllPlants(any(), any()) } returns mockPlants

        viewModel.onEvent(ApiPlantEvent.FetchPlants)
        advanceUntilIdle()
        assertEquals(mockPlants, viewModel.plantListState.value.plantList)

        val event = viewModel.uiEvent.first()
        assertTrue(event is UiEvent.Success)
    }

    @Test
    fun `test FetchNextPage loads more plants and appends them`() = runBlocking {
        val mockPlants = listOf(mockApiPlant)
        coEvery { mockRepositoryInteractor.getAllPlants(any(), any()) } returns mockPlants

        viewModel.onEvent(ApiPlantEvent.FetchNextPage)

        assertEquals(mockPlants, viewModel.plantListState.value.plantList)
        assertTrue(viewModel.plantListState.value.currentPage > 0)
    }

    @Test
    fun `test SearchTextChanged updates search text and triggers filter`() = runBlocking {
        val searchText = "new plant"

        viewModel.onEvent(ApiPlantEvent.SearchTextChanged(searchText))

        assertEquals(searchText, viewModel.plantListState.value.searchText)
    }

    @Test
    fun `test FilterButtonChanged updates filter button state`() {
        val newState = ApiPlantFilterButtonState.NAME

        viewModel.onEvent(ApiPlantEvent.FilterButtonChanged(newState))

        assertEquals(newState, viewModel.plantListState.value.filterButtonState)
    }

}