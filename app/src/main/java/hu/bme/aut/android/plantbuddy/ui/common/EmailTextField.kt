package hu.bme.aut.android.plantbuddy.ui.common

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@ExperimentalMaterial3Api
@Composable
fun EmailTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Done,
    readOnly: Boolean = false,
    isError: Boolean = false,
    onDone: (KeyboardActionScope.() -> Unit)? = null
) {
    TextField(
        value = value.trim(),
        onValueChange = onValueChange,
        label = { Text(text = label) },
        leadingIcon = leadingIcon,
        trailingIcon = if(isError) {
            {
               Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null)
            }
        } else {
            {
                if(trailingIcon != null) {
                    trailingIcon()
                }
            }
        },
        modifier = modifier.width(TextFieldDefaults.MinWidth),
        singleLine = true,
        readOnly = readOnly,
        isError = isError,
        enabled = enabled,
        keyboardActions = KeyboardActions(
            onDone = onDone
        ),
        colors = TextFieldDefaults.colors(
            unfocusedTextColor = Color.Black,
            focusedTextColor = Color.Black,
            unfocusedContainerColor = Color(0xFFE6E6E6),
            focusedContainerColor = Color.White,
            errorIndicatorColor = Color.Red,
            focusedIndicatorColor = Color.Black,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(10.dp)

    )
}