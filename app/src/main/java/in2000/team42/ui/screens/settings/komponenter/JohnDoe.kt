package in2000.team42.ui.screens.settings.komponenter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun JohnDoe(){
    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
        )
        {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "ProfilePicture",
                tint = Color.Cyan,
                modifier = Modifier.size(50.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    text = "John Doe",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold

                )
                Text(
                    text = "Solar Enthusiast"
                )

            }


        }

    }

}