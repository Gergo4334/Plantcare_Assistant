package hu.bme.aut.android.plantbuddy.ui.common

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@ExperimentalMaterial3Api
@Composable
fun NormalTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    onDone: (KeyboardActionScope.() -> Unit)?,
    colors: TextFieldColors? = null
) {
    TextField(
        value = value.trim(),
        onValueChange = onValueChange,
        label = { Text(text = label) },
        leadingIcon = leadingIcon,
        trailingIcon = if (isError) {
            {
                Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null)
            }
        } else {
            {
                trailingIcon?.let { trailingIcon() }
            }
        },
        modifier = modifier
            .width(TextFieldDefaults.MinWidth),
        singleLine = true,
        readOnly = readOnly,
        isError = isError,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = onDone
        ),
        colors = colors
            ?: TextFieldDefaults.colors(
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

@ExperimentalMaterial3Api
@Preview
@Composable
fun NormalTextView_Preview(){
    NormalTextField(
        value = "abc",
        label = "kecske",
        onValueChange = {},
        leadingIcon = {},
        onDone = {}
    )
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun NormalTextView_Error_Preview(){
    NormalTextField(
        value = "abc",
        label = "zsiraf",
        onValueChange = {},
        leadingIcon = {},
        trailingIcon = {},
        onDone = {},
        isError = true
    )
}