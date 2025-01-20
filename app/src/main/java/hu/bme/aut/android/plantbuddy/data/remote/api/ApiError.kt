package hu.bme.aut.android.plantbuddy.data.remote.api

sealed class ApiError: Exception() {
    data class NetworkError(override val message: String): ApiError()
    data class HttpError(val code: Int, override val message: String): ApiError()
    data class UnknownError(override val message: String): ApiError()
}