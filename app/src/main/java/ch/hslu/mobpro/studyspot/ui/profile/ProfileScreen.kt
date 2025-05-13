package ch.hslu.mobpro.studyspot.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ch.hslu.mobpro.studyspot.viewmodel.AuthViewModel
import coil3.compose.rememberAsyncImagePainter

@Composable
fun ProfileScreen(authViewModel: AuthViewModel) {
    val user by authViewModel.currentUser.collectAsState()

    if (user != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(user?.profileImageUrl ?: ""),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Welcome, ${user?.name}!", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Email: ${user?.email}")
            Text("Location: ${user?.location ?: "Not set"}")

            Spacer(modifier = Modifier.height(12.dp))
            Text("Favorite Spots:", style = MaterialTheme.typography.titleMedium)

            // TODO
            Text("No favorite spots yet.")
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("User not logged in.")
        }
    }
}