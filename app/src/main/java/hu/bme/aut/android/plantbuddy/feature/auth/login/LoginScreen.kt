package hu.bme.aut.android.plantbuddy.feature.auth.login

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import hu.bme.aut.android.plantbuddy.R
import hu.bme.aut.android.plantbuddy.ui.common.EmailTextField
import hu.bme.aut.android.plantbuddy.ui.common.GradientBox
import hu.bme.aut.android.plantbuddy.ui.common.PasswordTextField
import hu.bme.aut.android.plantbuddy.ui.tool.rememberImeState
import hu.bme.aut.android.plantbuddy.util.UiEvent
import kotlinx.coroutines.launch
import hu.bme.aut.android.plantbuddy.R.string as StringResources

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@ExperimentalMaterial3Api
@Composable
fun LoginScreen(
    onSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: LoginScreenViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = SnackbarHostState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val imeVisibility by rememberImeState()

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when(event) {
                is UiEvent.Success -> {
                    onSuccess()
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
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        modifier = Modifier.fillMaxSize()
    ) {
        GradientBox {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val animatedUpperSection by animateFloatAsState(targetValue = if (imeVisibility) 0f else 0.35f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(animatedUpperSection),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        bitmap = ImageBitmap.imageResource(id = R.drawable.plantbuddy_hero_removebg),
                        contentScale = ContentScale.Fit,
                        contentDescription = stringResource(id = StringResources.plant_buddy_logo_description)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                        .background(Color.White)
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.fillMaxSize(0.05f))
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = StringResources.login_text_first_part),
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFF7DB63B)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = StringResources.login_text_last_part),
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                    EmailTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        value = state.email,
                        label = stringResource(id = StringResources.email_address),
                        onValueChange = { viewModel.onEvent(LoginUserEvent.EmailChanged(it)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Color(0xFF7DB63B)
                            )
                        },
                        imeAction = ImeAction.Next,
                        onDone = { }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    PasswordTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        value = state.password,
                        label = stringResource(id = StringResources.password),
                        onValueChange = { viewModel.onEvent(LoginUserEvent.PasswordChanged(it)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = Color(0xFF7DB63B)
                            )
                        },
                        isVisible = state.passwordVisibility,
                        onVisibilityChanged = { viewModel.onEvent(LoginUserEvent.PasswordVisibilityChanged) },
                        onDone = { }
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        TextButton(
                            onClick = { /*TODO*/ },

                            ) {
                            Text(
                                text = stringResource(id = StringResources.forgot_password),
                                color = Color(0xFF7DB63B)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            viewModel.onEvent(LoginUserEvent.SignIn)
                            focusManager.clearFocus()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7DB63B),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = stringResource(id = StringResources.login_button),
                            style = TextStyle(fontSize = 21.sp, fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = StringResources.register_navigation_button_text)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        TextButton(
                            onClick = onRegisterClick,
                        ) {
                            Text(
                                text = stringResource(id = StringResources.register_navigation_button),
                                color = Color(0xFF7DB63B)
                            )
                        }
                    }
                }
            }
        }
    }
}

/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun LoginScreen_Preview() {
    LoginScreen(
        onSuccess = {},
        onRegisterClick = {},
        viewModel = LoginScreenViewModel()
    )
}
*/