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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ch.hslu.mobpro.studyspot.data.model.StudySpot
import ch.hslu.mobpro.studyspot.viewmodel.StudySpotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudySearchScreen(studySpotViewModel: StudySpotViewModel) {
    val studySpots by studySpotViewModel.studySpots.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showOnlyFree by remember { mutableStateOf(false) }
    var showOnlyGroupWork by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        studySpotViewModel.fetchStudySpots()
    }

    val filteredSpots = studySpots.filter { spot ->
        val matchesSearch = if (searchQuery.isBlank()) true else {
            spot.name.contains(searchQuery, ignoreCase = true) ||
                    spot.location.contains(searchQuery, ignoreCase = true)
        }
        val matchesFreeFilter = if (showOnlyFree) spot.isFree else true
        val matchesGroupWorkFilter = if (showOnlyGroupWork) spot.isGroupWorkAllowed else true
        matchesSearch && matchesFreeFilter && matchesGroupWorkFilter
    }

    val freeSpots = filteredSpots.filter { it.isFree }
    val notFreeSpots = filteredSpots.filter { !it.isFree }

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

        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = {
                isSearchActive = false
                focusManager.clearFocus()
            },
            active = isSearchActive,
            onActiveChange = { isActive ->
                isSearchActive = isActive
                if (!isActive && searchQuery.isBlank()) {
                    searchQuery = ""
                }
            },
            placeholder = { Text("Search for study spots...") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.clickable {
                        isSearchActive = false
                        focusManager.clearFocus()
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    listOf("Suurstoffi 1A", "Suurstoffi 2B", "Bibliothek", "Computer Lab")
                ) { suggestion ->
                    Text(
                        text = suggestion,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                searchQuery = suggestion
                                isSearchActive = false
                                focusManager.clearFocus()
                            }
                            .padding(16.dp)
                    )
                }
            }
        }

        // Filter (einfach so filterchips zum draufklicken, ist glaube ich am einfachsten)
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
                leadingIcon =
                {
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
                leadingIcon =
                    {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "Group Work Allowed",
                        )
                    })
        }

        // Results count
        Text(
            text = "${filteredSpots.size} study spots found",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Free spots section
        if (freeSpots.isNotEmpty()) {
            Text(
                text = "Free",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f, false)
            ) {
                items(freeSpots) { spot ->
                    StudySpotItem(spot)
                }
            }
        }

        if (!showOnlyFree && notFreeSpots.isNotEmpty()) {
            Text(
                text = "Not Free",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f, false)
            ) {
                items(notFreeSpots) { spot ->
                    StudySpotItem(spot)
                }
            }
        }

        if (filteredSpots.isEmpty()) {
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

@Composable
fun StudySpotItem(spot: StudySpot) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {} // maybe some kind of navigation to a study spot? or maybe its alright like this
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

        Column {
            Text(
                text = spot.name,
                style = MaterialTheme.typography.bodyLarge
            )

            //Maybe a small text for indication about the group work?
            /*
            if (spot.isGroupWorkAllowed) {
                Text(
                    text = "Group work allowed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Group work not allowed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }*/
        }

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "View Details",
        )
    }
    Divider(modifier = Modifier.padding(start = 56.dp))
}