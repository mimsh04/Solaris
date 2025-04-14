package in2000.team42.ui.screens.settings



import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import in2000.team42.data.saved.SavedProjectEntity
import in2000.team42.ui.screens.faq.FaqDialog
import in2000.team42.ui.screens.settings.komponenter.*

@Composable
fun SettingsScreen (navController: NavHostController,
                    project: SavedProjectEntity?,
                    modifier : Modifier = Modifier) {
    var showFAQ by remember { mutableStateOf(false) }

    Box(
        modifier=Modifier
            .fillMaxSize()
    ){
        Column(
            modifier = if (showFAQ) Modifier.fillMaxSize().blur(10.dp) else Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.padding(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {

                Column {
                    JohnDoe()
                    Column {
                        ProjectContent(project)
                        AnbefalteProdukter()

                        LurerDuPaaNoe(navController, showFAQ, { showFAQ = it })

                        SolarPanelData()
                    }
                }

            }
        }

        if(showFAQ){
            FaqDialog(onDismiss = { showFAQ = false })
        }

    }


}







