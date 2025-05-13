package in2000.team42.ui.screens.guide.komponenter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import in2000.team42.R
import in2000.team42.ui.screens.settings.guide.installation.MonteringData
import in2000.team42.ui.screens.settings.guide.installation.installationSteps
import in2000.team42.ui.screens.settings.guide.komponenter.CustomRoundedBox

@Composable
fun Montering(){
    Column(
        modifier=Modifier.padding(16.dp)
    ){
        LazyColumn(
            modifier=Modifier.fillMaxWidth()
        ) {
            items(installationSteps) { steg ->
                ExpandableMonteringItem(monteringData = steg)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ExpandableMonteringItem(monteringData: MonteringData){
    var isExpanded by remember { mutableStateOf(false) }

    CustomRoundedBox(
        height = if (isExpanded) 200.dp else 45.dp,
        fontWeight = FontWeight.Bold
    ){
        Column(
            modifier = Modifier.padding(8.dp)
                .clickable { isExpanded = !isExpanded }
        ){
            Row(
                modifier=Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = stringResource(id = monteringData.questionResId),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text= if (isExpanded) "-" else "+",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            if (isExpanded){
                Text(
                    text = stringResource(id = monteringData.answerResId),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top=10.dp),
                )
            }
        }
    }
}