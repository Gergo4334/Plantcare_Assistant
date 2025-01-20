package hu.bme.aut.android.plantbuddy.navigation

sealed class Screen(val route: String) {
    object Home: Screen("home")
    object Profile: Screen("profile")
    object Login: Screen("login")
    object Register: Screen("register")
    object Splash: Screen("splash")
    object UserPlantDetails: Screen("userPlantDetails/{plantId}")
    object ApiPlantDetails: Screen("apiPlantDetails/{plantId}")
}