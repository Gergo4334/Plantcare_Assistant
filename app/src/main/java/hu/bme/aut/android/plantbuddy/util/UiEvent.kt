package hu.bme.aut.android.plantbuddy.util

import hu.bme.aut.android.plantbuddy.ui.model.UiText

sealed class UiEvent {
    object Success: UiEvent()

    data class Failure(val message: UiText): UiEvent()
}