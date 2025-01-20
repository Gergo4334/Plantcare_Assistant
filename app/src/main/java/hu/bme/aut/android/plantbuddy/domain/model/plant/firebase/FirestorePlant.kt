package hu.bme.aut.android.plantbuddy.domain.model.plant.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import hu.bme.aut.android.plantbuddy.domain.model.plant.PlantDimensions
import hu.bme.aut.android.plantbuddy.domain.model.plant.WateringPeriod
import hu.bme.aut.android.plantbuddy.domain.model.plant.user.UserPlant
import java.time.ZoneId
import java.util.Date

data class FirebasePlant(
    @DocumentId val id: String,
    val name: String = "",
    val type: String? = null,
    val cycle: String = "",
    val wateringPeriod: WateringPeriod = WateringPeriod(value = null, unit = ""),
    val lastWateredDate: Timestamp = Timestamp.now(),
    val sunlight: String? = null,
    val image: String? = null,
    val isFavourite: Boolean = false,
    val indoor: Boolean = false,
    val dimensions: PlantDimensions = PlantDimensions(type = null, minValue = 0, maxValue = 10, unit = "cm")
) {
    // Üres konstruktor Firestore-hoz
    constructor() : this("")
}

fun FirebasePlant.asUserPlant() = UserPlant(
    id = id,
    name = name,
    type = type,
    cycle = cycle,
    wateringPeriod = wateringPeriod,
    lastWateredDate = lastWateredDate.toDate().toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate(),
    sunlight = sunlight,
    image = image,
    isFavourite = isFavourite,
    indoor = indoor,
    dimensions = dimensions
)

fun UserPlant.asFirebasePlant() = FirebasePlant(
    id = id,
    name = name,
    type = type,
    cycle = cycle,
    wateringPeriod = wateringPeriod,
    lastWateredDate = Timestamp(Date.from(lastWateredDate.atStartOfDay(ZoneId.systemDefault()).toInstant())),
    sunlight = sunlight,
    image = image,
    isFavourite = isFavourite,
    indoor = indoor,
    dimensions = dimensions
)