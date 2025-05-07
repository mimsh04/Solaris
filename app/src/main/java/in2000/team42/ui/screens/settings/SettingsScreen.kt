package in2000.team42.ui.screens.settings



import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import in2000.team42.ui.screens.settings.faq.FaqDialog
import in2000.team42.ui.screens.settings.komponenter.*

@Composable
fun SettingsScreen (navController: NavHostController,
                    modifier : Modifier = Modifier) {
    var showFAQ by remember { mutableStateOf(false) }


    Column(
        modifier = if (showFAQ) Modifier.fillMaxSize().blur(10.dp) else Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Innstillinger",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.Start)
            )
            Column {
                AnbefalteProdukter()
                LurerDuPaaNoe(navController, showFAQ, { showFAQ = it })
            }
        }
    }

 if(showFAQ){
     FaqDialog(onDismiss = { showFAQ = false })
 }
}









