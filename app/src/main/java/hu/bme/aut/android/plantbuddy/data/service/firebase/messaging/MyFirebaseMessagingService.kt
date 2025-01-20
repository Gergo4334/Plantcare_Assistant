package hu.bme.aut.android.plantbuddy.data.service.firebase.messaging

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import hu.bme.aut.android.plantbuddy.MainActivity
import hu.bme.aut.android.plantbuddy.R
import hu.bme.aut.android.plantbuddy.domain.interactor.firebase.firestore.FirestoreRepositoryInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyFirebaseMessagingService: FirebaseMessagingService() {
    @Inject
    lateinit var repository: FirestoreRepositoryInteractor
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM New Token", "New FMC token: $token")
        CoroutineScope(Dispatchers.IO).launch {
            repository.saveFcmToken(token)
        }
    }
    override fun onMessageReceived(message: RemoteMessage) {
        message.notification?.let {
            val isSinglePlantNotification = message.data["single_plant"]?.toBoolean() ?: false
            val plantId = message.data["plant_id"]
            sendNotification(it.title, it.body, isSinglePlantNotification, plantId)
        }
    }

    private fun sendNotification(title: String?, messageBody: String?, isSinglePlant: Boolean, plantId: String?) {
        val notificationBuilder = NotificationCompat.Builder(this, "watering_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (isSinglePlant && plantId != null) {
            val wateringIntent = Intent(this, MainActivity::class.java).apply {
                action = "WATER_NOW"
                putExtra("PLANT_ID", plantId)
            }

            val wateringPendingIntent: PendingIntent = PendingIntent.getActivity(
                this, 0, wateringIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            notificationBuilder
                .addAction(
                    R.drawable.ic_watering_notification,
                    "Just watered",
                    wateringPendingIntent
                )
        }
        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(1, notificationBuilder.build())

    }
}