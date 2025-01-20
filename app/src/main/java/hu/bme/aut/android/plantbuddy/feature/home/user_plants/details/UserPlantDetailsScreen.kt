package hu.bme.aut.android.plantbuddy.feature.home.user_plants.details

import android.app.DatePickerDialog
import android.net.Uri
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import hu.bme.aut.android.plantbuddy.R
import hu.bme.aut.android.plantbuddy.ui.common.CameraCapture
import hu.bme.aut.android.plantbuddy.ui.common.CommonDropdown
import hu.bme.aut.android.plantbuddy.ui.common.ExpandableFabMenu
import hu.bme.aut.android.plantbuddy.ui.common.NormalTextField
import hu.bme.aut.android.plantbuddy.util.UiEvent
import kotlinx.coroutines.launch
import java.time.LocalDate
import hu.bme.aut.android.plantbuddy.R.string as StringResources

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPlantDetailsScreen(
    onNavigateBack: () -> Unit,
    viewModel: UserPlantDetailsViewModel = hiltViewModel()
) {
    val plantState by viewModel.plantState.collectAsStateWithLifecycle()
    val snackBarHostState = SnackbarHostState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val showDatePicker = remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var launchCamera by remember { mutableStateOf(false) }

    val datePickerDialog = plantState.plant?.let {
        DatePickerDialog(
            LocalContext.current,
            { _, year, month, dayOfMonth ->
                viewModel.updateLastWateredDate(LocalDate.of(year, month + 1, dayOfMonth)) // Months are 0-indexed in DatePickerDialog
            },
            it.lastWateredDate.year,
            it.lastWateredDate.monthValue.minus(1), // Adjusting for 0-indexed months
            it.lastWateredDate.dayOfMonth
        )
    }

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(UserPlantDetailsEvent.FetchPlantDetails)
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = viewModel.successMessage.value
                        )
                    }
                }
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
                title = { Text(text = plantState.plant?.name + "' details", color = Color.White)},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF9CCC65)),
                modifier = Modifier.border(1.dp, Color.Black, RoundedCornerShape(5.dp)),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = StringResources.arrow_back_icon_content_description),
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (plantState.isEditing) {
                        IconButton(onClick = { viewModel.onEvent(UserPlantDetailsEvent.SaveModifications) }) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = stringResource(id = StringResources.save_icon_content_description),
                                tint = Color.White,

                                )
                        }
                    } else {
                        IconButton(onClick = { viewModel.onEvent(UserPlantDetailsEvent.EditDetails) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(id = StringResources.edit_icon_content_description),
                                tint = Color.White,

                                )
                        }
                    }
                    IconButton(onClick = { viewModel.onEvent(UserPlantDetailsEvent.UpdateFavouriteStatus) }) {
                        Icon(
                            imageVector = if (plantState.plant?.isFavourite == true) Icons.Default.Star else Icons.Outlined.Star,
                            contentDescription = stringResource(id = StringResources.star_icon_content_description),
                            tint = Color.Yellow,
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        if (plantState.isLoading) {
            CircularProgressIndicator()
        } else if (plantState.plant != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFF9CCC65),
                                    shape = RoundedCornerShape(
                                        bottomStart = 30.dp,
                                        bottomEnd = 30.dp
                                    )
                                )
                                .padding(vertical = 10.dp, horizontal = 30.dp)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(200.dp)
                                        .background(Color.Black)
                                ) {
                                    if (viewModel.editingImage.value.isNullOrEmpty()) {
                                        IconButton(
                                            onClick = {
                                                launchCamera = true
                                            },
                                            modifier = Modifier
                                                .align(Alignment.Center),
                                            enabled = plantState.isEditing
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Take photo"
                                            )
                                        }
                                    } else {
                                        AsyncImage(
                                            model = viewModel.editingImage.value,
                                            contentDescription = null,
                                            contentScale = ContentScale.Inside,
                                            placeholder = painterResource(id = R.drawable.plant_placeholder),
                                            error = painterResource(id = R.drawable.error_icon_4),
                                            modifier = Modifier
                                                .border(3.dp, Color.Black, RoundedCornerShape(15.dp))
                                                .clip(RoundedCornerShape(15.dp))
                                                .fillMaxSize()
                                                .then(if (plantState.isEditing) Modifier.clickable { launchCamera = true } else Modifier)
                                        )
                                    }
                                }
                                CameraCapture(
                                    launchCamera = launchCamera,
                                    onImageFile = {
                                        capturedImageUri = it
                                        viewModel.updatePlantImage(it)
                                        launchCamera = false
                                    }
                                )

                                NormalTextField(
                                    value = viewModel.editingName.value,
                                    label = stringResource(id = StringResources.plant_name_label),
                                    onValueChange = { viewModel.updatePlantName(it) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.LocalFlorist,
                                            contentDescription = null
                                        )
                                    },
                                    trailingIcon = {},
                                    modifier = Modifier.clip(RoundedCornerShape(10.dp)),
                                    enabled = plantState.isEditing,
                                    isError = viewModel.editingName.value.isEmpty(),
                                    onDone = { focusManager.clearFocus() },
                                    colors = plainWhiteTextFieldColors()
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    NormalTextField(
                                        value = viewModel.editingWateringValue.value,
                                        label = stringResource(id = StringResources.plant_watering_value_label),
                                        onValueChange = { viewModel.updateWateringPeriodValue(it) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.WaterDrop,
                                                contentDescription = null
                                            )
                                        },
                                        trailingIcon = {},
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .weight(0.6f),
                                        enabled = plantState.isEditing,
                                        isError = viewModel.editingWateringValue.value.isEmpty(),
                                        onDone = { focusManager.clearFocus() },
                                        colors = plainWhiteTextFieldColors()
                                    )
                                    CommonDropdown(
                                        label = stringResource(id = StringResources.unit_label),
                                        options = listOf("days", "weeks", "months"),
                                        selectedOption = viewModel.editingWateringPeriod.value,
                                        onOptionSelected = { viewModel.updateWateringPeriodUnit(it) },
                                        modifier = Modifier.weight(0.4f),
                                        enabled = plantState.isEditing,
                                        color = plainWhiteTextFieldColors()
                                    )

                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    modifier = Modifier
                                        .then(if (plantState.isEditing) Modifier.clickable {
                                            showDatePicker.value = true
                                        } else Modifier)
                                        .background(
                                            Color.White,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .padding(15.dp)
                                ) {
                                    Text(
                                        text = "Last watered: ${viewModel.editingLastWateredDate.value}",
                                        color = if (plantState.isEditing) Color.Black else Color.LightGray
                                    )
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = stringResource(id = StringResources.calendar_icon_content_description),
                                        tint = if (plantState.isEditing) Color.Black else Color.LightGray
                                    )
                                }
                                if (showDatePicker.value && datePickerDialog != null) {
                                    datePickerDialog.show()
                                    showDatePicker.value = false
                                }
                            }
                        }
                    }
                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 15.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                NormalTextField(
                                    value = viewModel.editingType.value,
                                    label = stringResource(id = StringResources.plant_type_label),
                                    onValueChange = { viewModel.updatePlantType(it) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Nature,
                                            contentDescription = null
                                        )
                                    },
                                    trailingIcon = {},
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .clip(RoundedCornerShape(10.dp)),
                                    enabled = plantState.isEditing,
                                    isError = viewModel.editingType.value.isEmpty(),
                                    onDone = { focusManager.clearFocus() },
                                    colors = plainWhiteTextFieldColors()
                                )
                                NormalTextField(
                                    value = viewModel.editingCycle.value,
                                    label = stringResource(id = StringResources.plant_cycle_label),
                                    onValueChange = { viewModel.updatePlantCycle(it) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Timelapse,
                                            contentDescription = null
                                        )
                                    },
                                    trailingIcon = {},
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .clip(RoundedCornerShape(10.dp)),
                                    enabled = plantState.isEditing,
                                    isError = viewModel.editingCycle.value.isEmpty(),
                                    onDone = { focusManager.clearFocus() },
                                    colors = plainWhiteTextFieldColors()
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                NormalTextField(
                                    value = viewModel.editingSunlight.value,
                                    label = stringResource(id = StringResources.plant_sunlight_label),
                                    onValueChange = { viewModel.updatePlantSunlight(it) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.WbSunny,
                                            contentDescription = null
                                        )
                                    },
                                    trailingIcon = {},
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .clip(RoundedCornerShape(10.dp)),
                                    enabled = plantState.isEditing,
                                    isError = viewModel.editingSunlight.value.isEmpty(),
                                    onDone = { focusManager.clearFocus() },
                                    colors = plainWhiteTextFieldColors()
                                )

                                Column(
                                    modifier = Modifier.weight(0.5f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                Color.White,
                                                shape = RoundedCornerShape(10.dp)
                                            ),
                                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Icon(
                                            imageVector = Icons.Default.Home,
                                            contentDescription = null,
                                            tint = if (plantState.isEditing) Color.Black else Color.LightGray
                                        )
                                        Text(
                                            text = stringResource(id = StringResources.indoor_text),
                                            color = if (plantState.isEditing) Color.Black else Color.LightGray
                                        )
                                        Checkbox(
                                            checked = viewModel.editingIndoor.value,
                                            onCheckedChange = { viewModel.updatePlantIndoorStatus(it) },
                                            enabled = plantState.isEditing
                                        )
                                    }
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 15.dp),
                                        color = Color.LightGray
                                    )
                                }
                            }
                            Text(
                                text = stringResource(id = StringResources.plant_dimensions_text),
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextField(
                                    value = viewModel.editingDimensionMinValue.value.toString(),
                                    label = { Text(stringResource(id = StringResources.min_dimension_value_label)) },
                                    onValueChange = { viewModel.updateDimensionMin(it.toInt()) },
                                    leadingIcon = {
                                        Column(
                                            verticalArrangement = Arrangement.Center,
                                        ) {
                                            IconButton(
                                                onClick = { viewModel.increaseDimensionMin() },
                                                modifier = Modifier.size(20.dp),
                                                enabled = plantState.isEditing
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.KeyboardArrowUp,
                                                    contentDescription = stringResource(
                                                        id = StringResources.keyboard_arrow_up_content_description
                                                    )
                                                )
                                            }
                                            IconButton(
                                                onClick = { viewModel.decreaseDimensionMin() },
                                                modifier = Modifier.size(20.dp),
                                                enabled = plantState.isEditing
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.KeyboardArrowDown,
                                                    contentDescription = stringResource(
                                                        id = StringResources.keyboard_arrow_down_content_description
                                                    )
                                                )
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    enabled = plantState.isEditing,
                                    isError = viewModel.editingDimensionMinValue.value.toString()
                                        .isEmpty(),
                                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                    colors = plainWhiteTextFieldColors()
                                )
                                Text(
                                    text = " - ",
                                )
                                TextField(
                                    value = viewModel.editingDimensionMaxValue.value.toString(),
                                    label = { Text(stringResource(id = StringResources.max_dimension_value_label)) },
                                    onValueChange = { viewModel.updateDimensionMax(it.toInt()) },
                                    leadingIcon = {
                                        Column(
                                            verticalArrangement = Arrangement.Center,
                                        ) {
                                            IconButton(
                                                onClick = { viewModel.increaseDimensionMax() },
                                                modifier = Modifier.size(20.dp),
                                                enabled = plantState.isEditing
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.KeyboardArrowUp,
                                                    contentDescription = stringResource(
                                                        id = StringResources.keyboard_arrow_up_content_description
                                                    )
                                                )
                                            }
                                            IconButton(
                                                onClick = { viewModel.decreaseDimensionMax() },
                                                modifier = Modifier.size(20.dp),
                                                enabled = plantState.isEditing
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.KeyboardArrowDown,
                                                    contentDescription = stringResource(
                                                        id = StringResources.keyboard_arrow_down_content_description
                                                    )
                                                )
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    enabled = plantState.isEditing,
                                    isError = viewModel.editingDimensionMaxValue.value.toString()
                                        .isEmpty(),
                                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = plainWhiteTextFieldColors()

                                )
                                CommonDropdown(
                                    label = stringResource(id = StringResources.unit_label),
                                    options = listOf("mm", "cm", "m"),
                                    selectedOption = viewModel.editingDimensionUnit.value,
                                    onOptionSelected = { viewModel.updateDimensionUnit(it) },
                                    enabled = plantState.isEditing,
                                    modifier = Modifier.weight(1f),
                                    color = plainWhiteTextFieldColors()
                                )
                            }
                            HorizontalDivider(
                                modifier = Modifier.padding(10.dp),
                                thickness = 2.dp
                            )
                        }
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp, vertical = 5.dp)
                                .border(2.dp, Color.Gray, RoundedCornerShape(10.dp))
                                .heightIn(min = 300.dp, max = 350.dp),
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

                }
                if (plantState.isEditing) {
                    ExpandableFabMenu(
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Save",
                                    modifier = Modifier
                                        .background(Color(0xFF8CBE67), RoundedCornerShape(10.dp))
                                        .padding(4.dp)
                                )
                                FloatingActionButton(
                                    onClick = { viewModel.onEvent(UserPlantDetailsEvent.SaveModifications) },
                                    containerColor = Color(0xFF8CBE67)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Save,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Cancel",
                                    modifier = Modifier
                                        .background(Color(0xFF8CBE67), RoundedCornerShape(10.dp))
                                        .padding(4.dp)
                                )
                                FloatingActionButton(
                                    onClick = { viewModel.onEvent(UserPlantDetailsEvent.CancelEditing) },
                                    containerColor = Color(0xFF8CBE67)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Cancel,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
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
                    text = stringResource(id = StringResources.no_plant_found_error),
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

@Composable
fun plainWhiteTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        unfocusedContainerColor = Color.White,
        focusedContainerColor = Color.White,
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Black,
        unfocusedTextColor = Color.DarkGray,
        focusedTextColor = Color.Black,
        disabledContainerColor = Color.White,
        disabledTextColor = Color.LightGray,
        errorContainerColor = Color.White
    )
}

/*@Composable
@Preview
fun UserPlantDetailsScreen_Preview() {
    UserPlantDetailsScreen(plantId = "0")
}*/
