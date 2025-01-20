package hu.bme.aut.android.plantbuddy.feature.home.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import hu.bme.aut.android.plantbuddy.R
import hu.bme.aut.android.plantbuddy.ui.common.GradientBox
import hu.bme.aut.android.plantbuddy.ui.common.SlideshowDialog

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProfileScreen(
    pagerState: PagerState,
    viewModel: ProfileScreenViewModel = hiltViewModel()
) {
    val profileState by viewModel.state.collectAsStateWithLifecycle()
    var isSlideshowVisible by remember { mutableStateOf(false) }
    var selectedImages by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(key1 = pagerState.currentPage) {
        if (pagerState.currentPage == 2) {
            if (profileState.username.isEmpty()){
                viewModel.onEvent(ProfileScreenEvent.FetchUser)
            }
            if (profileState.images.isEmpty()) {
                viewModel.onEvent(ProfileScreenEvent.FetchImages)
            }
        }
    }


    GradientBox(
        modifier = Modifier.fillMaxSize(),
        colors = listOf(Color(0xFF9CCC65), Color(0xFFC5E1A5))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 70.dp)
                    .clip(RoundedCornerShape(topStart = 140.dp, topEnd = 140.dp))
                    .background(Color.White)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(top = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                    Text(
                        text = profileState.username,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFlorist,
                        contentDescription = null
                    )
                    Text(
                        text = profileState.favouritePlant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Button(
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9CCC65),
                        contentColor = Color.White
                    ),
                ) {
                    Text(text = "Reset password")
                }
                Button(
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                ) {
                    Text(text = "Sign out")
                }
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    thickness = 2.dp
                )

                if (profileState.isImagesLoading) {
                    CircularProgressIndicator()
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        profileState.images.forEach { (id, imageList) ->
                            if (imageList.isNotEmpty()) {
                                item {
                                    PlantImageCell(
                                        imageUrl = imageList.first(),
                                        onClick = {
                                            selectedImages = imageList
                                            isSlideshowVisible = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                if (isSlideshowVisible) {
                    SlideshowDialog(
                        images = selectedImages,
                        onClose = { isSlideshowVisible = false }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(top = 20.dp)
                .align(Alignment.TopCenter)
        ) {
            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .border(3.dp, Color.Black, CircleShape),
                bitmap = ImageBitmap.imageResource(id = R.drawable.logo),
                contentDescription = null)
        }
    }
}

@Composable
fun PlantImageCell(
    imageUrl: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(2.dp)
            .clickable { onClick() }
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(10.dp))
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/*
@Composable
@Preview
fun ProfileScreen_Preview() {
    ProfileScreen()
}*/
