package hu.bme.aut.android.plantbuddy.domain.model.user

data class ProfileState(
    val username: String = "",
    val favouritePlant: String = "",
    val images: Map<String, List<String>> = emptyMap(),
    val isImagesLoading: Boolean = false
)