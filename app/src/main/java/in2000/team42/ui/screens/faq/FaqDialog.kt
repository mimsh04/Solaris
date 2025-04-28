package in2000.team42.ui.screens.faq

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import in2000.team42.model.faq.faqItems

@Composable
fun FaqDialog(onDismiss:()->Unit){
    AlertDialog(
        onDismissRequest = onDismiss,
        title={
            Text(
                text= "FAQ",
                style= MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )},
        text={
            Column {
                LazyColumn (
                    modifier = Modifier.heightIn(max= 300.dp)
                        .fillMaxWidth()
                )
                {
                    items(faqItems){ faq->
                        FAQItem(faq =faq)

                    }
                }
            } },
        confirmButton = {
            Button(onClick = onDismiss){
                Text("Lukk", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    )

}