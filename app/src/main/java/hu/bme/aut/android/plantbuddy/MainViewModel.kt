package hu.bme.aut.android.plantbuddy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.android.plantbuddy.domain.interactor.auth.AuthServiceInteractor
import hu.bme.aut.android.plantbuddy.domain.interactor.firebase.firestore.FirestoreRepositoryInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: FirestoreRepositoryInteractor,
    private val authService: AuthServiceInteractor
): ViewModel() {
    fun waterNow(plantId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val plantToUpdate = repository.getPlantById(plantId)?.copy(lastWateredDate = LocalDate.now())
            if (plantToUpdate != null) {
                repository.updatePlant(plantToUpdate)
            }
        }
    }

    fun registerFcmToken() {
        viewModelScope.launch(Dispatchers.IO) {
            authService.observeCurrentUser().collect { user ->
                if (user != null) {
                    FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                        Log.d("FCM Register", "FCM Token: $token")
                        viewModelScope.launch(Dispatchers.IO) {
                            repository.saveFcmToken(token)
                        }
                    }
                }
            }
        }
    }
}