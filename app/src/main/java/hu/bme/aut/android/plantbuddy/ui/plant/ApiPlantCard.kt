package hu.bme.aut.android.plantbuddy.ui.plant

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.FilterDrama
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import hu.bme.aut.android.plantbuddy.domain.model.plant.api.ApiPlant
import hu.bme.aut.android.plantbuddy.domain.model.plant.api.ApiPlantDetails
import hu.bme.aut.android.plantbuddy.domain.model.plant.PlantDimensions
import hu.bme.aut.android.plantbuddy.domain.model.plant.PlantImage
import hu.bme.aut.android.plantbuddy.domain.model.plant.WateringPeriod

@Composable
fun ApiPlantCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    plant: ApiPlant
) {
    val (weatherIcon, iconTint) = mapWeatherIcon(plant.sunlight.last())
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background image
            // For preview
            /*
            Image(
                bitmap = ImageBitmap.imageResource(id = R.drawable.leaf),
                contentDescription = "Image of " + plant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            */
            // Actual image
            AsyncImage(
                model = plant.image?.thumbnail,
                contentDescription = "Image of " + plant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
                    .background(Color(0x66000000), shape = RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = plant.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                )
                Row(
                    modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Autorenew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Green
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = plant.cycle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .background(Color(0x66000000), shape = RoundedCornerShape(8.dp)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 1.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        modifier = Modifier
                            .size(14.dp),
                        tint = Color.Blue
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = plant.watering,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                    )
                }
                Row(
                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 1.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = weatherIcon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(14.dp),
                        tint = iconTint
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = plant.sunlight.last(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                    )
                }
            }
        }
    }
}

fun mapWeatherIcon(sunlight: String): Pair<ImageVector, Color> {
    return when (sunlight.lowercase()) {
        "full sun" -> Icons.Outlined.WbSunny to Color.Yellow
        "part shade" -> Icons.Filled.FilterDrama to Color.Gray
        "filtered shade" -> Icons.Filled.WbCloudy to Color(0xFF90CAF9)
        "part sun" -> Icons.Filled.BrightnessMedium to Color(0xFFFFC107)
        "part sun/part shade" -> Icons.Filled.WbTwilight to Color(0xFF7E57C2)
        else -> Icons.AutoMirrored.Filled.HelpOutline to Color.White
    }
}

@Composable
@Preview
fun ApiPlantCard_Preview() {
    ApiPlantCard(
        modifier = Modifier.width(150.dp),
        onClick = { },
        plant = ApiPlant(
            id = 664,
            name = "ornamental onion",
            cycle = "Perennial",
            watering = "Average",
            sunlight = listOf("full sun", "part sun/part shade"),
            image = PlantImage(
                regularUrl = "https://perenual.com/storage/species_image/664_allium_ambassador/regular/52265692295_ef78282782_b.jpg",
                mediumUrl = "https://perenual.com/storage/species_image/664_allium_ambassador/medium/52265692295_ef78282782_b.jpg",
                smallUrl = "https://perenual.com/storage/species_image/664_allium_ambassador/small/52265692295_ef78282782_b.jpg",
                thumbnail = "https://perenual.com/storage/species_image/664_allium_ambassador/thumbnail/52265692295_ef78282782_b.jpg"
            ),
            details = ApiPlantDetails(
                type = "Bulb",
                dimensions = PlantDimensions(
                    type = null,
                    minValue = 3,
                    maxValue = 4,
                    unit = "feet"
                ),
                wateringPeriod = WateringPeriod(
                    value = "7-10",
                    unit = "days"
                ),
                indoor = false,
                careLevel = "Medium",
                flowers = true,
                floweringSeason = "Spring",
                growthRate = "High"
            )
        )
    )
}