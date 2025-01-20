package hu.bme.aut.android.plantbuddy.domain.model.plant

data class PlantDimensions(
    val type: String? = null,
    var minValue: Int = 0,
    var maxValue: Int = 0,
    var unit: String = ""
)