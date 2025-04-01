package in2000.team42.ui.screens.settings



import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import in2000.team42.ui.screens.settings.komponenter.AnbefalteProdukter
import in2000.team42.ui.screens.settings.komponenter.JohnDoe
import in2000.team42.ui.screens.settings.komponenter.LurerDuPaaNoe
import in2000.team42.ui.screens.settings.komponenter.SolarPanelData

@Composable
fun SettingsScreen (navController: NavHostController,modifier : Modifier = Modifier) {

    Column(
        modifier=Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.padding(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            JohnDoe()

            Column {
                AnbefalteProdukter()

                LurerDuPaaNoe(navController)

                SolarPanelData()
            }

        }

    }

}







