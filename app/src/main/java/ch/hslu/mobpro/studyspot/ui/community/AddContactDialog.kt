package ch.hslu.mobpro.studyspot.ui.community

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun AddContactDialog(onDismiss: () -> Unit, onAdd: (String) -> Unit) {
    var email by remember { mutableStateOf("") }

    AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
        TextButton(onClick = { onAdd(email) }) {
            Text("Add")
        }
    },
    dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    },
    title = { Text("Add Contact") },
    text = {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true
        )
    }
    )
}
