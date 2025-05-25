package ch.hslu.mobpro.studyspot.ui.study

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ch.hslu.mobpro.studyspot.data.model.StudySpot
import ch.hslu.mobpro.studyspot.viewmodel.StudySpotViewModel
import ch.hslu.mobpro.studyspot.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudySearchScreen(
    studySpotViewModel: StudySpotViewModel,
    authViewModel: AuthViewModel
) {
    val studySpots by studySpotViewModel.studySpots.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showOnlyFree by remember { mutableStateOf(false) }
    var showOnlyGroupWork by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        studySpotViewModel.fetchStudySpots()
    }

    val favoriteSpotIds = currentUser?.favoriteStudySpotIds ?: emptyList()

    val filteredSpots = studySpots.filter { spot ->
        val matchesSearch = if (searchQuery.isBlank()) true else {
            spot.name.contains(searchQuery, ignoreCase = true) ||
                    spot.location.contains(searchQuery, ignoreCase = true)
        }
        val matchesFreeFilter = if (showOnlyFree) spot.isFree else true
        val matchesGroupWorkFilter = if (showOnlyGroupWork) spot.isGroupWorkAllowed else true
        matchesSearch && matchesFreeFilter && matchesGroupWorkFilter
    }

    val favoriteFilteredSpots = filteredSpots.filter { favoriteSpotIds.contains(it.id) }
    val nonFavoriteFilteredSpots = filteredSpots.filter { !favoriteSpotIds.contains(it.id) }

    val favoriteFreeSpots = favoriteFilteredSpots.filter { it.isFree }
    val favoriteNotFreeSpots = favoriteFilteredSpots.filter { !it.isFree }
    val nonFavoriteFreeSpots = nonFavoriteFilteredSpots.filter { it.isFree }
    val nonFavoriteNotFreeSpots = nonFavoriteFilteredSpots.filter { !it.isFree }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Study spots",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search for study spots...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true
        )

        // Filter chips (is better and easier to use i think honestly)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = showOnlyFree,
                onClick = { showOnlyFree = !showOnlyFree },
                label = { Text("Free") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Is Free",
                    )
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            FilterChip(
                selected = showOnlyGroupWork,
                onClick = { showOnlyGroupWork = !showOnlyGroupWork },
                label = { Text("Group Work") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Group Work Allowed",
                    )
                }
            )
        }

        Text(
            text = "${filteredSpots.size} study spots found",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            if (favoriteFilteredSpots.isNotEmpty()) {
                if (favoriteFreeSpots.isNotEmpty()) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Favorites",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Favorites - Free",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                items(favoriteFreeSpots) { spot ->
                    StudySpotItem(spot, isFavorite = true)
                }
            }


            if (nonFavoriteFreeSpots.isNotEmpty()) {
                item {
                    Text(
                        text = "Free",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }


            items(nonFavoriteFreeSpots) { spot ->
                StudySpotItem(spot, isFavorite = false)
            }

            if (!showOnlyFree && nonFavoriteNotFreeSpots.isNotEmpty()) {
                item {
                    Text(
                        text = "Not Free",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            items(favoriteNotFreeSpots) { spot ->
                StudySpotItem(spot, isFavorite = true)
            }

            items(nonFavoriteNotFreeSpots) { spot ->
                StudySpotItem(spot, isFavorite = false)
            }

            if (filteredSpots.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No study spots found matching your criteria",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudySpotItem(spot: StudySpot, isFavorite: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (spot.isGroupWorkAllowed) Color.Green else Color.Red)
                .padding(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = if (spot.isGroupWorkAllowed) "Group Work Enabled" else "Group Work Disabled",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = spot.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (isFavorite) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Text(
                text = spot.location,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row {
                if (spot.isGroupWorkAllowed) {
                    Text(
                        "Group Work",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "View Details",
        )
    }
    HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
}