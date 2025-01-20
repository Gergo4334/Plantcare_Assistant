package hu.bme.aut.android.plantbuddy.data.repositories

import hu.bme.aut.android.plantbuddy.data.model.dto.ApiPlantDto
import hu.bme.aut.android.plantbuddy.data.model.dto.PlantDimensionsDto
import hu.bme.aut.android.plantbuddy.data.model.dto.PlantImageDto
import hu.bme.aut.android.plantbuddy.data.model.dto.PlantResponse
import hu.bme.aut.android.plantbuddy.data.model.dto.PlantWithDetailsDto
import hu.bme.aut.android.plantbuddy.data.model.dto.WateringBenchmarkDto
import hu.bme.aut.android.plantbuddy.data.remote.api.ApiError
import hu.bme.aut.android.plantbuddy.data.remote.api.plant.PerenualApi
import hu.bme.aut.android.plantbuddy.data.repositories.plant.PlantRepositoryImpl
import hu.bme.aut.android.plantbuddy.domain.model.plant.PlantDimensions
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import okio.IOException
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.doThrow
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class PlantRepositoryImplTest {
    @Mock
    lateinit var mockApi: PerenualApi
    lateinit var mockRepository: PlantRepositoryImpl
    val plantImageDtoMock = PlantImageDto("url", "url","url","url")
    val plantDimensionsDtoMock = PlantDimensionsDto("medium", 10, 30, "cm")
    val wateringBenchmarkDtoMock = WateringBenchmarkDto("value", "unit")

    @Before
    fun setUp() {
        mockRepository = PlantRepositoryImpl(mockApi)
    }

    @Test
    fun `getAllPlants returns success when API call is successful`() = runBlocking {
        val mockResponse = Response.success(
            PlantResponse(
                data = listOf(ApiPlantDto(1, "Rose", "Perennial", "Moderate", listOf("Full Sun"), plantImageDtoMock)),
                to = 10,
                perPage = 10,
                currentPage = 1,
                from = 1,
                lastPage = 2,
                total = 20,
            )
        )

        whenever(mockApi.getAllPlants(eq(1), eq(10), any())).thenReturn(mockResponse)
        val result = mockRepository.getAllPlants(1, 10)
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Rose", result.getOrNull()?.first()?.commonName)

    }

    @Test
    fun `getAllPlants returns failure on unknown error`() = runBlocking {
        whenever(mockApi.getAllPlants(eq(1), eq(10), any())).thenThrow(RuntimeException("Something went wrong"))

        val result = mockRepository.getAllPlants(1, 10)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is ApiError.UnknownError)
    }

    @Test
    fun `getPlantDetailsById returns success when API call is successful`() = runBlocking {
        val mockResponse = Response.success(
            PlantWithDetailsDto(
                id = 1,
                commonName = "Rose",
                cycle = "Perennial",
                watering = "Moderate",
                sunlight = listOf("Full Sun"),
                defaultImage = plantImageDtoMock,
                type = "Flowering",
                dimensions = plantDimensionsDtoMock,
                wateringGeneralBenchmark = wateringBenchmarkDtoMock,
                indoor = true,
                careLevel = "Moderate",
                flowers = true,
                floweringSeason = "Spring",
                growthRate = "Medium"
            )
        )

        whenever(mockApi.getPlantDetailById(eq(1), any())).thenReturn(mockResponse)

        val result = mockRepository.getPlantDetailsById(1)

        assertTrue(result.isSuccess)
        val (plantDto, plantDetailsDto) = result.getOrNull() ?: throw AssertionError("Result was null")
        assertEquals("Rose", plantDto.commonName)
        assertEquals("Flowering", plantDetailsDto.type)
    }

    @Test
    fun `getPlantDetailsById returns failure when response body is null`() = runBlocking {
        val mockResponse = Response.success<PlantWithDetailsDto>(null)

        whenever(mockApi.getPlantDetailById(eq(1), any())).thenReturn(mockResponse)

        val result = mockRepository.getPlantDetailsById(1)

        assertTrue(result.isFailure)
        val error = result.exceptionOrNull()
        assertTrue(error is ApiError.UnknownError)
        assertEquals("Response body is null", (error as ApiError.UnknownError).message)
    }
}