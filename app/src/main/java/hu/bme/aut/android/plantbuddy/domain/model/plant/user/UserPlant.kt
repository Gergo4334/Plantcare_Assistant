package hu.bme.aut.android.plantbuddy.domain.model.plant.user

import hu.bme.aut.android.plantbuddy.domain.model.plant.PlantDimensions
import hu.bme.aut.android.plantbuddy.domain.model.plant.WateringPeriod
import java.time.LocalDate

data class UserPlant(
    var id: String = "", // Firestore ID
    var name: String,
    var type: String?,
    var cycle: String,
    var wateringPeriod: WateringPeriod,
    var lastWateredDate: LocalDate,
    var sunlight: String?,
    var image: String?,
    var isFavourite: Boolean,
    var indoor: Boolean,
    var dimensions: PlantDimensions
)

val sampleUserPlant = UserPlant(
    id = "",
    name = "Default Plant Name",
    type = "Flowering",
    cycle = "Annual",
    wateringPeriod = WateringPeriod(value = "7", unit = "days"),
    lastWateredDate = LocalDate.now(),
    sunlight = "Full Sun",
    image = null,
    isFavourite = false,
    indoor = false,
    dimensions = PlantDimensions(type = "Height", minValue = 0, maxValue = 100, unit = "cm")
)