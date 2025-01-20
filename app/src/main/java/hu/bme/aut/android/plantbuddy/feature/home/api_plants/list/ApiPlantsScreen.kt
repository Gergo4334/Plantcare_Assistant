package hu.bme.aut.android.plantbuddy.feature.home.api_plants.list

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.gson.Gson
import hu.bme.aut.android.plantbuddy.R
import hu.bme.aut.android.plantbuddy.domain.model.plant.filter.ApiPlantFilterButtonState
import hu.bme.aut.android.plantbuddy.ui.common.CameraCapture
import hu.bme.aut.android.plantbuddy.ui.common.CommonDropdown
import hu.bme.aut.android.plantbuddy.ui.common.NormalTextField
import hu.bme.aut.android.plantbuddy.ui.plant.ApiPlantCard
import hu.bme.aut.android.plantbuddy.util.UiEvent
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors
import hu.bme.aut.android.plantbuddy.R.string as StringResources

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ApiPlantsScreen(
    pagerState: PagerState,
    viewModel: ApiPlantsScreenViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val plantListState by viewModel.plantListState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var launchCamera by remember { mutableStateOf(false) }
    var showrecognitionResult by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = pagerState.currentPage) {
        if (pagerState.currentPage == 0) {
            if(plantListState.plantList.isEmpty()) {
                viewModel.onEvent(ApiPlantEvent.FetchPlants)
            }
            viewModel.uiEvent.collect { event ->
                when(event) {
                    is UiEvent.Success -> {
                        Toast.makeText(context, viewModel.successMessage.value, Toast.LENGTH_SHORT).show()
                    }
                    is UiEvent.Failure -> {
                        Toast.makeText(context, event.message.asString(context), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
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
                    options = ApiPlantFilterButtonState.entries.toList(),
                    selectedOption = plantListState.filterButtonState,
                    onOptionSelected = { viewModel.onEvent(ApiPlantEvent.FilterButtonChanged(it)) },
                    modifier = Modifier.weight(1f)
                )

                // Search TextField
                NormalTextField(
                    value = plantListState.searchText,
                    label = stringResource(id = StringResources.filter_search_label_text),
                    onValueChange = { viewModel.onEvent(ApiPlantEvent.SearchTextChanged(it)) },
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

            if (plantListState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
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
                            ApiPlantCard(
                                modifier = Modifier.aspectRatio(0.6f),
                                onClick = {
                                    navController.navigate("apiPlantDetails/${plant.id}")
                                },
                                plant = plant
                            )
                            if (index == plantListState.plantList.lastIndex) {
                                viewModel.onEvent(ApiPlantEvent.FetchNextPage)
                            }
                        }
                    }
                )
            }
        }
        FloatingActionButton(
            onClick = { launchCamera = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        }

        CameraCapture(
            launchCamera = launchCamera
        ) {
            viewModel.onEvent(ApiPlantEvent.imageUriChanged(it))
            launchCamera = false
            viewModel.onEvent(ApiPlantEvent.StartImageRecognition)
        }

        if (plantListState.showResultDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.closeShowResultDialog() },
                confirmButton = {
                    Button(
                        onClick = { viewModel.closeShowResultDialog() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF508B0D)
                        ),
                        modifier = Modifier.padding(horizontal = 5.dp)
                    ) {
                        Text(text = "Ok")
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        AsyncImage(
                            model = plantListState.recognitionResponseImage,
                            contentDescription = null,
                            placeholder = painterResource(id = R.drawable.plant_placeholder),
                            error = painterResource(id = R.drawable.error_icon_4),
                            modifier = Modifier.size(120.dp)
                        )
                        Text(
                            text = "${plantListState.recognitionResponseName} ~ ${plantListState.recognitionResponseProbability}",
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                                .border(2.dp, Color.Gray, RoundedCornerShape(20.dp))
                                .heightIn(min = 100.dp, max = 160.dp)
                                .verticalScroll(rememberScrollState()),
                            contentAlignment = Alignment.Center
                        ) {
                            if (plantListState.aiDescription.isEmpty()) {
                                CircularProgressIndicator()
                            } else {
                                Text(
                                    text = plantListState.aiDescription,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .align(Alignment.TopStart),
                                )
                            }
                        }
                    }
                },
            )
        }
    }
}