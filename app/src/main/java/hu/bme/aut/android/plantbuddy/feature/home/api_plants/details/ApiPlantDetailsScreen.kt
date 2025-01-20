package hu.bme.aut.android.plantbuddy.feature.home.api_plants.details

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FilterDrama
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.SignalCellularAlt1Bar
import androidx.compose.material.icons.filled.SignalCellularAlt2Bar
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import hu.bme.aut.android.plantbuddy.R
import hu.bme.aut.android.plantbuddy.domain.model.google.GoogleMapsLocation
import hu.bme.aut.android.plantbuddy.domain.model.plant.api.ApiPlant
import hu.bme.aut.android.plantbuddy.ui.common.ExpandableCard
import hu.bme.aut.android.plantbuddy.util.UiEvent
import kotlinx.coroutines.launch
import java.util.Locale
import hu.bme.aut.android.plantbuddy.R.string as StringResources

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiPlantDetailsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ApiPlantsDetailsViewModel = hiltViewModel()
) {
    val plantState by viewModel.plantState.collectAsStateWithLifecycle()
    val snackBarHostState = SnackbarHostState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(47.1625, 19.5033), 6f) // Hungary
    }

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(ApiPlantDetailsEvent.FetchDetails)
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Success -> { }
                is UiEvent.Failure -> {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = event.message.asString(context)
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = plantState.plant?.name + "' details", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF9CCC65)),
                modifier = Modifier.border(1.dp, Color.Black, RoundedCornerShape(5.dp)),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        if (plantState.isLoading) {
            CircularProgressIndicator()
        } else if (plantState.plant != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    AsyncImage(
                        model = plantState.plant?.image?.mediumUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        placeholder = painterResource(id = R.drawable.plant_placeholder),
                        error = painterResource(id = R.drawable.error_icon_4),
                        modifier = Modifier
                            .size(300.dp)
                            .border(5.dp, Color.Black, RoundedCornerShape(50.dp))
                            .clip(RoundedCornerShape(50.dp))
                    )
                }
                item {
                    Text(
                        text = plantState.plant?.name ?: stringResource(id = StringResources.plant_missing_data_indicator),
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Cycle",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Autorenew,
                                    contentDescription = null,
                                    tint = Color.Green)
                                Text(
                                    text = plantState.plant?.cycle ?: stringResource(id = StringResources.plant_missing_data_indicator),
                                    modifier = Modifier.padding(start = 3.dp)
                                )
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Sunlight",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val (weatherIcon, iconTint) = mapWeatherIcon(plantState.plant?.sunlight?.first() ?: "-")
                                Icon(
                                    imageVector = weatherIcon,
                                    contentDescription = null,
                                    tint = iconTint)
                                Text(
                                    text = plantState.plant?.sunlight?.joinToString(", ") ?: stringResource(id = StringResources.plant_missing_data_indicator),
                                    modifier = Modifier.padding(start = 3.dp)
                                )
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Watering",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = Color.Blue)
                                Text(text = plantState.plant?.watering ?: stringResource(id = StringResources.plant_missing_data_indicator),
                                    modifier = Modifier.padding(start = 3.dp)
                                )
                            }
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(15.dp))
                }
                item {
                    ExpandableCard(title = "Details") {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 5.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    modifier = Modifier.padding(horizontal = 5.dp),
                                    tint = Color(0xFF388E3C)
                                )
                                Text(
                                    text = "Growth rate:",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = plantState.plant?.details?.growthRate ?: stringResource(id = StringResources.plant_missing_data_indicator),
                                    modifier = Modifier.padding(start = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(20.dp))
                                Icon(
                                    imageVector = Icons.Default.Straighten,
                                    contentDescription = null,
                                    modifier = Modifier.padding(horizontal = 5.dp)
                                )
                                Text(text = "Dimensions:", fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${plantState.plant?.details?.dimensions?.minValue} - ${plantState.plant?.details?.dimensions?.maxValue} ${plantState.plant?.details?.dimensions?.unit}",
                                    modifier = Modifier.padding(start = 2.dp)
                                )
                            }
                            Row(
                                modifier = Modifier.padding(vertical = 15.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Opacity,
                                    contentDescription = null,
                                    modifier = Modifier.padding(horizontal = 5.dp),
                                    tint = Color(0xFF2196F3)
                                )
                                Text(text = "Watering period:", fontWeight = FontWeight.Bold)
                                Text(
                                    text ="${plantState.plant?.details?.wateringPeriod?.value} ${plantState.plant?.details?.wateringPeriod?.unit}",
                                    modifier = Modifier.padding(start = 2.dp)
                                )
                            }
                            Row(
                                modifier = Modifier.padding(vertical = 15.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Nature,
                                    contentDescription = null,
                                    tint = Color(0xFF388E3C)
                                )
                                Text(
                                    text = "Type:",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = plantState.plant?.details?.type ?: stringResource(id = StringResources.plant_missing_data_indicator),
                                    modifier = Modifier.padding(start = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(15.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (plantState.plant?.details?.indoor == true) Icons.Default.Home else Icons.Default.Park,
                                        contentDescription = null
                                    )
                                    Text(
                                        text = if (plantState.plant?.details?.indoor == true) "Indoor" else "Outdoor",
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(start = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(15.dp))
                                Text(text = "Care level:", fontWeight = FontWeight.Bold)
                                Text(
                                    text = plantState.plant?.details?.careLevel ?: stringResource(id = StringResources.plant_missing_data_indicator),
                                    modifier = Modifier.padding(start = 2.dp)
                                )
                                val (plantCareIcon, iconTint) = getCareLevelIcon(plantState.plant?.details?.careLevel ?: "missing")
                                Icon(
                                    imageVector = plantCareIcon,
                                    contentDescription = null,
                                    modifier = Modifier.padding(horizontal = 2.dp),
                                    tint = iconTint
                                )
                            }
                            Row(
                                modifier = Modifier.padding(vertical = 15.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Flowers:",
                                    fontWeight = FontWeight.Bold)
                                Icon(
                                    imageVector = if (plantState.plant?.details?.flowers == true) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    contentDescription = null,
                                    modifier = Modifier.padding(start = 2.dp),
                                    tint = if (plantState.plant?.details?.flowers == true) Color.Green else Color.Red
                                )
                                Spacer(modifier = Modifier.width(15.dp))
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null)
                                Text(
                                    text = "Flowering season:",
                                    fontWeight = FontWeight.Bold)
                                Text(
                                    text = plantState.plant?.details?.floweringSeason ?: stringResource(id = StringResources.plant_missing_data_indicator),
                                    modifier = Modifier.padding(start = 2.dp))
                            }
                        }
                    }
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                            .border(2.dp, Color.Gray, RoundedCornerShape(20.dp))
                            .heightIn(min = 250.dp, max = 250.dp)
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center
                    ) {
                        if (plantState.aiDescription.isEmpty()) {
                            CircularProgressIndicator()
                        } else {
                            Text(
                                text = plantState.aiDescription,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .align(Alignment.TopStart),
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
                item {
                    Text(
                        text = "Places where you can buy the plant:",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp),
                    )

                    GoogleMap(
                        modifier = Modifier
                            .height(300.dp)
                            .padding(horizontal = 20.dp),
                        cameraPositionState = cameraPositionState
                    ) {
                        if (plantState.locations.isNotEmpty()) {
                            plantState.locations.forEach { location ->
                                val markerState = rememberMarkerState(
                                    position = LatLng(
                                        location.latitude,
                                        location.longitude
                                    )
                                )
                                Marker(
                                    state = markerState,
                                    title = location.name,
                                    snippet = location.address
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = "An error occurred when loading details",
                    color = Color.Red,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

fun getCareLevelIcon(careLevel: String): Pair<ImageVector, Color> {
    return when (careLevel.lowercase(Locale.ROOT)) {
        "low" -> Icons.Default.SignalCellularAlt1Bar to Color.Green
        "medium" -> Icons.Default.SignalCellularAlt2Bar to Color.Yellow
        "high" -> Icons.Default.SignalCellularAlt to Color.Red
        else -> Icons.Default.SignalCellularAlt to Color.Red
    }
}

fun mapWeatherIcon(sunlight: String): Pair<ImageVector, Color> {
    return when (sunlight.lowercase()) {
        "full sun" -> Icons.Outlined.WbSunny to Color.Yellow
        "part shade" -> Icons.Filled.FilterDrama to Color.Gray
        "filtered shade" -> Icons.Filled.WbCloudy to Color(0xFF90CAF9)
        "part sun" -> Icons.Filled.BrightnessMedium to Color(0xFFFFC107)
        "part sun/part shade" -> Icons.Filled.WbTwilight to Color(0xFF7E57C2)
        else -> Icons.AutoMirrored.Filled.HelpOutline to Color.Black
    }
}

/*
@Composable
@Preview
fun ApiPlantDetailsScreen_Preview() {
    ApiPlantDetailsScreen()
}
*/