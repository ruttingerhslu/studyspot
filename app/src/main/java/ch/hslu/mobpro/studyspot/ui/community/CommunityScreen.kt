package ch.hslu.mobpro.studyspot.ui.community

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ch.hslu.mobpro.studyspot.viewmodel.AuthViewModel
import ch.hslu.mobpro.studyspot.viewmodel.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    authViewModel: AuthViewModel,
    communityViewModel: CommunityViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    LaunchedEffect(currentUser?.email) {
        currentUser?.email?.let { email ->
            communityViewModel.loadContactsForUser(email)
        }
    }
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val contactUsers by communityViewModel.contactUsers.collectAsState()

    val filteredContacts = remember(searchQuery, contactUsers) {
        if (searchQuery.isBlank()) contactUsers
        else contactUsers.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.email.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Community") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.AddCircle, contentDescription = "Add Contact")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search contacts") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredContacts) { contact ->
                    ContactItem(user = contact)
                }
            }
        }

        if (showAddDialog) {
            AddContactDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { email ->
                    currentUser?.email?.let { currentUserEmail ->
                        communityViewModel.addContactByEmail(currentUserEmail, email)
                        Toast.makeText(context, "Trying to add $email", Toast.LENGTH_SHORT).show()
                    }
                    showAddDialog = false
                }
            )
        }
    }
}
