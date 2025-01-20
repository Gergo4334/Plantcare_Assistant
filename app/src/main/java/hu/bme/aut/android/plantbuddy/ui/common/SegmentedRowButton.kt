package hu.bme.aut.android.plantbuddy.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SegmentedRowButton(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 26.dp)
            .border(
                width = 2.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(50)
            )
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = selectedOption == option
            Button(
                onClick = { onOptionSelected(option) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFF9DD585) else Color.White
                ),
                shape = when (index) {
                    0 -> RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp)
                    options.size - 1 -> RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp)
                    else -> RoundedCornerShape(0.dp)
                },
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Color(0xFF9DD585) else Color.Gray,
                        shape = when (index) {
                            0 -> RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp)
                            options.size - 1 -> RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp)
                            else -> RoundedCornerShape(0.dp)
                        }
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = option,
                        color = if (isSelected) Color.Black else Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}