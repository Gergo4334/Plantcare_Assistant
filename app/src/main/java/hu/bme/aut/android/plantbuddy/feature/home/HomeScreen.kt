package hu.bme.aut.android.plantbuddy.feature.home

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import hu.bme.aut.android.plantbuddy.feature.home.api_plants.list.ApiPlantsScreen
import hu.bme.aut.android.plantbuddy.feature.home.profile.ProfileScreen
import hu.bme.aut.android.plantbuddy.feature.home.user_plants.list.UserPlantsScreen
import hu.bme.aut.android.plantbuddy.ui.common.BottomNavigationBar
import hu.bme.aut.android.plantbuddy.util.UiEvent
import kotlinx.coroutines.launch
import hu.bme.aut.android.plantbuddy.R.string as StringResources

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun HomeScreen(
    navController: NavHostController,
    onLogoutClick: () -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(initialPage = 1)
    val snackBarHostState = SnackbarHostState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var topBarText by remember { mutableStateOf("Your garden") }

    val notificationPermissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    var hasRequestedPermission by remember { mutableStateOf(false) }
    var showRationaleDialog by remember { mutableStateOf(false) }
    val requestPermissionLauncher  = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (!hasRequestedPermission) {
                scope.launch {
                    snackBarHostState.showSnackbar(
                        message = "Permission granted"
                    )
                }
                hasRequestedPermission = true
            }
        } else {
            if (!hasRequestedPermission) {
                scope.launch {
                    snackBarHostState.showSnackbar(
                        message = "Permission denied"
                    )
                }
                hasRequestedPermission = true
            }
        }
    }

    LaunchedEffect(notificationPermissionState) {
        if (!notificationPermissionState.status.isGranted && notificationPermissionState.status.shouldShowRationale) {
            showRationaleDialog = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        viewModel.uiEvent.collect { event ->
            when(event) {
                is UiEvent.Success -> {
                    onLogoutClick()
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
                title = { Text(text = topBarText, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF9CCC65)),
                modifier = Modifier.border(1.dp, Color.Black, RoundedCornerShape(5.dp)),
                actions = {
                    IconButton(onClick = { viewModel.onEvent(HomeScreenEvent.SignOut) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = stringResource(id = StringResources.logout_icon_content_description),
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(pagerState = pagerState, coroutineScope = scope)
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->

        if (showRationaleDialog) {
            AlertDialog(
                onDismissRequest = { showRationaleDialog = false },
                title = { Text(text = "Permission required") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append("In order to provide the best experience, we need access to ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)) {
                                    append("push notifications.")
                                }
                            }
                        )
                        Text("By granting this permission, we will be able to notify you about your plants' needs, like when it's time to water them.")
                        Text("We promise not to disturb you, and we'll only send notifications related to your plants.")
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        showRationaleDialog = false
                    }) {
                        Text(text = "Grant permission")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRationaleDialog = false }) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
        HorizontalPager(
            count = 3,
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {page ->
            when (page) {
                0 -> {
                    topBarText = "All plants"
                    ApiPlantsScreen(pagerState = pagerState, navController = navController)
                }
                1 -> {
                    topBarText = "Your garden"
                    UserPlantsScreen(navController, pagerState = pagerState)
                }
                2 -> {
                    topBarText = "Profile"
                    ProfileScreen(pagerState = pagerState)
                }
            }
        }
    }
}


/*
@Composable
@Preview
fun HomeScreen_Preview(){
    HomeScreen()
}
*/