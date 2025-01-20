package hu.bme.aut.android.plantbuddy.domain.model.google

data class GoogleMapsLocation(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)
