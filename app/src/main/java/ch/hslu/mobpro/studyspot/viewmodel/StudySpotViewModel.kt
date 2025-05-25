package ch.hslu.mobpro.studyspot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.hslu.mobpro.studyspot.data.local.StudySpotDao
import ch.hslu.mobpro.studyspot.data.model.StudySpot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudySpotViewModel @Inject constructor(
    private val studySpotDao: StudySpotDao
) : ViewModel() {
    private val _studySpots = MutableStateFlow<List<StudySpot>>(emptyList())
    val studySpots: StateFlow<List<StudySpot>> = _studySpots.asStateFlow()

    init {
        fetchStudySpots()
    }

    fun fetchStudySpots() {
        viewModelScope.launch {
            studySpotDao.getAllStudySpots().collect { spots ->
                _studySpots.value = spots
            }
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