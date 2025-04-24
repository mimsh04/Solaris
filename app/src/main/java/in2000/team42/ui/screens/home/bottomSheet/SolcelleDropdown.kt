package in2000.team42.ui.screens.home.bottomSheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import in2000.team42.model.solarPanels.SolarPanelModel


@Composable
fun SolcelleDropdown(
    modifier: Modifier = Modifier,
    panelOptions: List<SolarPanelModel>,
    selectedPanel: SolarPanelModel,
    onPanelSelected: (SolarPanelModel) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopStart)
    ) {

        Row (
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { expanded = !expanded }
                .padding(16.dp),
        ){
            DropdownItem(selectedPanel, onClick = {expanded = !expanded}, modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
            )

            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = if (expanded) "Skjul valg" else "Vis valg",
                // Roterer 180 grader hvis expanded, ellers 0 degrees. Fikk ikke ArrowDropUp til å fungere, så roterer bare DropDown
                modifier = Modifier.rotate(if (expanded) 180f else 0f)
            )
        }



        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
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