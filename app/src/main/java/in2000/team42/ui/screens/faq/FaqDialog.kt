package in2000.team42.ui.screens.faq


import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun FaqDialog(onDismiss:()->Unit){
   AlertDialog(
       onDismissRequest = onDismiss,
       title={Text("FAQ")},
       text={Text("Her finner du ofte stilte spørsmål")},
       confirmButton = {
           Button(onClick = onDismiss){
               Text("Lukk")
           }
       }
   )

}