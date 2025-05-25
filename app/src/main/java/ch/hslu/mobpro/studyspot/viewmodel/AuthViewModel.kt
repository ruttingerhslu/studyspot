package ch.hslu.mobpro.studyspot.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.hslu.mobpro.studyspot.data.local.UserDao
import ch.hslu.mobpro.studyspot.data.local.StudySpotDao
import ch.hslu.mobpro.studyspot.data.model.User
import ch.hslu.mobpro.studyspot.data.model.StudySpot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userDao: UserDao,
    private val studySpotDao: StudySpotDao
) : ViewModel()
{
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _favoriteStudySpots = MutableStateFlow<List<StudySpot>>(emptyList())
    val favoriteStudySpots: StateFlow<List<StudySpot>> = _favoriteStudySpots

    fun setCurrentUser(user: User) {
        _currentUser.value = user
        loadFavoriteStudySpots()
    }

    private fun loadFavoriteStudySpots() {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user != null) {
                val favoriteSpots = mutableListOf<StudySpot>()
                for (spotId in user.favoriteStudySpotIds) {
                    val spot = studySpotDao.getStudySpotById(spotId)
                    spot?.let { favoriteSpots.add(it) }
                }
                _favoriteStudySpots.value = favoriteSpots
            }
        }
    }

    fun addFavoriteStudySpot(studySpot: StudySpot) {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user != null && !user.favoriteStudySpotIds.contains(studySpot.id)) {
                val updatedFavoriteIds = user.favoriteStudySpotIds + studySpot.id
                val updatedUser = user.copy(favoriteStudySpotIds = updatedFavoriteIds)
                userDao.updateUser(updatedUser)
                _currentUser.value = updatedUser
                loadFavoriteStudySpots()
            }
        }
    }

    fun removeFavoriteStudySpot(studySpotId: String) {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user != null) {
                val updatedFavoriteIds = user.favoriteStudySpotIds.filter { it != studySpotId }
                val updatedUser = user.copy(favoriteStudySpotIds = updatedFavoriteIds)
                userDao.updateUser(updatedUser)
                _currentUser.value = updatedUser
                loadFavoriteStudySpots()
            }
        }
    }

    fun register(name: String, email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val newUser = User(name = name, email = email, password = password)
                userDao.registerUser(newUser)
                setCurrentUser(newUser)
                onSuccess()
            } catch (e: Exception) {
                onError("User already exists or registration failed")
            }
        }
    }

    fun login(email: String, password: String, onSuccess: (User) -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            Log.d("Login", "Attempting login for $email")
            val user = userDao.login(email, password)
            if (user != null) {
                Log.d("Login", "Login successful for ${user.email}")
                onSuccess(user)
            } else {
                Log.e("Login", "Login failed: user not found")
                onError()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _currentUser.value = null
            _favoriteStudySpots.value = emptyList()
        }
    }

    fun updateUserProfile(name: String, email: String, location: String) {
        viewModelScope.launch {
            val user = currentUser.value
            if (user != null) {
                val updatedUser = user.copy(name = name, email = email, location = location)
                userDao.updateUser(updatedUser)
                _currentUser.value = updatedUser
            }
        }
    }
}