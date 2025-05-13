package in2000.team42.ui.screens.home.bottomSheet.configuration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import in2000.team42.data.solarPanels.SolarPanelModel
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.res.stringResource
import in2000.team42.R

@Composable
fun SolarPanelDropdown(
    modifier: Modifier = Modifier,
    panelOptions: List<SolarPanelModel>,
    selectedPanel: SolarPanelModel,
    onPanelSelected: (SolarPanelModel) -> Unit,
    label: String = stringResource(R.string.homescreen_choose_solar_panel)
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.TopStart)
    ) {

        Box(
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedPanel.name,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = { Text(label) },
                readOnly = true,
                singleLine = true,
                enabled = false,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = if (expanded) "Skjul valg" else "Vis valg",
                        modifier = Modifier.rotate(if (expanded) 180f else 0f)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    disabledBorderColor = MaterialTheme.colorScheme.primary,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            panelOptions.forEach { panel ->
                DropdownItem(
                    panelInfo = panel,
                    onClick = {
                        onPanelSelected(panel)
                        expanded = false
                    }
                )
            }
        }
    }
}