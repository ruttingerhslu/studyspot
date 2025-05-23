package ch.hslu.mobpro.studyspot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.hslu.mobpro.studyspot.data.model.StudySpot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudySpotViewModel : ViewModel() {
    private val _studySpots = MutableStateFlow<List<StudySpot>>(emptyList())
    val studySpots: StateFlow<List<StudySpot>> = _studySpots.asStateFlow()

    fun fetchStudySpots() {
        viewModelScope.launch {
            // Mock data
            val spots = listOf(
                StudySpot("1", "Suurstoffi 1A, 433", "Building 1A", true, true),
                StudySpot("2", "Suurstoffi 1A, Bibliothek", "Building 1A", false, true),
                StudySpot("3", "Suurstoffi 1A, 421", "Building 1A", true, true),
                StudySpot("4", "Suurstoffi 1A, 420", "Building 1A", true, true),
                StudySpot("5", "Suurstoffi 1A, 305", "Building 1A", true, false),
                StudySpot("6", "Suurstoffi 1A, 306", "Building 1A", true, false),
                StudySpot("7", "Suurstoffi 1A, 307", "Building 1A", true, false),
                StudySpot("8", "Suurstoffi 1A, 308", "Building 1A", true, false),

                StudySpot("9", "Suurstoffi 2B, 101", "Building 2B", true, true),
                StudySpot("10", "Suurstoffi 2B, 102", "Building 2B", false, true),
                StudySpot("11", "Suurstoffi 2B, Computer Lab", "Building 2B", true, true),
                StudySpot("12", "Suurstoffi 2B, 205", "Building 2B", false, false),
                StudySpot("13", "Suurstoffi 2B, 206", "Building 2B", true, false),
                StudySpot("14", "Suurstoffi 2B, Conference Room", "Building 2B", true, false)
            )
            _studySpots.value = spots
        }
    }

    fun searchStudySpots(query: String, freeOnly: Boolean): List<StudySpot> {
        return _studySpots.value.filter { spot ->
            val matchesQuery = spot.name.contains(query, ignoreCase = true) ||
                    spot.location.contains(query, ignoreCase = true)
            val matchesFreeFilter = if (freeOnly) spot.isFree else true
            matchesQuery && matchesFreeFilter
        }
    }
}