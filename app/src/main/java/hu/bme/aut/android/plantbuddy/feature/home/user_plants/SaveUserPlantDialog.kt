package hu.bme.aut.android.plantbuddy.feature.home.user_plants

import android.app.DatePickerDialog
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import hu.bme.aut.android.plantbuddy.ui.common.CameraCapture
import hu.bme.aut.android.plantbuddy.ui.common.NormalTextField
import java.time.LocalDate
import hu.bme.aut.android.plantbuddy.R.string as StringResources

@ExperimentalMaterial3Api
@Composable
fun SaveUserPlantDialog(
    newPlantName: String,
    onPlantNameChange: (String) -> Unit,
    newPlantCycle: String,
    onPlantCycleChange: (String) -> Unit,
    newPlantWateringUnit: String,
    onPlantWateringChange: (String) -> Unit,
    newPlantLastWateredDate: LocalDate,
    onPlantLastWateredChange: (LocalDate) -> Unit,
    newPlantIsIndoor: Boolean,
    onPlantIndoorChange: (Boolean) -> Unit,
    newPlantIsFavourite: Boolean,
    onPlantFavouriteChange: (Boolean) -> Unit,
    onPlantImageChange: (Uri) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var launchCamera by remember { mutableStateOf(false) }

    // Function to handle date selection
    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _, year, month, dayOfMonth ->
            onPlantLastWateredChange(LocalDate.of(year, month + 1, dayOfMonth)) // Months are 0-indexed in DatePickerDialog
        },
        newPlantLastWateredDate.year,
        newPlantLastWateredDate.monthValue - 1, // Adjusting for 0-indexed months
        newPlantLastWateredDate.dayOfMonth
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(id = StringResources.save_plant_dialog_title))
                Spacer(modifier = Modifier.width(15.dp))
                IconButton(
                    onClick = { onDismiss() },
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = StringResources.close_icon_content_description)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (capturedImageUri == null) {
                    IconButton(
                        onClick = {
                            launchCamera = true
                        },
                        modifier = Modifier.border(1.dp, Color.Black, RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Take photo"
                        )
                    }
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(model = capturedImageUri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .clickable {
                                launchCamera = true
                            }
                    )
                }
                CameraCapture(
                    launchCamera = launchCamera,
                    onImageFile = {
                        capturedImageUri = it
                        onPlantImageChange(it)
                        launchCamera = false
                    }
                )

                // Plant name field
                NormalTextField(
                    value = newPlantName,
                    label = "Name",
                    onValueChange = { onPlantNameChange(it) },
                    leadingIcon = { Icon(Icons.Default.Eco, contentDescription = null) },
                    trailingIcon = { },
                    onDone = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )

                // Plant cycle field
                NormalTextField(
                    value = newPlantCycle,
                    label = "Cycle",
                    onValueChange = { onPlantCycleChange(it) },
                    leadingIcon = { Icon(Icons.Default.Autorenew, contentDescription = null) },
                    trailingIcon = { },
                    onDone = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )

                NormalTextField(
                    value = newPlantWateringUnit,
                    label = "Watering period in days",
                    onValueChange = { onPlantWateringChange(it) },
                    leadingIcon = { Icon(Icons.Default.InvertColors, contentDescription = null) },
                    trailingIcon = { },
                    onDone = {
                        focusManager.clearFocus()
                    }
                )
                Spacer(modifier = Modifier.height(5.dp))
                // Last watered date label with date picker
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showDatePicker = true }
                ) {
                    Text(text = "Last watered: ${newPlantLastWateredDate.toString()}")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = stringResource(id = StringResources.calendar_icon_content_description)
                    )
                }

                // Show the DatePickerDialog when requested
                if (showDatePicker) {
                    datePickerDialog.show()
                    showDatePicker = false
                }

                // Indoor and Favourite row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = newPlantIsIndoor,
                            onCheckedChange = { onPlantIndoorChange(it) }
                        )
                        Text(text = stringResource(id = StringResources.indoor_text))
                    }
                    Spacer(modifier = Modifier.width(32.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onPlantFavouriteChange(!newPlantIsFavourite) }
                        ) {
                            if (newPlantIsFavourite) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = stringResource(id = StringResources.star_icon_content_description),
                                    tint = Color.Yellow
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = stringResource(id = StringResources.star_icon_content_description),
                                    tint = Color.Gray
                                )
                            }
                        }
                        Text(text = stringResource(id = StringResources.favourite_text))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF508B0D)
                ),
                modifier = Modifier.padding(horizontal = 5.dp)
            ) {
                Text(text = stringResource(id = StringResources.save_button_text))
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text(text = stringResource(id = StringResources.cancel_button_text))
            }
        }
    )
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun SaveUserPlantDialog_Preview() {
    SaveUserPlantDialog(
        newPlantName = "name",
        onPlantNameChange = { },
        newPlantCycle = "cycle",
        onPlantCycleChange = { },
        newPlantWateringUnit = "watering value",
        onPlantWateringChange = { },
        newPlantLastWateredDate = LocalDate.now(),
        onPlantLastWateredChange = { },
        newPlantIsIndoor = true,
        onPlantIndoorChange = { },
        newPlantIsFavourite = false,
        onPlantFavouriteChange = { },
        onPlantImageChange = { },
        onDismiss = { },
        onSave = { }
    )
}