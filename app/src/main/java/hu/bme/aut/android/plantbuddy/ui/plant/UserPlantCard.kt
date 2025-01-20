package hu.bme.aut.android.plantbuddy.ui.plant

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import hu.bme.aut.android.plantbuddy.R
import hu.bme.aut.android.plantbuddy.domain.model.plant.PlantDimensions
import hu.bme.aut.android.plantbuddy.domain.model.plant.user.UserPlant
import hu.bme.aut.android.plantbuddy.domain.model.plant.WateringPeriod
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale
import hu.bme.aut.android.plantbuddy.R.string as StringResources

@Composable
fun UserPlantCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onFavouriteClick: (UserPlant) -> Unit,
    onWaterClick: () -> Unit,
    plant: UserPlant
) {
    var isFavourite by remember { mutableStateOf(plant.isFavourite) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
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
                model = plant.image,
                contentDescription = "Image of " + plant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(id = R.drawable.plant_placeholder),
            )
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(5.dp)
                    .background(Color(0x66000000), shape = RoundedCornerShape(8.dp))
            ) {
                Row(
                    modifier = Modifier.padding(start = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = plant.name,
                        fontSize = 20.sp,
                        color = Color.White,
                        modifier = Modifier.wrapContentWidth(align = Alignment.Start)
                    )
                    IconButton(
                        onClick = {
                            onFavouriteClick(plant)
                            isFavourite = !isFavourite
                        },
                    ) {
                        Icon(
                            imageVector = if (isFavourite) Icons.Default.Star else Icons.Outlined.StarOutline,
                            contentDescription = stringResource(id = StringResources.star_icon_content_description),
                            tint = Color.Yellow
                        )
                    }
                }
                Text(
                    text = plant.cycle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.padding(start = 5.dp),
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(5.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onWaterClick,
                    modifier = Modifier.background(Color(0x66000000), shape = RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.Blue
                    )
                }
                Column(
                    modifier = Modifier
                        .background(Color(0x66000000), shape = RoundedCornerShape(8.dp)),
                    horizontalAlignment = Alignment.End
                ){
                    Text(
                        text = plant.wateringPeriod.let {
                            "${it.value ?: "N/A"} ${it.unit}" },
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .align(Alignment.End)
                    )
                    Text(
                        text = plant.lastWateredDate.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun PlantCard_Preview() {
    UserPlantCard(
        modifier = Modifier
            .width(200.dp),
        onClick = { },
        onFavouriteClick = { },
        onWaterClick = { },
        plant = UserPlant(
            name = "ornamental onion",
            type = "bulb",
            cycle = "Perennial",
            wateringPeriod = WateringPeriod(
                value = "7-10",
                unit = "days"
            ),
            lastWateredDate = LocalDate.of(2024,6,4),
            sunlight = "full sun",
            image = null,
            isFavourite = false,
            indoor = true,
            dimensions = PlantDimensions(
                type = null,
                minValue = 3,
                maxValue = 4,
                unit = "feet"
            )
        )
    )
}