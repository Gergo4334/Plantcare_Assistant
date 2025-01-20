package hu.bme.aut.android.plantbuddy.feature.home.user_plants.list

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.android.gms.common.internal.service.Common
import hu.bme.aut.android.plantbuddy.R
import hu.bme.aut.android.plantbuddy.domain.model.plant.filter.IndoorOutdoorFilter
import hu.bme.aut.android.plantbuddy.domain.model.plant.filter.UserPlantFilterButtonState
import hu.bme.aut.android.plantbuddy.feature.home.user_plants.SaveUserPlantDialog
import hu.bme.aut.android.plantbuddy.ui.common.CommonDropdown
import hu.bme.aut.android.plantbuddy.ui.common.NormalTextField
import hu.bme.aut.android.plantbuddy.ui.common.SegmentedRowButton
import hu.bme.aut.android.plantbuddy.ui.plant.UserPlantCard
import hu.bme.aut.android.plantbuddy.util.UiEvent
import kotlinx.coroutines.launch
import hu.bme.aut.android.plantbuddy.R.string as StringResources

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UserPlantsScreen(
    navController: NavHostController,
    pagerState: PagerState,
    viewModel: UserPlantsViewModel = hiltViewModel()
) {
    val plantListState by viewModel.plantListState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var showSavePlantDialog by remember { mutableStateOf(false) }
    val segmentedButtonOptions = listOf("Indoor", "Outdoor")
    var selectedIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = pagerState.currentPage) {
        if (pagerState.currentPage == 1) {
            if(plantListState.plantList.isEmpty()) {
                viewModel.onEvent(UserPlantEvent.FetchPlants)
            }
            viewModel.uiEvent.collect { event ->
                when(event) {
                    is UiEvent.Success -> {
                        Toast.makeText(context, viewModel.successMessage.value, Toast.LENGTH_SHORT).show()
                    }
                    is UiEvent.Failure -> {
                        Toast.makeText(context, event.message.asString(context), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Filter Dropdown
                CommonDropdown(
                    label = stringResource(id = StringResources.filter_label_text),
                    options = UserPlantFilterButtonState.entries.toList(),
                    selectedOption = plantListState.filterButtonState,
                    onOptionSelected = { viewModel.onEvent(UserPlantEvent.FilterButtonChanged(it)) },
                    modifier = Modifier.weight(1f)
                )

                // Search TextField
                NormalTextField(
                    value = plantListState.searchText,
                    label = stringResource(id = StringResources.filter_search_label_text),
                    onValueChange = { viewModel.onEvent(UserPlantEvent.SearchTextChanged(it)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = null,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(2f)
                        .fillMaxWidth(),
                    onDone = { focusManager.clearFocus() }
                )
            }
            
            SingleChoiceSegmentedButtonRow {
                segmentedButtonOptions.forEachIndexed { index, option ->
                    SegmentedButton(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            when (option) {
                                "Indoor" ->  {
                                    viewModel.onEvent(UserPlantEvent.IndoorFilterChanged(IndoorOutdoorFilter.INDOOR))
                                }
                                "Outdoor" -> {
                                    viewModel.onEvent(UserPlantEvent.IndoorFilterChanged(IndoorOutdoorFilter.OUTDOOR))
                                }
                                else -> { }
                            }
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = segmentedButtonOptions.size),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = Color(0xFF9CCC65),
                        )
                    ) {
                        Text(text = option)
                    }
                }
            }

            if (plantListState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (plantListState.plantList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Your garden is empty",
                        )
                    }
                } else {
                    // Plant cards grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize(),
                        content = {
                            items(plantListState.filteredList.size) { index ->
                                val plant = plantListState.filteredList[index]
                                UserPlantCard(
                                    modifier = Modifier.aspectRatio(0.6f),
                                    onClick = { navController.navigate("userPlantDetails/${plant.id}") },
                                    onFavouriteClick = { viewModel.onEvent(
                                        UserPlantEvent.UpdateFavouriteStatus(
                                            plant
                                        )
                                    ) },
                                    onWaterClick = { viewModel.onEvent(UserPlantEvent.WaterPlant(plant)) },
                                    plant = plant
                                )
                            }
                        }
                    )
                }

            }
        }

        FloatingActionButton(
            onClick = { showSavePlantDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = StringResources.add_icon_content_description))
        }

        if (showSavePlantDialog) {
            SaveUserPlantDialog(
                newPlantName = viewModel.newPlantName,
                onPlantNameChange = { viewModel.updatePlantName(it) },
                newPlantCycle = viewModel.newPlantCycle,
                onPlantCycleChange = { viewModel.updatePlantCycle(it) },
                newPlantWateringUnit = viewModel.newPlantWateringPeriodValue,
                onPlantWateringChange = { viewModel.updateWateringPeriodValue(it) },
                newPlantLastWateredDate = viewModel.newPlantLastWateredDate,
                onPlantLastWateredChange = { viewModel.updateLastWateredDate(it) },
                newPlantIsIndoor = viewModel.newPlantIndoor,
                onPlantIndoorChange = { viewModel.updateIndoorStatus(it) },
                newPlantIsFavourite = viewModel.newPlantIsFavourite,
                onPlantFavouriteChange = { viewModel.updateIsFavourite(it) },
                onPlantImageChange = { viewModel.updateNewPlantImage(it) },
                onDismiss = {
                    showSavePlantDialog = false
                },
                onSave = {
                    if (viewModel.newPlantName.isBlank() || viewModel.newPlantCycle.isBlank() || viewModel.newPlantWateringPeriodValue.isBlank()) {
                        Toast.makeText(context, viewModel.successMessage.value, Toast.LENGTH_SHORT).show()
                    }
                    else {
                        viewModel.onEvent(UserPlantEvent.SaveNewPlant)
                        showSavePlantDialog = false
                    }
                }
            )
        }
    }
}