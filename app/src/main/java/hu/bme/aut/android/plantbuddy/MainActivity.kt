package hu.bme.aut.android.plantbuddy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.android.plantbuddy.navigation.NavGraph
import hu.bme.aut.android.plantbuddy.ui.theme.PlantBuddyTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        viewModel.registerFcmToken()
        handleIntentAction(intent)
        enableEdgeToEdge()
        setContent {
            PlantBuddyTheme {
                NavGraph()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntentAction(intent)
    }

    private fun handleIntentAction(intent: Intent) {
        val plantId = intent.getStringExtra("PLANT_ID")
        when (intent.action) {
            "WATER_NOW" -> {
                plantId?.let { viewModel.waterNow(it) }
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        val name = "Watering reminders"
        val descriptionText = "Notification about watering plant"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("watering_channel", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PlantBuddyTheme {
        Greeting("Android")
    }
}