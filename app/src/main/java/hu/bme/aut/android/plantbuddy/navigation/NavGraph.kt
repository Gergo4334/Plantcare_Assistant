package hu.bme.aut.android.plantbuddy.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import hu.bme.aut.android.plantbuddy.feature.auth.login.LoginScreen
import hu.bme.aut.android.plantbuddy.feature.auth.register.RegisterScreen
import hu.bme.aut.android.plantbuddy.feature.home.HomeScreen
import hu.bme.aut.android.plantbuddy.feature.home.api_plants.details.ApiPlantDetailsScreen
import hu.bme.aut.android.plantbuddy.feature.home.user_plants.details.UserPlantDetailsScreen
import hu.bme.aut.android.plantbuddy.feature.splash.SplashScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                userLoggedInNavigation = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                userLoggedOutNavigation = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                onLogoutClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.UserPlantDetails.route,
            arguments = listOf(navArgument("plantId") { type = NavType.StringType })
        ) {backstackEntry ->
            val plantId = backstackEntry.arguments?.getString("plantId")
            plantId?.let {
                UserPlantDetailsScreen(onNavigateBack = { navController.popBackStack() })
            }
        }

        composable(
            route = Screen.ApiPlantDetails.route,
            arguments = listOf(navArgument("plantId") { type = NavType.StringType })
        ) {
            ApiPlantDetailsScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}