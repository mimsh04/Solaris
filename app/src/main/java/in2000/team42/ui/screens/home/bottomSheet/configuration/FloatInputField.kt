package in2000.team42.ui.screens.home.bottomSheet.configuration

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun FloatInputField(
    tittel: String,
    verdi: Float,
    onValueChange: (Float) -> Unit,
    range: IntRange = 0..90,
) {
    var showedVal by remember { mutableStateOf(verdi.toInt().toString()) }

    LaunchedEffect (verdi) {
        if (verdi == maxOf(0f, range.first.toFloat())) return@LaunchedEffect
        showedVal = verdi.toInt().toString()
    }

    @Composable // Had to add Composable to be able to use MaterialTheme.colorScheme.error
    fun getOutlineColor(showedText: String): androidx.compose.ui.graphics.Color {
        return if (showedText.isEmpty()) MaterialTheme.colorScheme.error
        else if (showedText.toIntOrNull() in range) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.error
    }

    Row(
        modifier = Modifier
    ) {
        OutlinedTextField(
            value = showedVal,
            onValueChange = { newValue ->
                showedVal = newValue
                if (showedVal.isEmpty()) onValueChange(maxOf(range.first, 0).toFloat())
                newValue.toFloatOrNull()?.let {
                    if (it.toInt() in range) {
                        onValueChange(it)
                    } else {
                        onValueChange(maxOf(range.first, 0).toFloat())
                    }
                }
            },
            label = { Text(tittel, color = MaterialTheme.colorScheme.onSurface) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.width(180.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = getOutlineColor(showedVal),
                unfocusedBorderColor = getOutlineColor(showedVal)
            )
        )
    }

}