package hu.bme.aut.android.plantbuddy.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import hu.bme.aut.android.plantbuddy.R.string as StringResources

@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomNavigationBar(pagerState: PagerState, coroutineScope: CoroutineScope) {
    NavigationBar(
        containerColor = Color(0xFF9CCC65),
    ) {
        NavigationBarItem(
            selected = pagerState.currentPage == 0,
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(0)
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = stringResource(id = StringResources.list_icon_content_description),
                    modifier = Modifier.size(34.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                unselectedIconColor = Color.White,
                indicatorColor = Color.LightGray
            )
        )

        NavigationBarItem(
            selected = pagerState.currentPage == 1,
            onClick = {
                  coroutineScope.launch {
                      pagerState.animateScrollToPage(1)
                  }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.LocalFlorist,
                    contentDescription = stringResource(id = StringResources.localflorist_icon_content_description),
                    modifier = Modifier.size(30.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                unselectedIconColor = Color.White,
                indicatorColor = Color.LightGray
            )
        )

        NavigationBarItem(
            selected = pagerState.currentPage == 2,
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(2)
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = stringResource(id = StringResources.account_icon_content_description),
                    modifier = Modifier.size(30.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                unselectedIconColor = Color.White,
                indicatorColor = Color.LightGray
            )
        )
    }
}