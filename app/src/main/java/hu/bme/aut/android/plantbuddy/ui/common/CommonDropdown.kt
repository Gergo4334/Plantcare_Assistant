package hu.bme.aut.android.plantbuddy.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CommonDropdown(
    label: String,
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    itemToString: (T) -> String = { it.toString() },
    enabled: Boolean = true,
    color: TextFieldColors? = null
) {
    var isExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded },
        modifier = modifier
    ) {
        TextField(
            readOnly = true,
            value = selectedOption?.let { itemToString(it) } ?: "Select an option",
            onValueChange = {},
            label = { Text(text = label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            colors = color
                ?: TextFieldDefaults.colors(
                unfocusedContainerColor = Color.LightGray,
                focusedContainerColor = Color.White,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .clickable { isExpanded = !isExpanded },
            enabled = enabled
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = itemToString(option))},
                    onClick = {
                        onOptionSelected(option)
                        isExpanded = false
                    }
                )
            }
        }
    }
}

@Composable
@Preview
fun CommonDropdown_Preview() {
    CommonDropdown(
        label = "Label",
        options = listOf("days", "weeks", "months"),
        selectedOption = "days",
        onOptionSelected = {}
    )
}